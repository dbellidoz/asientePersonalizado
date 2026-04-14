package com.localagent;

import java.util.AbstractMap.SimpleEntry;
import java.util.List;

import com.localagent.RAG.RAGService;
import com.localagent.model.Chunk;

/**
 * Hello world!
 *
 */
public class App {

    public static void main(String[] args) {
        //EmbeddingService embeddingService = new EmbeddingService();
        /*double[] vector = embeddingService.embedInfo("luz natural en la calle");
        for (int i = 0; i < 5; i++) {
            System.out.println(vector[i]);
        }*/
        String ruta = "C:/proyectos/local-agent/src/resources/foto.pdf";
        /*DocumentLoader docLoader = new DocumentLoader();
        String contenido = docLoader.loadDocument(ruta);
        List< String> chunkList = docLoader.chunkText(contenido, 50, 10);
        double[] vectoresFichero;
        VectorStore vs = new VectorStore();
        for (int i = 0; i < chunkList.size(); i++) {
            String chunk = chunkList.get(i);
            vectoresFichero = embeddingService.embedInfo(chunk);
            Chunk nuevoChunk = new Chunk(vectoresFichero, chunk);
            vs.addNewChunkDocumento(nuevoChunk);
        }*/

        String consulta = "como puedo corregir el exceso de contraste";
        /*double[] embededRequest = embeddingService.embedInfo(consulta);

        List<SimpleEntry<Chunk, Double>> similaridades = vs.similarity(embededRequest, 10); */
        RAGService rag = new RAGService();
        rag.cargarVectorStore(ruta);
        List<SimpleEntry<Chunk, Double>> similaridades = rag.buscarChunk(consulta, 10);

        for (SimpleEntry<Chunk, Double> similaridad : similaridades){
            System.out.println(similaridad.getKey().getText() + " " +similaridad.getValue());
        }

        

        /*
         * for (String chunk : chunList){
         * 
         * System.out.println(chunk);
         * }
         */
    }
}
