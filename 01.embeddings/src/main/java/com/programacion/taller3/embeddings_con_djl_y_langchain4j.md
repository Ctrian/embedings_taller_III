# Notas de Estudio: Funcionamiento de Embeddings en Java (`EmbeddingTest.java`)

Este documento sirve como guía de estudio para comprender cómo se procesa el texto, se tokeniza y se convierte en representaciones vectoriales (embeddings) utilizando tres librerías principales: **jtokkit**, **Deep Java Library (DJL)** y **LangChain4j**.

---

## 1. Tokenización con `jtokkit`
La tokenización es el proceso de dividir un texto en unidades más pequeñas llamadas "tokens" (que pueden ser palabras, subpalabras o caracteres).

### Conceptos clave en el código:
- **`EncodingRegistry`**: Actúa como un catálogo de tokenizadores. Se usa para obtener el esquema de codificación específico de un modelo.
- **`Encoding`**: Es el objeto que realiza la conversión. En el código se usa `ModelType.TEXT_DAVINCI_003`, lo que significa que el texto se dividirá siguiendo las reglas de OpenAI.
- **`tokenizer.encode(raw_text)`**: Convierte el `String` en una lista de enteros (`IntArrayList`). Cada entero es el ID único de ese token en el vocabulario del modelo.
- **`tokenizer.decode(inputTokens)`**: Proceso inverso; toma los IDs numéricos y los convierte nuevamente a texto legible.

---

## 2. Preparación de Datos (Sliding Window)
Para entrenar modelos de lenguaje, no se pasa el texto completo, sino pares de **entrada (input)** y **objetivo (target)**.

### Funcionamiento:
El código implementa una "ventana deslizante" (*sliding window*):
1. Se define un `maxLength` (ej. 4 tokens).
2. Se recorre la lista de tokens.
3. **Input**: Tokens desde la posición $i$ hasta $i + \text{maxLength}$.
4. **Target**: Tokens desde la posición $i+1$ hasta $i + \text{maxLength} + 1$.
   - *Objetivo*: El modelo aprende a predecir el siguiente token basándose en los anteriores.

---

## 3. Embeddings Manuales con `DJL` (Deep Java Library)
Un embedding es básicamente una **tabla de búsqueda (Lookup Table)** donde cada ID de token tiene asignado un vector de números reales.

### Implementación técnica:
- **`NDManager`**: Es el gestor de memoria de DJL. Controla la creación y liberación de arreglos multidimensionales (`NDArray`).
- **Matriz de Pesos (`weights`)**: Se crea un `NDArray` de forma `(vocabSize, outputDim)`.
  - `vocabSize` (50257): Número de palabras posibles en el diccionario.
  - `outputDim` (256): Tamaño del vector que representa a cada palabra.
- **`weights.get(indices)`**: Esta es la operación fundamental del embedding. En lugar de hacer una multiplicación matricial compleja, el código simplemente "extrae" las filas de la matriz correspondientes a los IDs de los tokens de entrada.

---

## 4. Embeddings Preentrenados con `LangChain4j`
A diferencia del método anterior (donde los pesos eran aleatorios), LangChain4j permite usar modelos ya entrenados con millones de textos.

### El modelo `AllMiniLmL6V2QuantizedEmbeddingModel`:
- **Modelo ONNX**: Utiliza un formato optimizado para ejecución rápida.
- **`embeddingModel.embed(text)`**: 
  1. Recibe el texto crudo.
  2. Internamente lo tokeniza.
  3. Pasa los tokens por una red neuronal profunda.
  4. Devuelve un **vector denso** (un `float[]`) que captura el **significado semántico** del texto.
- **Resultado**: Dos frases con significados similares tendrán vectores "cercanos" en el espacio matemático, aunque usen palabras diferentes.

---

## Resumen de Flujo de Datos

`Texto Plano` $\xrightarrow{\text{jtokkit}}$ `IDs de Tokens` $\xrightarrow{\text{DJL/LangChain4j}}$ `Vectores (Embeddings)`

| Librería | Función Principal | Aporte al Proceso |
|-----------|------------------|-------------------|
| **jtokkit** | Tokenización | Convierte texto $\rightarrow$ números |
| **DJL** | Manipulación Tensorial | Implementa la lógica de la tabla de búsqueda |
| **LangChain4j** | Modelos Preentrenados | Proporciona vectores con significado real |
