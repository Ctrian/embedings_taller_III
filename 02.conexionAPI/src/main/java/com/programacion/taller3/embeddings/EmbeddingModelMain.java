package com.programacion.taller3.embeddings;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.onnx.OnnxEmbeddingModel;
import dev.langchain4j.model.embedding.onnx.PoolingMode;
import dev.langchain4j.model.output.Response;

import java.nio.file.Paths;
import java.util.Arrays;

public class EmbeddingModelMain {
    public static void main(String[] args) {
        var pathModel = Paths.get("D:/Taller III llamaCPP/llama onnx/model.onnx");
        var pathToTokenizer = Paths.get("D:/Taller III llamaCPP/llama onnx/tokenizer.json");

        OnnxEmbeddingModel model =new OnnxEmbeddingModel(pathModel, pathToTokenizer, PoolingMode.MEAN);

        Response<Embedding> response = model.embed("Hola como estas?");

        Embedding embedding = response.content();

        System.out.println("Dimensiones: " + embedding.vector().length);
        System.out.println(embedding);

    }
}
