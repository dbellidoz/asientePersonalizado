package com.localagent;

import java.util.List;

import com.localagent.RAG.DocumentLoader;
import com.localagent.RAG.EmbeddingService;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {
        EmbeddingService embeddingService = new EmbeddingService();
        double[] vector = embeddingService.embedInfo("luz natural en la calle");
        for (int i = 0; i < 5; i++) {
            System.out.println(vector[i]);
        }

        /*
         * String ruta = "C:/proyectos/local-agent/src/resources/foto.pdf";
         * DocumentLoader docLoader = new DocumentLoader();
         * String contenido = docLoader.loadDocument(ruta);
         * List<String> chunList = docLoader.chunkText(contenido, 50, 10);
         * for(int i = 0; i < 5; i++){
         * String chunk = chunList.get(i);
         * System.out.println("CHUNK N"+i+
         * "---------------------------------------------------------");
         * System.out.println(chunk);
         * }
         */
        /*
         * for (String chunk : chunList){
         * 
         * System.out.println(chunk);
         * }
         */

    }
}
