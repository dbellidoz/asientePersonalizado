package com.localagent.RAG;

import java.io.File;
import java.util.AbstractMap;
import java.util.List;

import com.localagent.model.Chunk;

public class RAGService {

    EmbeddingService embeddingService = new EmbeddingService();
    VectorStore vs = new VectorStore();

    private void cargarVectorStore(String ruta) {
        DocumentLoader docLoader = new DocumentLoader();

        String contenido = docLoader.loadDocument(ruta);
        List< String> chunkList = docLoader.chunkText(contenido, 800, 100);
        double[] vectoresFichero;
        String nombreFichero = new File(ruta).getName();
        for (int i = 0; i < chunkList.size(); i++) {
            String chunk = chunkList.get(i);
            vectoresFichero = embeddingService.embedInfo(chunk);
            Chunk nuevoChunk = new Chunk(vectoresFichero, chunk, nombreFichero);
            vs.addNewChunkDocumento(nuevoChunk);
        }
    }

    public void cargarDocumento(String ruta) {
        cargarVectorStore(ruta);
    }

    public void eliminarChunkFichero(String fichero){
        vs.removeChunkDocument(fichero);
    }

    public List<AbstractMap.SimpleEntry<Chunk, Double>> buscarChunk(String request, int nChunks) {
        double[] embeddedRequest = embeddingService.embedInfo(request);
        return vs.similarity(embeddedRequest, nChunks);
    }

    public void cargarDirectorio(String ruta) {
        File directory = new File(ruta);
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getPath().endsWith(".pdf")) {
                        cargarVectorStore(file.getPath());
                    }
                }
            }
        }
    }

}
