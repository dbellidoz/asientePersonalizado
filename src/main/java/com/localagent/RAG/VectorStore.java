package com.localagent.RAG;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;

import com.localagent.model.Chunk;

public class VectorStore {

    private List<Chunk> chunksList = new ArrayList<>();

    //Metodo que añade los chucks de consulta
    public void addNewChunkDocumento(Chunk chunk) {
        this.chunksList.add(chunk);
    }

    //Metodo privado que calcula la similaridad entre dos chunks (consulta - documentos)
    //Parametro 1: valores embebidos de la consulta
    //Parametro 2: valores embebidos de los documentos
    private double cosineSimilarity(double[] inputEmb, double[] chunksListEmb) {
        double puntoProducto = 0.0;
        double magnitudA = 0.0;
        double magnitudB = 0.0;

        //suma de los cuadrados de los valores embebidos
        for (int i = 0; i < inputEmb.length; i++) {
            puntoProducto += inputEmb[i] * chunksListEmb[i];
            magnitudA += inputEmb[i] * inputEmb[i];
            magnitudB += chunksListEmb[i] * chunksListEmb[i];
        }

        magnitudA = Math.sqrt(magnitudA);
        magnitudB = Math.sqrt(magnitudB);

        if (magnitudA == 0.0 || magnitudB == 0.0) {
            return 0.0;
        }

        return puntoProducto / (magnitudA * magnitudB);
    }

    //Metodo que reune recibe los embebbed de la consulta y el numero de chunks que quiere recuperar
    //Crea una lista con SimpleEntry (objetos que relacionan valores) con los elementos Chunk y Double
    //     Chunk (paquete model) es una clase que contiene un texto y un array de doubles con los embeddings correspondientes a ese texto
    //     El Double sera el valor de similaridad que se colculara con el metodo cosineSimilarity
    //Se ordenan por el indice de similaridad y se seleccionan los n primeros
    public List<AbstractMap.SimpleEntry<Chunk, Double>> similarity(double[] embInput, int howmanyChunks) {
        List<AbstractMap.SimpleEntry<Chunk, Double>> similaridades = new ArrayList<>();
        for (int i = 0; i < chunksList.size(); i++) {
            Double similaridad = cosineSimilarity(embInput, chunksList.get(i).getEmbedding());
            similaridades.add(new AbstractMap.SimpleEntry<>(chunksList.get(i), similaridad));
        }

        similaridades.sort((a, b) -> -1 * Double.compare(a.getValue(), b.getValue()));

        return similaridades.subList(0, howmanyChunks);
    }

}
