package com.programacion.taller3.routers;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FileReaderRouter extends RouteBuilder {

    // nosotros definimos esta ruta
    @Value("${app.files.inbound:D:/TestTallerIII/SpringAi}")
    String inbound;

    @Override
    public void configure() throws Exception {
        String from = "file:%s?antInclude=*.pdf&delay=1000&move=procesados".formatted(inbound);

        from(from)
                .log("Archivo leido:${header.CamelFileName}")
                .bean("fileProcesor", "procesar")
                .bean("transfomerProcessor", "procesar")
                .bean("embeddingProcessor", "procesar")
                //to("direct:processFile")
        ;
    }
}
