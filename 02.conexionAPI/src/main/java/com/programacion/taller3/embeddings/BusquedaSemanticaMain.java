package com.programacion.taller3.embeddings;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.store.embedding.CosineSimilarity;

import java.util.List;

public class BusquedaSemanticaMain {
    public static void main(String[] args) {
        var documentos = List.of(
                "Java is an object-oriented programming language",
                "Python is popular for artificial intelligence",
                "The coffee machine is in the kitchen",
                "Embeddings represent text as vectors"
        );

        var model = new AllMiniLmL6V2EmbeddingModel();

        List<Embedding> embeddings = documentos.stream()
                .map(model::embed)
                .map(Response::content)
                .toList();

        String consultar = "there are plates in the desk";
        Embedding query = model.embed(consultar).content();

        var index = 0;
        int mejorIndice = 0;
        double mejorScore = 0;
        for(var it:embeddings){
            double score = CosineSimilarity.between(query, it);

            System.out.printf("Documento %d - Similaridad: %.2f%n", index, score);
            if (score > mejorScore){
                mejorScore = score;
                mejorIndice = index - 1;
            }
            index++;
        }
        System.out.println("Mejor documento: " + documentos.get(mejorIndice + 1));
        System.out.println("Similaridad: " + mejorScore);
    }
}
