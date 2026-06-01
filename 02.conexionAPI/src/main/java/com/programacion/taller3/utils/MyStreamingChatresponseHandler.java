package com.programacion.taller3.utils;

import dev.langchain4j.model.chat.response.*;

import java.util.concurrent.atomic.AtomicInteger;

public class MyStreamingChatresponseHandler implements StreamingChatResponseHandler {

    public AtomicInteger count;

    public MyStreamingChatresponseHandler(AtomicInteger count) {
        this.count = count;
    }

    @Override
    public void onPartialResponse(String partialResponse) {
        StreamingChatResponseHandler.super.onPartialResponse(partialResponse);
    }

    @Override
    public void onPartialResponse(PartialResponse partialResponse, PartialResponseContext context) {
        StreamingChatResponseHandler.super.onPartialResponse(partialResponse, context);
    }

    @Override
    public void onPartialThinking(PartialThinking partialThinking) {
        StreamingChatResponseHandler.super.onPartialThinking(partialThinking);
    }

    @Override
    public void onPartialThinking(PartialThinking partialThinking, PartialThinkingContext context) {
        StreamingChatResponseHandler.super.onPartialThinking(partialThinking, context);
    }

    @Override
    public void onPartialToolCall(PartialToolCall partialToolCall) {
        StreamingChatResponseHandler.super.onPartialToolCall(partialToolCall);
    }

    @Override
    public void onPartialToolCall(PartialToolCall partialToolCall, PartialToolCallContext context) {
        StreamingChatResponseHandler.super.onPartialToolCall(partialToolCall, context);
    }

    @Override
    public void onCompleteToolCall(CompleteToolCall completeToolCall) {
        StreamingChatResponseHandler.super.onCompleteToolCall(completeToolCall);
    }

    @Override
    public void onCompleteResponse(ChatResponse completeResponse) {
        System.out.println("Respuesta completa: " + completeResponse);
        count.getAndIncrement();
    }

    @Override
    public void onError(Throwable error) {

    }
}
