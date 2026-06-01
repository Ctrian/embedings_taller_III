package com.programacion.taller3;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.model.openai.OpenAiChatModel;

/**
 * EJERCICIO P3 — Asistente matematico con multiples tools
 *
 * Consigna:
 * 1. Crea HerramientasMatematicas con 3 @Tool: sumar, multiplicar, raizCuadrada
 * 2. Crea interfaz AsistenteMatematico con @SystemMessage
 * 3. Construye AiService con tools
 * 4. Pregunta: "Cuanto es 15 * 8 + la raiz cuadrada de 144?"
 *
 * NOTA: Requiere servidor LLM en localhost:8080.
 * Si no tienes uno, al menos completa el codigo para que compile.
 */

// TODO: Define la interfaz AsistenteMatematico
// @SystemMessage("Eres un asistente matematico experto...")
// interface AsistenteMatematico { ... }


// TODO: Define la clase HerramientasMatematicas con 3 metodos @Tool
// class HerramientasMatematicas { ... }


public class AsistenteMatematicoMain {

    public static void main(String[] args) {

        // TODO: 1. Crear el ChatModel
        // OpenAiChatModel model = OpenAiChatModel.builder()
        //     .baseUrl("http://localhost:8080/v1")
        //     .apiKey("cualquiera")
        //     .modelName("gpt-3.5-turbo")
        //     .build();



        // TODO: 2. Construir el AiService con tools
        // AsistenteMatematico asistente = AiServices.builder(AsistenteMatematico.class)
        //     .chatModel(model)
        //     .tools(new HerramientasMatematicas())
        //     .build();



        // TODO: 3. Hacer la pregunta
        // String pregunta = "Cuanto es 15 * 8 + la raiz cuadrada de 144?";
        // String respuesta = asistente.preguntar(pregunta);
        // System.out.println("Respuesta: " + respuesta);



    }
}
