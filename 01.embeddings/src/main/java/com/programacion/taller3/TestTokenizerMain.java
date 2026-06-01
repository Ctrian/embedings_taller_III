package com.programacion.taller3;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Clase principal que prueba el sistema de tokenización V1 y V2.
 *
 * Flujo general:
 * 1. Genera un vocabulario a partir de un archivo de texto (the-verdict.txt)
 * 2. Crea un Tokenizer con ese vocabulario
 * 3. Prueba encode (texto → IDs) y decode (IDs → texto)
 *
 * El vocabulario se construye extrayendo todas las palabras únicas del texto,
 * ordenándolas alfabéticamente y asignándoles IDs secuenciales (0, 1, 2, ...).
 * Cada entrada del vocabulario es un Pair(tokenId, token).
 */
public class TestTokenizerMain {

    public static final String PATH = "D:/Taller III/embedings/01.embeddings/the-verdict.txt";

    /**
     * Genera un vocabulario BÁSICO a partir del archivo de texto.
     *
     * Pasos:
     * 1. Lee todo el texto del archivo
     * 2. Divide el texto en tokens usando la regex (separa palabras y puntuación, conservando ambos)
     * 3. Limpia: trim() elimina espacios sobrantes, filter elimina strings vacíos
     * 4. distinct() obtiene solo palabras únicas, sorted() las ordena alfabéticamente
     * 5. Asigna IDs secuenciales empezando en 0
     * 6. Retorna List<Pair> donde cada Pair es (ID, palabra)
     *
     * Ejemplo: si el texto tiene "the", "cat", "sat" → vocab = [(0, "cat"), (1, "sat"), (2, "the")]
     * (ordenado alfabéticamente, IDs secuenciales)
     */
    public static List<Pair> vocabulary(String filename) throws Exception {
        // Paso 1: Leer archivo completo
        String raw_text = Files.readString(Path.of(filename));

        // Paso 2: Regex que divide ANTES y DESPUÉS de puntuación/espacios
        // Lookahead (?=...) → divide antes del delimitador (la coma queda como token separado)
        // Lookbehind (?<=...) → divide después del delimitador
        // Resultado: "Hello, world!" → ["Hello", ",", "world", "!"]
        String regex = "(?=[,.:;?_!\"()']|--|\\s)|(?<=[,.:;?_!\"()']|--|\\s)";
        var tokens = raw_text.split(regex);

        // Paso 3: Limpiar tokens - quitar espacios y vacíos
        var preprocessed = Stream.of(tokens)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        // Paso 4: Obtener palabras únicas ordenadas alfabéticamente
        // distinct() elimina duplicados, sorted() ordena
        var all_words = preprocessed.stream()
                .distinct()
                .sorted()
                .toList();

        // Paso 5: Asignar IDs secuenciales (0, 1, 2, ...) a cada palabra
        // AtomicInteger es necesario porque los lambdas no permiten modificar
        // variables locales (counter++ no compila dentro de un lambda)
        AtomicInteger counter = new AtomicInteger(0);
        return all_words.stream()
                .map(it -> new Pair(counter.getAndIncrement(), it))
                .toList();
    }

    /**
     * Genera un vocabulario EXTENDIDO a partir del archivo de texto.
     *
     * Igual que vocabulary(), pero agrega dos tokens especiales al final:
     * - <|endoftext|> : marca el fin de un documento (separa textos distintos)
     * - <|unk|>       : reemplaza palabras desconocidas que no están en el vocabulario
     *
     * Estos tokens son necesarios para TokenizerV2, que maneja:
     * - Múltiples documentos separados por <|endoftext|>
     * - Palabras fuera del vocabulario reemplazadas por <|unk|>
     */
    public static List<Pair> vocabularioExtendido(String filename) throws Exception {
        // Paso 1: Leer archivo completo
        String raw_text = Files.readString(Path.of(filename));

        // Paso 2: Misma regex para dividir en tokens
        String regex = "(?=[,.:;?_!\"()']|--|\\s)|(?<=[,.:;?_!\"()']|--|\\s)";
        var tokens = raw_text.split(regex);

        // Paso 3: Limpiar tokens
        var preprocessed = Stream.of(tokens)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        // Paso 4: Palabras únicas ordenadas
        // Usamos Collectors.toList() en lugar de .toList() porque necesitamos
        // una lista MUTABLE (toList() retorna una lista inmutable)
        var all_words = preprocessed.stream()
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        // Paso 5: Agregar tokens especiales al vocabulario
        // <|endoftext|> se usa como separador entre documentos
        // <|unk|> reemplaza palabras que no existen en el vocabulario
        all_words.add("<|endoftext|>");
        all_words.add("<|unk|>");

        // Paso 6: Asignar IDs secuenciales (los tokens especiales quedan al final)
        AtomicInteger counter = new AtomicInteger(0);
        return all_words.stream()
                .map(it -> new Pair(counter.getAndIncrement(), it))
                .toList();
    }

