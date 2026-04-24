package com.localagent.server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.localagent.utils.Constants;

public class ConfigManager {

    private static Gson gson = new Gson();

    // Carga el system prompt — si no existe config.json, crea uno con el prompt por defecto
    public static String loadSystemPrompt() throws IOException {
        if (Files.exists(Path.of(Constants.CONFIG_PATH))) {
            String contenido = Files.readString(Path.of(Constants.CONFIG_PATH));
            JsonObject json = gson.fromJson(contenido, JsonObject.class);
            String systemprompt = json.has("system_prompt") ? json.get("system_prompt").getAsString() : Constants.SYSTEM_PROMPT;
            return systemprompt;
        } else {
            JsonObject predeterminado = new JsonObject();
            predeterminado.addProperty("system_prompt", Constants.SYSTEM_PROMPT);
            new File(Constants.CONFIG_PATH).getParentFile().mkdirs();
            Path path = Files.createFile(Path.of(Constants.CONFIG_PATH));
            Files.writeString(path, gson.toJson(predeterminado));
            return Constants.SYSTEM_PROMPT;
        }
    }

    // Guarda el nuevo system prompt en config.json
    public static void saveSystemPrompt(String prompt) throws IOException {
        JsonObject json = new JsonObject();
        json.addProperty("system_prompt", prompt);
        Files.writeString(Path.of(Constants.CONFIG_PATH), gson.toJson(json));
    }
}