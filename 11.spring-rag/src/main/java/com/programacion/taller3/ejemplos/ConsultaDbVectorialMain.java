package com.programacion.taller3.ejemplos;

import io.qdrant.client.QdrantClient;
import io.qdrant.client.QdrantGrpcClient;
import io.qdrant.client.QueryFactory;
import io.qdrant.client.grpc.Points;
import org.springframework.ai.document.MetadataMode;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.transformers.TransformersEmbeddingModel;

import java.awt.*;
import java.util.List;

public class ConsultaDbVectorialMain {

    public ConsultaDbVectorialMain() throws Exception {
    }

    // la misma capacidad del modelo se debe poner para la base?
    static float[] embedd(String text) throws Exception {
        var embeddingModel = new TransformersEmbeddingModel(MetadataMode.ALL);
        embeddingModel.setModelResource("classpath:models/model.onnx");
        embeddingModel.setTokenizerResource("classpath:tokenizer/tokenizer.json");
        embeddingModel.afterPropertiesSet();

        EmbeddingRequest request = new EmbeddingRequest(List.of(text),  null);
        var response = embeddingModel.call(request);

        return response.getResults().getFirst().getOutput();
    }

public static void main(String[] args) throws Exception {
    QdrantClient client = new QdrantClient(
            QdrantGrpcClient.newBuilder("localhost", 6333, false).build()
    );

    String texto = "requisitos para titulacion";
    float[] point = embedd(texto);

    var querySpec = Points.QueryPoints.newBuilder()
            .setCollectionName("spring-ai")
            .setLimit(3)
            .setQuery(
                    QueryFactory.nearest(point)
            )
            .setWithPayload(
                    Points.WithPayloadSelector.newBuilder()
                            .setEnable(true)
                            .build()
            )
            .build();

    List<Points.ScoredPoint> results = client.queryAsync(querySpec).get();

    for(var it : results) {
        System.out.println("----------------------------------");
        System.out.println(it);
        var metadata = it.getPayloadMap();

        System.out.println("score: " + it.getScore());
        System.out.println(metadata.get("doc_content").toString().replace("\\n", System.lineSeparator()));
    }
}

}
