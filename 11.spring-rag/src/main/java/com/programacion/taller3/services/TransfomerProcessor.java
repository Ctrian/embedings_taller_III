package com.programacion.taller3.services;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TransfomerProcessor {

    public List<Document> procesar(List<Document> docs){
        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(800)
                .build();

        List<Document> splited = splitter.split(docs);
        System.out.println("TransformerProcessor :: documentos creados: " + splited.size());

        //System.out.println("pagina 0");
        //System.out.println(splited.getFirst());

        return splited;
    }
}
