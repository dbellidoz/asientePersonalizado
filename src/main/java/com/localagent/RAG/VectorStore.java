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
    
    public void addNewChunkDocumento(Chunk chunk){
        this.chunksList.add(chunk);
    }
/* 
    public double[] similarity(double[] embInput){
        List<AbstractMap.SimpleEntry<Chunk, Double>> similaridad = new ArrayList();
        

        
    }*/

    private double cosineSimilarity(double[] inputEmb, double[] chunksListEmb){
        double puntoProducto = 0.0;
        double magnitudA = 0.0;
        double magnitudB = 0.0;
        double similaridad = 0.0;

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
