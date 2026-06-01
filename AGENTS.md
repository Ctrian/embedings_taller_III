# AGENTS.md

## Project

Java Gradle multi-project workspace for LLM tokenization, embeddings, and chat APIs. Root `settings.gradle.kts` includes three subprojects.

## Subprojects

- **`01.embeddings`** — Custom tokenizer (V1/V2), BPE via jtokkit, embedding with DJL + LangChain4j (AllMiniLmL6V2 quantized, local ONNX). Has its own `settings.gradle.kts` (can build standalone).
- **`02.conexionAPI`** — LangChain4j OpenAI-compatible chat client, AiServices, function calling, semantic search. Targets a **local LLM server at `http://localhost:8080`** (llama.cpp or similar).
- **`03.SpringAI`** — Spring Boot 4 + Spring AI 2.0.0-M7. REST chat endpoint at `GET /chat?message=...`. Also points to `http://localhost:8080` for the LLM backend. Runs on port **8081** (not 8080).

## Build & Run

```sh
# Build all subprojects from repo root
./gradlew build

# Build a single subproject
./gradlew :01.embeddings:build
./gradlew :02.conexionAPI:build
./gradlew :03.SpringAI:build

# Run a Main class (no application plugins; use JavaExec or IDE)
./gradlew :01.embeddings:run   # if application plugin added
# Otherwise run via IDE or:
./gradlew -p 01.embeddings run
```

Tests use JUnit Platform (Jupiter). `02.conexionAPI` uses JUnit BOM 6.0.0.

## Critical Gotchas

- **Hardcoded absolute paths** — Several classes have Windows absolute paths that won't work on other machines:
  - `EmbeddingTest.java`: `C:/Av2/Taller_III/01.embeddings/the-verdict.txt`
  - `DataSampling.java`: `C:/Av2/Taller_III/01.embeddings/the-verdict.txt`
  - `EmbeddingModelMain.java`: `D:/Taller III llamaCPP/llama onnx/model.onnx` and `tokenizer.json`
  - `TestTokenizerMain.java`: `D:/Taller III/embedings/01.embeddings/the-verdict.txt` (correct for this repo)
  - Before running `EmbeddingTest` or `DataSampling`, update the `PATH` constant to match your machine.

- **Local LLM server required** — `02.conexionAPI` and `03.SpringAI` require a local OpenAI-compatible server on `localhost:8080`. The `apiKey` is set to `"cualquiera"` (any string works with llama.cpp). Without this server running, all chat/embedding calls will fail with connection refused.

- **Java version mismatch** — `03.SpringAI` declares Java toolchain 24 via Gradle, but the system has JDK 17. You must install JDK 24 or override the toolchain to run `03.SpringAI`.

- **No test suite** — `01.embeddings` and `03.SpringAI` have no test implementations despite `useJUnitPlatform()` being configured. All verification is via `main()` methods (run manually).

- **LangChain4j version split** — `01.embeddings` uses langchain4j `1.12.2`; `02.conexionAPI` uses `1.14.0`. The embedding artifact names also differ between versions (`langchain4j-embeddings-all-minilm-l6-v2-q` vs `langchain4j-embeddings-all-minilm-l6-v2`).

- **`03.SpringAI` uses Spring AI milestone repo** — Dependencies come from a milestone release (`2.0.0-M7`). May require `maven { url = "https://repo.spring.io/milestone" }` in repositories if resolution fails.
