package com.programacion.taller3;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;

import java.time.LocalDateTime;

class Herramientas {
    @Tool("Obtiene la hora y la fecha actual")
    String obtenerHoraFechaActual() {
        return LocalDateTime.now().toString();
    }

    @Tool("Calcula el área de un círculo dado su radio")
    String calcularAreaCirculo1(double radio) {
        double area = Math.PI * Math.pow(radio, 2);
        return "El área del círculo con radio " + radio + " es: " + area;
    }

    // que hace @P??
    @Tool("Calcula el área de un círculo dado su radio")
    double calcularAreaCirculo2(@P("El radio del círculo") double radio) {
        return Math.PI * radio * radio;
    }

}

interface AsistenteConTools {
    @SystemMessage("Eres un asistente que puede usar herramientas para responder preguntas.")
    String responderPregunta(String pregunta);
}

public class FunctionCallingMain {
    public static void main(String[] args) {
        var model = ChatMain.chatModel();

        var service = AiServices.builder(AsistenteConTools.class)
                .chatModel(model)
                .tools(new Herramientas())
                .build();

        var respuesta = service.responderPregunta("que hora y fecha es?");
        System.out.println(respuesta);
    }
}