    /**
     * Método principal que demuestra el flujo completo del sistema de tokenización.
     *
     * FLUJO:
     * PARTE 1 - TokenizerV1:
     *   vocabulary() → genera vocabulario básico → TokenizerV1 → encode/decode
     *   Problema: las palabras desconocidas se IGNORAN (desaparecen del resultado)
     *
     * PARTE 2 - TokenizerV2:
     *   vocabularioExtendido() → genera vocabulario con tokens especiales → TokenizerV2 → encode/decode
     *   Ventaja: las palabras desconocidas se reemplazan por <|unk|>, no se pierden
     */
    public static void main(String[] args) throws Exception {

        // ============================================
        // PARTE 1: Prueba de TokenizerV1 (básico)
        // ============================================

        // Genera vocabulario básico a partir del archivo the-verdict.txt
        // vocab = [(0, ","), (1, "."), (2, "the"), (3, "and"), ...]
        var vocab = vocabulary(PATH);

        // Muestra los primeros 51 tokens del vocabulario para inspección
        System.out.println("=== Primeros 51 tokens del vocabulario básico ===");
        vocab.stream()
                .takeWhile(it -> it.tokenId() < 51)
                .forEach(System.out::println);

        // Texto de prueba: contiene comillas, comas, puntos, apóstrofes
        var text = "\"It's the last he painted, you know,\" Mrs. Gisburn said with pardonable pride.";

        // Crea el tokenizador V1 con el vocabulario básico
        // Internamente construye dos mapas: palabra→ID y ID→palabra
        TokenizerV1 tokenizer = new TokenizerV1(vocab);

        // encode: convierte el texto en una lista de IDs numéricos
        // Ej: "the" → 2, "last" → 270, "," → 0, etc.
        // NOTA: si una palabra no está en el vocabulario, se IGNORA (filter nonNull)
        var ids = tokenizer.encode(text);
        System.out.println("\nTexto original: " + text);
        System.out.println("Encoded (IDs): " + ids);

        // decode: convierte la lista de IDs de vuelta a texto
        // Une los tokens con espacio: "It ' s the last he painted , you know ,\" Mrs . Gisburn ..."
        // OJO: la reconstrucción no es idéntica al original (sin espacios antes de puntuación)
        System.out.println("Decoded: " + tokenizer.decode(ids));

        // ============================================
        // PARTE 2: Prueba de TokenizerV2 (extendido)
        // ============================================

        // Genera vocabulario extendido con <|endoftext|> y <|unk|>
        var vocabExt = vocabularioExtendido(PATH);

        // Muestra los últimos tokens (incluye los especiales)
        // skip(vocab.size() - 5) salta todos excepto los últimos 5
        // NOTA: usa vocab.size() (básico) como referencia, pero vocabExt tiene 2 más
        System.out.println("\n=== Últimos tokens del vocabulario extendido (incluye especiales) ===");
        vocabExt.stream()
                .skip(vocab.size() - 5)
                .forEach(System.out::println);

        // Dos textos independientes que se van a combinar en uno solo
        // "Hello" NO está en el vocabulario → en V1 se ignoraría, en V2 se reemplaza por <|unk|>
        var text1 = "Hello, do you like tea?";
        var text2 = "In the sunlit terraces of the palace.";

        // Se combinan con <|endoftext|> como separador entre documentos
        // El doble espacio es para que la regex separe "<|endoftext|>" como token propio
        var textCombinado = text1 + "  <|endoftext|>  " + text2;

        // Crea tokenizador V2 con el vocabulario extendido
        // La diferencia clave: getOrDefault(token, strToInt.get("<|unk|>"))
        // Si un token no existe → retorna el ID de <|unk|> en vez de null
        TokenizerV2 tokenizer2 = new TokenizerV2(vocabExt);
        var ids2 = tokenizer2.encode(textCombinado);

        // "Hello" → ID de <|unk|> (no está en el vocabulario)
        // "<|endoftext|>" → ID de <|endoftext|>
        // El resto de palabras → sus IDs correspondientes
        System.out.println("\nTexto combinado: " + textCombinado);
        System.out.println("Encoded V2: " + ids2);
        System.out.println("Decoded V2: " + tokenizer2.decode(ids2));
    }
}
