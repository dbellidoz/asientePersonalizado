package com.localagent.RAG;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.localagent.model.Chunk;

public class VectorStore {
    private List<Chunk> chunksList = new ArrayList<>();
    private List<Chunk> chunksInput;
    
    //Metodo que añade los chucks de consulta
    public void addNewChunkDocumento(Chunk chunk){
        this.chunksList.add(chunk);
    }
 
    public List<AbstractMap.SimpleEntry<Chunk, Double>> similarity(double[] embInput){
        List<AbstractMap.SimpleEntry<Chunk, Double>> similaridades = new ArrayList<>();
        for (int i = 0; i < chunksList.size(); i++) {
            Double similaridad = cosineSimilarity(embInput, chunksList.get(i).getEmbedding());
            similaridades.add(new AbstractMap.SimpleEntry<>(chunksList.get(i), similaridad));
        }

        return similaridades;
    }

    //Metodo privado que calcula la similaridad entre dos chunks (consulta - documentos)
    //Parametro 1: valores embebidos de la consulta
    //Parametro 2: valores embebidos de los documentos
    private double cosineSimilarity(double[] inputEmb, double[] chunksListEmb){
        double puntoProducto = 0.0;
        double magnitudA = 0.0;
        double magnitudB = 0.0;
        double similaridad = 0.0;

        //suma de los cuadrados de los valores embebidos
        for (int i = 0; i < inputEmb.length; i++) {
            puntoProducto += inputEmb[i] * chunksListEmb[i];
            magnitudA += inputEmb[i] * inputEmb[i];
            magnitudB += chunksListEmb[i] * chunksListEmb[i];
        }

        magnitudA = Math.sqrt(magnitudA);
        magnitudB = Math.sqrt(magnitudB);

        similaridad = puntoProducto/(magnitudA * magnitudB);

        return similaridad;
    }


}
