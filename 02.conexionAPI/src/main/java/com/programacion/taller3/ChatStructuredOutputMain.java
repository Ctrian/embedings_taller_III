package com.programacion.taller3;

import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

record Persona(String nombre, int edad, String ciudad) {
}

interface Extractor {
    @UserMessage("Extrae el nombre, edad y ciudad de este texto y devuelve SOLO un JSON válido en este formato:\n" +
            "{\"nombre\": \"...\", \"edad\": número, \"ciudad\": \"...\"}\n" +
            "\n" +
            "Texto: {{texto}}")
    Persona extractPersona(@V("texto") String message);
}

public class ChatStructuredOutputMain {

    public static void main(String[] args) {
        var model = ChatMain.chatModel();

        Extractor extractor = AiServices
                .builder(Extractor.class)
                .chatModel(model)
                .build();

        Persona persona = extractor.extractPersona("Mi nombre es Juan, tengo 30 años y vivo en Madrid.");
        System.out.println("Persona extraida: " + persona);
    }
}
