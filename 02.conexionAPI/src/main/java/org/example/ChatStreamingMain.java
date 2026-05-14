package org.example;

import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import org.example.utils.MyStreamingChatresponseHandler;

import java.util.concurrent.atomic.AtomicInteger;

public class ChatStreamingMain {
    public static void main(String[] args) {
        AtomicInteger counter = new AtomicInteger(1);

        var chatModel = OpenAiStreamingChatModel
                .builder()
                .apiKey("cualquiera")
                .modelName("gpt-3.5-turbo")
                .baseUrl("http://localhost:8080")
                .logRequests(true)
                .logResponses(true)
                .build();

        chatModel.chat("¿Cuál es la capital de Francia?", new MyStreamingChatresponseHandler(counter));

        while(counter.get() > 0) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
