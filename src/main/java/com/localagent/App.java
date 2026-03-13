package com.localagent;

import java.util.List;

import com.localagent.RAG.DocumentLoader;



/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {
        String ruta = "C:/proyectos/local-agent/src/resources/foto.pdf";
        DocumentLoader docLoader = new DocumentLoader();
        String contenido = docLoader.loadDocument(ruta);
        List<String> chunList = docLoader.chunkText(contenido, 50, 10);
        for(int i = 0; i < 5; i++){
            String chunk = chunList.get(i);
            System.out.println("CHUNK N"+i+"---------------------------------------------------------");
            System.out.println(chunk);
        }
        /*for (String chunk : chunList){

            System.out.println(chunk);
        }*/

    }
}
