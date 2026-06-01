package com.programacion.taller3;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Tokenizador básico tipo GPT (BPE simplificado) - Versión 1.
 *
 * Construye dos mapas a partir de un vocabulario (List<Pair>):
 * - strToInt: palabra → ID (para encode)
 * - intToStr: ID → palabra (para decode)
 *
 * LIMITACIÓN: si un token no existe en el vocabulario, se IGNORA
 * (filter(Objects::nonNull) elimina los null de strToInt.get()).
 * Esto significa que palabras desconocidas desaparecen del resultado.
 * Ver TokenizerV2 para la solución.
 */
public class TokenizerV1 {

    // Regex que divide el texto ANTES y DESPUÉS de cada delimitador
    // Lookahead (?=...) → corta antes del delimitador
    // Lookbehind (?<=...) → corta después del delimitador
    // Esto preserva la puntuación como tokens independientes
    // Ej: "Hello, world!" → ["Hello", ",", "world", "!"]
    public static final String regex = "(?=[,.:;?_!\"()']|--|\\s)|(?<=[,.:;?_!\"()']|--|\\s)";

    // Mapa palabra → ID: usado por encode() para convertir texto a números
    private Map<String, Integer> strToInt;

    // Mapa ID → palabra: usado por decode() para convertir números a texto
    private Map<Integer, String> intToStr;

    /**
     * Construye el tokenizador a partir de una lista de Pair(tokenId, token).
     *
     * @param vocab lista de pares generada por TestTokenizerMain.vocabulary()
     *
     * Internamente convierte la lista en dos mapas para búsqueda O(1):
     * strToInt = {"the" → 2, "," → 0, "." → 1, ...}
     * intToStr = {2 → "the", 0 → ",", 1 → ".", ...}
     */
    public TokenizerV1(List<Pair> vocab) {
        strToInt = vocab.stream()
                .collect(Collectors.toMap(Pair::token, Pair::tokenId));

        intToStr = vocab.stream()
                .collect(Collectors.toMap(Pair::tokenId, Pair::token));
    }

    /**
     * Codifica un texto en una lista de IDs numéricos.
     *
     * Pasos:
     * 1. split(regex) → divide el texto en tokens (palabras + puntuación)
     * 2. trim() → elimina espacios sobrantes de cada token
     * 3. filter(!isEmpty()) → elimina tokens vacíos
     * 4. strToInt.get(token) → busca el ID de cada token en el mapa
     * 5. filter(nonNull) → ELIMINA tokens no encontrados (devuelven null)
     *
     * PROBLEMA: el paso 5 hace que palabras desconocidas desaparezcan silenciosamente.
     * Si "Hello" no está en el vocabulario, simplemente no aparece en la lista de IDs.
     *
     * @param text texto a codificar
     * @return lista de IDs numéricos correspondientes a los tokens
     */
    public List<Integer> encode(String text) {
        return Arrays.stream(text.split(regex))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(token -> strToInt.get(token))
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Decodifica una lista de IDs numéricos de vuelta a texto.
     *
     * Pasos:
     * 1. intToStr.get(id) → busca la palabra para cada ID
     * 2. joining(" ") → une todos los tokens con espacio
     *
     * NOTA: la reconstrucción NO es idéntica al original:
     * - Original: "Hello, world!" → Decoded: "Hello , world !"
     * - Los espacios se insertan entre todos los tokens, incluyendo puntuación
     *
     * @param ids lista de IDs numéricos
     * @return texto reconstruido (tokens separados por espacio)
     */
    public String decode(List<Integer> ids) {
        return ids.stream()
                .map(id -> intToStr.get(id))
                .collect(Collectors.joining(" "));
    }
}
