package com.programacion.taller3;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public ChatMemory chatMemory() {
        // El builder utiliza por defecto almacenamiento en memoria (RAM)
        // maxMessages(20) indica que recordará los últimos 20 mensajes de la conversación
        return MessageWindowChatMemory.builder()
                .maxMessages(20)
                .build();
    }

}