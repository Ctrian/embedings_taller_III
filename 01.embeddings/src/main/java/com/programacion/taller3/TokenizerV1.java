package com.programacion.taller3;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class TokenizerV1 {

    public static final String regex = "(?=[,.:;?_!\"()']|--|\\s)|(?<=[,.:;?_!\"()']|--|\\s)";

    private Map<String, Integer> strToInt;
    private Map<Integer, String> intToStr;

    public TokenizerV1(List<Pair> vocab) {
        // Construye dos mapas: palabra→id  e  id→palabra
        strToInt = vocab.stream()
                .collect(Collectors.toMap(Pair::token, Pair::tokenId));

        intToStr = vocab.stream()
                .collect(Collectors.toMap(Pair::tokenId, Pair::token));
    }

    // encode: texto → lista de IDs numéricos
    public List<Integer> encode(String text) {
        return Arrays.stream(text.split(regex))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(token -> strToInt.get(token))   // busca el ID de cada token
                .filter(Objects::nonNull)             // ignora tokens no encontrados
                .toList();
    }

    // decode: lista de IDs → texto reconstruido
    public String decode(List<Integer> ids) {
        return ids.stream()
                .map(id -> intToStr.get(id))          // ← aquí estaba el lambda vacío
                .collect(Collectors.joining(" "));    // une con espacio
    }
}