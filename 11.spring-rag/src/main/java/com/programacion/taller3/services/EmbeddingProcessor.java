package com.programacion.taller3.services;

import com.openai.services.blocking.EmbeddingService;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmbeddingProcessor {

    @Autowired
    VectorStore vectorStore;

    @Autowired
    EmbeddingModel embeddingModel;

    public void procesar(List<Document> docs) {
        System.out.println("EmbeddingProcessor + procesamiento de docs: " + docs.size());

        System.out.println("EmbeddingModel: " + embeddingModel);
        System.out.println("VectorStore: " + vectorStore);
    }

}
