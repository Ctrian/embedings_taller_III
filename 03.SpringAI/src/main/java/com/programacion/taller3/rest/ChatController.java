package com.programacion.taller3.rest;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChatController {

    final ChatClient client;

    public ChatController(ChatClient.Builder builder) {
        client = builder.build();
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String message) {
        return client.prompt()
                .user(message)
                .call()
                .content();
    }

}
