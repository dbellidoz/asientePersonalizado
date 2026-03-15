package com.localagent.model;

public class Chunk {
    private double[] embedding;
    private String text;
    
    public double[] getEmbedding() {
        return embedding;
    }

    public void setEmbedding(double[] embedding) {
        this.embedding = embedding;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Chunk(double[] embedding, String text) {
        this.embedding = embedding;
        this.text = text;
    }
    
    

}
