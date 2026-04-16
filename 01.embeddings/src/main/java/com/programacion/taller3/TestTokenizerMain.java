package com.programacion.taller3;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestTokenizerMain {

    public static final String PATH = "D:/Taller III/embedings/01.embeddings/the-verdict.txt";

    // Vocabulario básico: solo palabras del texto
    public static List<Pair> vocabulary(String filename) throws Exception {
        String raw_text = Files.readString(Path.of(filename));

        String regex = "(?=[,.:;?_!\"()']|--|\\s)|(?<=[,.:;?_!\"()']|--|\\s)";
        var tokens = raw_text.split(regex);

        var preprocessed = Stream.of(tokens)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        var all_words = preprocessed.stream()
                .distinct()
                .sorted()
                .toList();

        AtomicInteger counter = new AtomicInteger(0);
        return all_words.stream()
                .map(it -> new Pair(counter.getAndIncrement(), it))
                .toList();
    }

    // Vocabulario extendido: agrega tokens especiales <|unk|> y <|endoftext|>
    public static List<Pair> vocabularioExtendido(String filename) throws Exception {
        String raw_text = Files.readString(Path.of(filename));

        String regex = "(?=[,.:;?_!\"()']|--|\\s)|(?<=[,.:;?_!\"()']|--|\\s)";
        var tokens = raw_text.split(regex);

        var preprocessed = Stream.of(tokens)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        // Usamos Collectors.toList() para poder modificar la lista
        var all_words = preprocessed.stream()
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // Agregamos tokens especiales al final del vocabulario
        all_words.add("<|endoftext|>");
        all_words.add("<|unk|>");

        AtomicInteger counter = new AtomicInteger(0);
        return all_words.stream()
                .map(it -> new Pair(counter.getAndIncrement(), it))
                .toList();
    }

    public static void main(String[] args) throws Exception {

        // --- Prueba TokenizerV1 ---
        var vocab = vocabulary(PATH);

        System.out.println("=== Primeros 51 tokens del vocabulario básico ===");
        vocab.stream()
                .takeWhile(it -> it.tokenId() < 51)
                .forEach(System.out::println);

        var text = "\"It's the last he painted, you know,\" Mrs. Gisburn said with pardonable pride.";
        TokenizerV1 tokenizer = new TokenizerV1(vocab);

        var ids = tokenizer.encode(text);
        System.out.println("\nTexto original: " + text);
        System.out.println("Encoded (IDs): " + ids);
        System.out.println("Decoded: " + tokenizer.decode(ids));

        // --- Prueba TokenizerV2 ---
        var vocabExt = vocabularioExtendido(PATH);

        System.out.println("\n=== Últimos tokens del vocabulario extendido (incluye especiales) ===");
        vocabExt.stream()
                .skip(vocab.size() - 5)
                .forEach(System.out::println);  // ← forEach con F mayúscula

        var text1 = "Hello, do you like tea?";
        var text2 = "In the sunlit terraces of the palace.";
        var textCombinado = text1 + " <|endoftext|> " + text2;

        TokenizerV2 tokenizer2 = new TokenizerV2(vocabExt);
        var ids2 = tokenizer2.encode(textCombinado);

        System.out.println("\nTexto combinado: " + textCombinado);
        System.out.println("Encoded V2: " + ids2);
        System.out.println("Decoded V2: " + tokenizer2.decode(ids2));
    }
}