package com.programacion.taller3.utils;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface AsistenteLegal {
    String responder(String pregunta);

    @SystemMessage("Eres un asistente legal experto en derecho civil. Responde a las preguntas de manera clara y concisa, proporcionando información relevante y precisa sobre temas legales relacionados con el derecho civil.")
    @UserMessage("Responde a la pregunta utilizando argumentos legales y referencias a leyes o casos relevantes. Si no tienes suficiente información para responder, indícalo claramente: {{pregunta}}")

    String consultarPregunta(@V("pregunta") String pregunta);
}