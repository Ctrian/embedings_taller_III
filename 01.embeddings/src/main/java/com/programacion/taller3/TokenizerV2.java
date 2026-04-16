package com.programacion.taller3;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class TokenizerV2 {

    public static final String regex = "(?=[,.:;?_!\"()']|--|\\s)|(?<=[,.:;?_!\"()']|--|\\s)";

    private Map<String, Integer> strToInt;
    private Map<Integer, String> intToStr;

    public TokenizerV2(List<Pair> vocab) {
        strToInt = vocab.stream()
                .collect(Collectors.toMap(Pair::token, Pair::tokenId));

        intToStr = vocab.stream()
                .collect(Collectors.toMap(Pair::tokenId, Pair::token));
    }

    // encode: si el token no existe → usa el ID de "<|unk|>"
    public List<Integer> encode(String text) {
        return Arrays.stream(text.split(regex))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(token -> strToInt.getOrDefault(token, strToInt.get("<|unk|>")))
                .filter(Objects::nonNull)
                .toList();
    }

    // decode: igual que V1
    public String decode(List<Integer> ids) {
        return ids.stream()
                .map(id -> intToStr.get(id))
                .collect(Collectors.joining(" "));
    }
}