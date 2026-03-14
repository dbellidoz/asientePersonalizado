package com.localagent.RAG;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EmbeddingService {
    private final String OLLAMA_URL = "http://localhost:11434";
    private final String DIRECTION = "/api/embed";
    private final String MODEL = "nomic-embed-text";

    private final OkHttpClient CLIENT = new OkHttpClient();
    private final Gson GSON = new Gson();

    private String fullURL = OLLAMA_URL.concat(DIRECTION);

    private double[] embedInfo(String info) {
        JsonObject payload = new JsonObject();
        payload.addProperty("model", MODEL);
        payload.addProperty("input", info);

        RequestBody body = RequestBody.create(GSON.toJson(payload), MediaType.get("application/json"));

        Request req = new Request.Builder().url(fullURL).post(body).build();

        try (Response response = CLIENT.newCall(req).execute()) {
            
            okhttp3.ResponseBody respBody = response.body();
            String responseBody = respBody != null ? respBody.string() : "";
            JsonObject responseJSON = GSON.fromJson(responseBody, JsonObject.class);
            JsonArray responseMessage = responseJSON.getAsJsonArray("embeddings").get(0).getAsJsonArray();
            double[] arrayResponse = new double[responseMessage.size()];
            for (int i = 0; i < arrayResponse.length; i++) {
                arrayResponse[i] = responseMessage.get(i).getAsDouble();
            }
            return arrayResponse;
        }catch (Exception e) {
            System.out.print("Error: ".concat(e.getMessage()));
            return null;
        }

    }

}
