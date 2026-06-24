package com.programacion.taller3.rest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.util.StreamUtils;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.io.IOException;
import java.io.UncheckedIOException;

@RestController
public class ChatController {

    final ChatClient chatClient;

    @Value("classpath:/prompts/systemPrompt.st")
    Resource systemPrompt;

    @Value("classpath:/prompts/userPrompt.st")
    Resource userPrompt;

    @Value("classpath:/prompts/programacion/distribuida.md")
    Resource silabo;

    public ChatController(ChatClient.Builder builder) {
        chatClient = builder
                .defaultAdvisors(
                        new SimpleLoggerAdvisor()
                )
                .build();
    }

    private String readResource(Resource resource) {
        try {
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @PostMapping(value = "/chat", consumes = "application/json", produces = "text/plain")
    public String chat(@RequestBody ChatRequest request) {
        return chatClient.prompt()
                .system(systemSpec -> systemSpec
                        .text(systemPrompt)
                        .param("silabo", readResource(silabo)))
                .user(userSpec -> userSpec
                        .text(userPrompt)
                        .param("question", request.message()))
                .call()
                .content();

    }


    @PostMapping(path = "/api/chat", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamChat(@RequestBody ChatRequest request) {
        var message = request.message();

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("El usuario no puede estar vacio");
        }

        Flux<ServerSentEvent<String>> tokens = chatClient.prompt()
                .system(systemSpect -> systemSpect
                        .text(systemPrompt)
                        .param("silabo", readResource(silabo))
                )
                .user(userSpect -> userSpect
                        .text(userPrompt)
                        .param("question", request.message())
                )
                .stream()
                .content()
                .map(chunk-> ServerSentEvent.<String>builder(chunk)
                        .event("token")
                        .data(
                                Base64.getEncoder().encodeToString(chunk.getBytes(StandardCharsets.UTF_8))
                        )
                        .build()
                );

        Flux<ServerSentEvent<String>> done = Flux.just(
                ServerSentEvent.<String>builder()
                        .event("done")
                        .data("[DONE]")
                        .build()
        );

        return tokens.concatWith(done)
                .onErrorResume(error -> Flux.just(
                        ServerSentEvent.<String>builder()
                                .event("error")
                                .data(error.getMessage())
                                .build()
                ));
    }

}
