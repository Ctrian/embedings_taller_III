package com.programacion.taller3;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.IntArrayList;
import com.knuddels.jtokkit.api.ModelType;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2q.AllMiniLmL6V2QuantizedEmbeddingModel;
import dev.langchain4j.model.output.Response;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class EmbeddingTest {
    public static final String PATH = "C:/Av2/Taller_III/01.embeddings/the-verdict.txt";

    public static void main(String[] args) throws Exception {
        String raw_text = Files.readString(Path.of(PATH))
                .lines().reduce(String::concat).orElse("");

        EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
        // Cambiar el modelo puede producir un número diferente de tokens, ya que cada modelo tiene su propio vocabulario y reglas de tokenización
        Encoding tokenizer = registry.getEncodingForModel(ModelType.TEXT_DAVINCI_003);

        var enc_text = tokenizer.encode(raw_text);
        var enc_text_boxed = enc_text.boxed();
        var enc_sample = enc_text_boxed.subList(50, enc_text_boxed.size());

        System.out.println("Tokens count: " + enc_text.size());

        //-----------

        int contextSize = 4;
        var x = enc_sample.subList(0, 4);
        var y = enc_sample.subList(1, contextSize + 1);

        System.out.println(x);
        System.out.println(" " + y);

        IntArrayList inputTokens = new IntArrayList();
        x.forEach(inputTokens::add);

        IntArrayList targetTokens = new IntArrayList();
        y.forEach(targetTokens::add);

        System.out.println(tokenizer.decode(inputTokens));
        System.out.println("    " + tokenizer.decode(targetTokens));

        //-----------
        // generacion de par (input target)
        List<DatasetItem> dataset = new ArrayList<>();

        List<Integer> tokensIds = tokenizer.encode(raw_text).boxed();

        int maxLength = 4;

        IntStream.range(0, tokensIds.size() - maxLength)
                .forEach(i -> {
                    var inputChunk = tokensIds.subList(i, i + maxLength);
                    var targetChunk = tokensIds.subList(i + 1, i + maxLength + 1);

                    dataset.add(new DatasetItem(inputChunk, targetChunk));
                });

        //-----------
        // Prueba de Embedding
        int vocabSize = 50257;
        int putputDim = 256;

        try (NDManager manager = NDManager.newBaseManager()) {
            NDArray weights = manager.randomUniform(
                    -1.0f,
                    1.0f,
                    new Shape(vocabSize, putputDim));

            // ahora lo que debemos hacer es pasar el dataset (la libreria trabaja con Long)
            AtomicInteger count = new AtomicInteger(0);

            dataset
                    .forEach(item -> {
                        var input = item.input().stream()
                                .mapToLong(Integer::longValue)
                                .toArray();

                        NDArray indices = manager.create(input);
                        NDArray embedding = weights.get(indices);
                        System.out.println(embedding);
                        System.out.println("------------------------------");
                        System.out.println("Input indices: " + Arrays.toString(input));
                        System.out.println("Embedding output shape: " + embedding);
                    });
        }

        // uso de LangChain4j para el uso de modelos de embedding preentrenados
        // motor: AllMiniLmL6V2QuantizedEmbeddingModel
        EmbeddingModel embeddingModel = new AllMiniLmL6V2QuantizedEmbeddingModel();
        var text = "Hello, how are you?";

        Response<Embedding> response = embeddingModel.embed(text);
        float[] vector = response.content().vector();

        System.out.println("Embedding size: " + vector.length);
        System.out.println(Arrays.toString(vector));
    }
}
