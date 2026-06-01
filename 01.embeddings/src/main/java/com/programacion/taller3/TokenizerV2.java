package com.programacion.taller3;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Tokenizador extendido tipo GPT (BPE simplificado) - Versión 2.
 *
 * Mejora respecto a V1: maneja tokens DESCONOCIDOS usando el token especial <|unk|>.
 *
 * En V1: strToInt.get(token) retorna null para palabras desconocidas → se eliminan
 * En V2: strToInt.getOrDefault(token, strToInt.get("<|unk|>")) retorna el ID de <|unk|>
 *
 * Esto permite que el modelo sepa que había una palabra ahí, aunque no la conozca.
 * El vocabulario debe incluir <|unk|> y  (agregados por vocabularioExtendido()).
 */
public class TokenizerV2 {

    // Misma regex que V1: divide preservando puntuación como tokens independientes
    public static final String regex = "(?=[,.:;?_!\"()']|--|\\s)|(?<=[,.:;?_!\"()']|--|\\s)";

    private Map<String, Integer> strToInt;
    private Map<Integer, String> intToStr;

    /**
     * Construye el tokenizador a partir de un vocabulario extendido.
     *
     * @param vocab lista de pares generada por TestTokenizerMain.vocabularioExtendido()
     *              Debe contener los tokens especiales <|unk|> y 
     */
    public TokenizerV2(List<Pair> vocab) {
        strToInt = vocab.stream()
                .collect(Collectors.toMap(Pair::token, Pair::tokenId));

        intToStr = vocab.stream()
                .collect(Collectors.toMap(Pair::tokenId, Pair::token));
    }

    /**
     * Codifica un texto en una lista de IDs numéricos.
     *
     * Diferencia clave con V1:
     * - V1: strToInt.get(token) → null si no existe → se ignora
     * - V2: strToInt.getOrDefault(token, strToInt.get("<|unk|>")) → ID de <|unk|> si no existe
     *
     * Ejemplo: si "Hello" no está en el vocabulario:
     * - V1: "Hello" → null → eliminado → [ID_comma, ID_do, ID_you, ...]
     * - V2: "Hello" → ID_unk → preservado → [ID_unk, ID_comma, ID_do, ID_you, ...]
     *
     * El filter(Objects::nonNull) se mantiene como seguridad, pero en V2
     * casi nunca debería filtrar algo (solo si <|unk|> no estuviera en el mapa).
     *
     * @param text texto a codificar
     * @return lista de IDs numéricos (con <|unk|> para palabras desconocidas)
     */
    public List<Integer> encode(String text) {
        return Arrays.stream(text.split(regex))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(token -> strToInt.getOrDefault(token, strToInt.get("<|unk|>")))
                .filter(Objects::nonNull)
                .toList();
    }

    /**
     * Decodifica una lista de IDs numéricos de vuelta a texto.
     *
     * Idéntico a V1: busca cada ID en el mapa intToStr y une con espacios.
     * Los IDs de <|unk|> se decodificarán como "<|unk|>" en el texto resultante.
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
