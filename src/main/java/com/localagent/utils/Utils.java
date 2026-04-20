package com.localagent.utils;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.stream.Collectors;

import com.localagent.model.Chunk;

public class Utils {

    public static String trimming(String text) {
        return text == null || "".equals(text) ? "" : text.replaceAll("\\s+", "");
    }

    public static boolean isEmptyString(String text) {
        return trimming(text).length() == 0;
    }

    public static String fillContext(List<SimpleEntry<Chunk, Double>> similaridades) {
        String context = "";
        if (!similaridades.isEmpty()) {
            context = "Usa este contexto: \n".concat(similaridades.stream().map(s -> "Fuente: " + s.getKey().getSource() + "\n" + s.getKey().getText())
                    .collect(Collectors.joining("\n\n")));
        }
        return context;
    }

}
