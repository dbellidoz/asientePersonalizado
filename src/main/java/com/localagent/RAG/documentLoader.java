package com.localagent.RAG;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;


public class DocumentLoader {

    //Metodo que saca todo el texto de un PDF
    public String loadDocument(String ruta) {
        try (PDDocument documento = Loader.loadPDF(new File(ruta))) {
            PDFTextStripper textStripper = new PDFTextStripper();
            String texto = textStripper.getText(documento);
            return texto;
        }catch(IOException io){
            System.out.println("ERROR: "+io.getMessage());
            return null;
        } 
    }

    //Divide el texto en chunks con una medida y un solapamiento determinado
    public List<String> chunkText(String texto, int medidaChunk, int solapamiento){
        int incrementos = medidaChunk - solapamiento;
        List<String> chunks = new ArrayList<>();
        for(int i = 0; i<texto.length(); i+=incrementos){
            String chunk = texto.substring(i, Math.min(i+medidaChunk,texto.length()));
            chunks.add(chunk);            
        }
        return chunks;
    }


}
