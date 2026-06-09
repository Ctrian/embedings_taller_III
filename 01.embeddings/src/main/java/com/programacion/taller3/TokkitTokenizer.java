package com.programacion.taller3;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.IntArrayList;
import com.knuddels.jtokkit.api.ModelType;

public class TokkitTokenizer {

    public static void main(String[] args) {
        EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
        Encoding tokenizer = registry.getEncodingForModel(ModelType.GPT_4);
        Encoding tokenizer0 = registry.getEncodingForModel(ModelType.TEXT_DAVINCI_003);

        var text = "Hello<|endoftext|>World";

        IntArrayList ids = tokenizer.encodeOrdinary(text);

        System.out.println("Texto original: " + text);
        System.out.println("Numero de tokens: " + ids.size());
        System.out.println("Encoded (IDs): " + ids);

        System.out.println("\nDecoded: " + tokenizer.decode(ids));

        System.out.println("\nTokens individuales:");
        System.out.println(tokenizer);
        System.out.println(tokenizer0);
    }
}
