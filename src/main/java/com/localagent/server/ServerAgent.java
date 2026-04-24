package com.localagent.server;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.localagent.RAG.RAGService;
import com.localagent.agent.Agent;
import com.localagent.model.Chunk;
import com.localagent.utils.Constants;
import com.localagent.utils.Utils;

import io.javalin.Javalin;

public class ServerAgent {

    private static final int PORT = 8080;

    private final Agent agent;
    private final RAGService ragService;
    ;
    private final Javalin app;
    private final Gson gson = new Gson();

    public ServerAgent(Agent agent, RAGService ragService) {
        // inicializar campos
        this.agent = agent;
        this.ragService = ragService;
        // cargar directorio
        ragService.cargarDirectorio(Constants.DOCS_ROUTE);
        // definir endpoints
        // arrancar Javalin
        app = Javalin.create();
        app.get("/health", ctx -> ctx.result("ok"));
        app.post("/chat", ctx -> {
            String request = ctx.body();
            JsonObject bodyjson = gson.fromJson(request, JsonObject.class);
            String mensaje = bodyjson.get("mensaje").getAsString();
            String sessionId = bodyjson.get("sessionId").getAsString();
            List<SimpleEntry<Chunk, Double>> similaridades = ragService.buscarChunk(mensaje, 5);
            boolean areChunksRelevant = !similaridades.isEmpty();
            String respuesta = areChunksRelevant ? agent.chat(mensaje, Utils.fillContext(similaridades)) : Constants.DISCLAIMER.concat(agent.chat(mensaje, ""));
            JsonObject payload = new JsonObject();

            payload.addProperty("respuesta", respuesta);

            ctx.json(gson.toJson(payload));
            ctx.contentType("application/json");

        });
        app.post("/config", ctx -> {
            String request = ctx.body();
            JsonObject body = gson.fromJson(request, JsonObject.class);
            String system_prompt = body.get("system_prompt").getAsString();
            JsonObject payload  = new JsonObject();
            try {
                ConfigManager.saveSystemPrompt(system_prompt);
                agent.setSystem_prompt(system_prompt);
                payload.addProperty("status", "OK");
                payload.addProperty("mensaje", "Prompt de sistema modificado con éxito");
            } catch (Exception e) {
                payload.addProperty("status", "KO");
                payload.addProperty("mensaje", e.getMessage());
                ctx.status(500);
            }finally{
                ctx.json(gson.toJson(payload));
                ctx.contentType("application/json");
            }

        });
        app.start(PORT);

    }
}
