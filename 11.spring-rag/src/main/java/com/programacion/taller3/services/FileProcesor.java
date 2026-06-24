package com.programacion.taller3.services;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Component
public class FileProcesor {

    public List<Document> procesar(File file) {
        Resource resource = new FileSystemResource(file);
        // PagePdfDocumentReader reader = new PagePdfDocumentReader(res); /* esta madre no lee bien si no es .pdf */

        /* esta madre lee todo al parecer */
        TikaDocumentReader reader = new TikaDocumentReader(resource);

        List<Document> docs = reader.get();

        System.out.println("FileProcesor + procesamiento de file: " + file.getName() + " docs: " + docs.size());
        System.out.println("pagina 0");

        System.out.println(docs.getFirst());

        return docs;
    }
}
