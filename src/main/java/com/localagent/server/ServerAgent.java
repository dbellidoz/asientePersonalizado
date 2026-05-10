package com.localagent.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
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
        //cargar prompt persistido
        try {
            String prompt = ConfigManager.loadSystemPrompt();
            agent.setSystem_prompt(prompt);
        } catch (Exception e) {
            System.out.println("Error cargando el config: " + e.getMessage());
        }
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
        app.get("/chat/history", ctx->{
            JsonObject response = new JsonObject();
            response.add("mensajes", agent.getHistory());
            ctx.json(gson.toJson(response));
            ctx.contentType("application/json");
        });
        app.post("/config", ctx -> {
            String request = ctx.body();
            JsonObject body = gson.fromJson(request, JsonObject.class);
            String system_prompt = body.get("system_prompt").getAsString();
            JsonObject payload = new JsonObject();
            try {
                ConfigManager.saveSystemPrompt(system_prompt);
                agent.setSystem_prompt(system_prompt);
                payload.addProperty("status", "OK");
                payload.addProperty("mensaje", "Prompt de sistema modificado con éxito");
            } catch (Exception e) {
                payload.addProperty("status", "KO");
                payload.addProperty("mensaje", e.getMessage());
                ctx.status(500);
            } finally {
                ctx.json(gson.toJson(payload));
                ctx.contentType("application/json");
            }

        });
        app.get("/config", ctx -> {
            String respuesta;
            JsonObject json = new JsonObject();
            try {
                respuesta = ConfigManager.loadSystemPrompt();
                json.addProperty("system_prompt", respuesta);
            } catch (IOException e) {
                respuesta = "Error cargando el prompt del fichero: " + e.getMessage();
                json.addProperty("error", respuesta);
                System.out.println(respuesta);
                ctx.status(500);
            } finally {
                ctx.json(gson.toJson(json));
                ctx.contentType("application/json");
            }

        });
        app.post("/documents", ctx -> {
            String respuesta = null;
            JsonObject json = new JsonObject();
            try {
                var fichero = ctx.uploadedFile("fichero");
                if (fichero == null || fichero.content() == null) {
                    throw new IllegalArgumentException("No se ha recibido ningún archivo");
                }
                Path targetPath = Path.of(Constants.DOCS_ROUTE, fichero.filename());
                Files.createDirectories(targetPath.getParent());
                Files.copy(fichero.content(), targetPath);
                ragService.cargarDocumento(targetPath.toString());

                respuesta = "OK";

            } catch (Exception e) {
                respuesta = "NOK: error en cargando el documento: " + e.getMessage();
                ctx.status(500);
            } finally {
                json.addProperty("returnValue", respuesta);
                ctx.json(gson.toJson(json));
                ctx.contentType("application/json");
            }

        });
        app.delete("/documents/{fichero}", ctx -> {
            String respuesta = null;
            JsonObject json = new JsonObject();
            try {
                String fichero = ctx.pathParam("fichero");
                Files.delete(Path.of(Constants.DOCS_ROUTE, fichero));
                ragService.eliminarChunkFichero(fichero);
                respuesta = "OK";
            } catch (Exception e) {
                respuesta = "NOK: error en cargando el documento: " + e.getMessage();
                ctx.status(500);
            } finally {
                json.addProperty("returnValue", respuesta);
                ctx.json(gson.toJson(json));
                ctx.contentType("application/json");
            }

        });
        app.get("/documents", ctx -> {
            String respuesta = null;
            JsonObject json = new JsonObject();
            try (Stream<Path> documentos = Files.list(Path.of(Constants.DOCS_ROUTE))){
                JsonArray jsonArray = new JsonArray();
                documentos.map(pathdoc -> pathdoc.getFileName().toString())
                        .filter(nombreFichero -> nombreFichero.endsWith("pdf"))
                        .forEach(doc -> jsonArray.add(doc));
                json.add("documentos", jsonArray);
                respuesta = "OK";
            } catch (Exception e) {
                respuesta = "NOK: error al listar los documentos : " + e.getMessage();
                ctx.status(500);
            } finally {
                json.addProperty("returnValue", respuesta);
                ctx.json(gson.toJson(json));
                ctx.contentType("application/json");
            }

        });
        app.post("/reset", ctx -> {
            String respuesta = null;
            JsonObject json = new JsonObject();
            agent.clearHistory();
            respuesta = "ok";
            json.addProperty("returnValue", respuesta);
            ctx.json(gson.toJson(json));
            ctx.contentType("application/json");
        });
        app.start(PORT);

    }
}
