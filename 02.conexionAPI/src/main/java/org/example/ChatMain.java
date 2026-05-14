package org.example;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class ChatMain {

    //modelo local: llama-3.2-1b-instruct-q8_0.gguf

    public static ChatModel chatModel() {
        return OpenAiChatModel
                .builder()
                .apiKey("cualquiera")
                .modelName("gpt-3.5-turbo")
                .baseUrl("http://localhost:8080")
                .logRequests(true)
                .logResponses(true)
                .build();
    }

    public static void main(String[] args) {
        ChatModel model = ChatMain.chatModel();

        var respuesta = model.chat("Que es langchain4j?");
        System.out.println("Respuesta: " + respuesta);
    }
}