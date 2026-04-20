package com.localagent;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import com.localagent.RAG.RAGService;
import com.localagent.agent.Agent;
import com.localagent.model.Chunk;
import com.localagent.utils.Utils;

/**
 * Hello world!
 *
 */
public class App {
    private static final String DOCS_DIR = "C:/proyectos/local-agent/src/resources";
    private static final String DISCLAIMER = "no se encontró información relevante en los documentos, la respuesta está basada en el conocimiento del modelo de lenguaje y podría contener errores \n";
    public static void main(String[] args) {
        
        
        String consulta ="Que consejo me darías para empezar con la foto callejera";
        RAGService rag = new RAGService();
        Agent agent = new Agent();
        boolean areChunksRelevant;
        String respuesta;
        rag.cargarDirectorio(DOCS_DIR);
        List<SimpleEntry<Chunk, Double>> similaridades = rag.buscarChunk(consulta, 5);
        areChunksRelevant = !similaridades.isEmpty();
        respuesta = areChunksRelevant ? agent.chat(consulta, Utils.fillContext(similaridades)) : DISCLAIMER.concat(agent.chat(consulta,""));
        System.out.println(respuesta);
        for (SimpleEntry<Chunk, Double> similaridad : similaridades){
            System.out.println(similaridad.getKey().getText() + " " +similaridad.getValue());
        }
    }
}
