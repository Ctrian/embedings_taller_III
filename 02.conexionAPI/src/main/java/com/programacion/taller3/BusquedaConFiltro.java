package com.programacion.taller3;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

/**
 * EJERCICIO P2 — Busqueda semantica con filtro de relevancia
 *
 * Consigna:
 * 1. Agrega 5 documentos a un InMemoryEmbeddingStore
 * 2. Busca con query, maxResults(3), minScore(0.5)
 * 3. Imprime solo resultados que pasen el filtro
 * 4. Para cada resultado: texto, score, y si es relevante
 *
 * Puedes usar referencia de InMemoryEmbeddingStoreMain.java
 * pero NO lo copies directamente.
 */
public class BusquedaConFiltro {

    public static void main(String[] args) {

        // TODO: 1. Crear el modelo de embedding
        // AllMiniLmL6V2EmbeddingModel model = ...



        // TODO: 2. Crear el InMemoryEmbeddingStore
        // InMemoryEmbeddingStore<TextSegment> store = ...



        // TODO: 3. Definir 5 documentos y agregarlos al store
        // String[] documentos = {
        //     "El gato duerme en el sofa",
        //     "Los perros juegan en el parque",
        //     "La cocina tiene platos y tazas",
        //     "El automovil esta en el garaje",
        //     "Los animales necesitan comida y agua"
        // };
        // Para cada uno: crear TextSegment, embed, y store.add(...)



        // TODO: 4. Definir query y buscar
        // String query = "mascotas domesticas";
        // Embedding queryEmbedding = model.embed(query).content();
        // EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
        //     .queryEmbedding(queryEmbedding)
        //     .maxResults(3)
        //     .minScore(0.5)
        //     .build();



        // TODO: 5. Imprimir resultados
        // EmbeddingSearchResult<TextSegment> result = store.search(request);
        // for (EmbeddingMatch<TextSegment> match : result.matches()) {
        //     System.out.println("Texto: " + match.embedded().text());
        //     System.out.println("Score: " + match.score());
        //     System.out.println("Relevante: " + (match.score() >= 0.5 ? "SI" : "NO"));
        //     System.out.println("---");
        // }



    }
}
