package org.example;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;

import java.util.Scanner;

interface Conversator {
    String chat(String message);
}

public class ChatMemoryMain {
    public static void main(String[] args) {
        var model = ChatMain.chatModel();

        ChatMemory memory = MessageWindowChatMemory
                .builder()
                .maxMessages(10)
                .build();

        var bot = AiServices
                .builder(Conversator.class)
                .chatMemory(memory)
                .chatModel(model)
                .build();

        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.print("mensaje: ");
            String message = sc.nextLine();

            if ("exit".equalsIgnoreCase(message)) {
                break;
            }

            var respuesta = bot.chat(message);
            System.out.println("respuesta: " + respuesta);

        }
    }
}
