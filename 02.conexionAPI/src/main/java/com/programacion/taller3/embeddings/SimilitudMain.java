package com.programacion.taller3.embeddings;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.CosineSimilarity;

public class SimilitudMain {
    public static void main(String[] args) {
        AllMiniLmL6V2EmbeddingModel model = new AllMiniLmL6V2EmbeddingModel();

        Embedding embedding1 = model.embed("Me gustan los perros").content();
        Embedding embedding2 = model.embed("Amo a los caninos").content();
        Embedding embedding3 = model.embed("El clima esta soleado").content();

        System.out.println("dimension del modelo AllMiniLmL6V2: " +
                embedding1.vector().length
        );

        // Calculamos la similitud con el coseno
        double sim1 = CosineSimilarity.between(embedding1, embedding2);
        double sim2 = CosineSimilarity.between(embedding1, embedding3);

        System.out.println("perros vs caninos: " + sim1);
        System.out.println("perros vs clima: " + sim2);

    }
}
