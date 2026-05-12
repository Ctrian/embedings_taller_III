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

        var text = "Hello, do you like tea? In the sunlit terraces of the palace";

        IntArrayList ids = tokenizer.encodeOrdinary(text);

        System.out.println("Texto original: " + text);
        System.out.println("Numero de tokens: " + ids.size());
        System.out.println("Encoded (IDs): " + ids);

        System.out.println("\nDecoded: " + tokenizer.decode(ids));
    }
}
