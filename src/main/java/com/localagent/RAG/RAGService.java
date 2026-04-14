package com.localagent.RAG;

import java.util.AbstractMap;
import java.util.List;

import com.localagent.model.Chunk;

public class RAGService {

    EmbeddingService embeddingService = new EmbeddingService();
    VectorStore vs = new VectorStore();

    public void cargarVectorStore (String ruta){
        DocumentLoader docLoader = new DocumentLoader();
        
        String contenido = docLoader.loadDocument(ruta);
        List< String> chunkList = docLoader.chunkText(contenido, 50, 10);
        double[] vectoresFichero;
        
        for (int i = 0; i < chunkList.size(); i++) {
            String chunk = chunkList.get(i);
            vectoresFichero = embeddingService.embedInfo(chunk);
            Chunk nuevoChunk = new Chunk(vectoresFichero, chunk);
            vs.addNewChunkDocumento(nuevoChunk);
        }

        //return vs;
    }

    public List<AbstractMap.SimpleEntry<Chunk, Double>> buscarChunk(String request, int nChunks){
        double[] embeddedRequest = embeddingService.embedInfo(request);
        return vs.similarity(embeddedRequest, nChunks);
    }

}
