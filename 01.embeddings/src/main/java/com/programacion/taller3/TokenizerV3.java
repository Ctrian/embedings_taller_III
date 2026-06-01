package com.programacion.taller3;

import java.util.*;
import java.util.regex.Pattern;

/**
 * EJERCICIO P1 — TokenizerV3
 *
 * Consigna: Mejora TokenizerV2 con:
 * 1. decode() sin espacios extra antes de puntuacion ("Hello, world" no "Hello ,  world")
 * 2. encodeWithCount(text) -> Map<String, Integer> con frecuencia de cada token
 * 3. vocabularySize() -> int con el tamano del vocabulario
 *
 * NO mires TokenizerV1 ni V2. Intenta desde cero.
 */
public class TokenizerV3 {

    private final Map<String, Integer> strToInt;
    private final Map<Integer, String> intToStr;

    private static final Pattern pattern = Pattern.compile(
            "(?=[,.:;?_!\"()']|--|\\s)|(?<=[,.:;?_!\"()']|--|\\s)"
    );

    // PISTA: El constructor recibe List<Pair> vocabulario
    // y construye los dos mapas bidireccionales

    public TokenizerV3(List<Pair> vocabulary) {
        this.strToInt = new HashMap<>();
        this.intToStr = new HashMap<>();

        // TODO: Construye los mapas a partir del vocabulario


    }

    public List<Integer> encode(String text) {
        // TODO: Implementa encode
        // Split con regex, buscar cada token en strToInt
        // Si no existe, usar getOrDefault con <|unk|>


        return List.of();
    }

    public String decode(List<Integer> ids) {
        // TODO: Implementa decode MEJORADO
        // Mapear IDs a strings, unirlos
        // PERO: eliminar espacios extra antes de puntuacion
        // PISTA: revisa si el token actual es puntuacion antes de agregar espacio


        return "";
    }

    public Map<String, Integer> encodeWithCount(String text) {
        // TODO: Cuenta cuantas veces aparece cada token
        // PISTA: usa HashMap con merge() o compute()


        return Map.of();
    }

    public int vocabularySize() {
        // TODO: Devuelve el tamano del vocabulario


        return 0;
    }
}
