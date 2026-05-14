package org.example;

import dev.langchain4j.service.AiServices;
import org.example.utils.AsistenteLegal;

public class ChatAiServiceMain {
    public static void main(String[] args) {
        var model = ChatMain.chatModel();

        var asistente = AiServices.create(AsistenteLegal.class, model);
        var respuesta = asistente.consultarPregunta("¿Cuáles son los requisitos para salir del pais con una beca en Ecuador?");
        System.out.println(respuesta);

        //-----------------

        respuesta = asistente.responder("¿Cuáles son los requisitos para salir del pais con una beca en Colombia?");
        System.out.println(respuesta);
    }
}
