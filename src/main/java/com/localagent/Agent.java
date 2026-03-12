package com.localagent;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Agent {

    private static final String OLLAMA_URL = "http://localhost:11434";
    private static final String MODEL = "mistral";
    private static final String SYSTEM_PROMPT = "Eres un experto en fotografia especializado en streetphotography y tu obsesión es capturar "
            + "la esencia de los momentos cotidianos, creando arte en lo costumbrista y tu objetivo es ayudar a "
            + "los usuarios a mejorar y dar consejos criticos";

    // cada mensaje es un objeto con rol y content
    private final List<Message> historial = new ArrayList<>();
    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();

    public Agent() {
        //AL iniciar el agente haremos que primero lea el SYSTEM_PROMPT.
        historial.add(new Message("system", SYSTEM_PROMPT));
    }

    public String chat(String userMessage) {
        //Se añade el mensaje al historial
        historial.add(new Message("user", userMessage));

        //Se construye el JSON con el historial
        JsonObject payload = new JsonObject();
        payload.addProperty("model", MODEL);
        payload.addProperty("stream", false);

        //JSON de mensajes:
        JsonArray mensajes = new JsonArray();
        for (Message m : historial) {
            JsonObject msg = new JsonObject();
            msg.addProperty("role", m.role);
            msg.addProperty("content", m.content);
            mensajes.add(msg);
        }

        payload.add("messages", mensajes);

        //Hacer llamada HTTP a Ollama
        RequestBody body = RequestBody.create(
            gson.toJson(payload),
            MediaType.get("application/json")
        );

        Request request = new Request.Builder().url(OLLAMA_URL).post(body).build();
        
        try (Response response = client.newCall(request).execute() ){
            String responseBody = response.body().string();
            JsonObject responseJSON = gson.fromJson(responseBody, JsonObject.class);
            String responseMessage = responseJSON.getAsJsonObject("message").get("content").getAsString();

            //Se añade la respuesta al historial (contexto)
            historial.add(new Message("assistant", responseMessage));

            return responseMessage;
        } catch (Exception e) {
            return "Error: ".concat(e.getMessage());
        }
    }

    // Clase interna para representar un mensaje
    // Es private porque nadie fuera de Agent necesita conocerla
    private static class Message {

        String role;
        String content;

        Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}
