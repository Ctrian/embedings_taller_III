# Notas de Revisión: Funciones de Librerías para Embeddings

## DatasetItem.java

### Estructura
```java
public record DatasetItem(List<Integer> input, List<Integer> target) {}
```

**¿Qué es un record?**  
Es una clase inmutable automáticamente generada en Java 14+. Compila a una clase con:
- Constructor con parámetros `input` y `target`
- Getters: `input()` y `target()`
- `equals()`, `hashCode()` y `toString()`

**Propósito**:  
Representar un par de entrenamiento (input → target) donde:
- `input`: secuencia de tokens de entrada
- `target`: secuencia de tokens esperada (desplazada 1 posición)

---

## DataSampling.java

### Librería: JTokkit

**¿Qué es?**  
Librería Java para tokenización de texto usando los vocabularios de OpenAI.

#### Componentes clave:

1. **EncodingRegistry**
   ```java
   EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
   ```
   - Registro de codificaciones disponibles
   - `newDefaultEncodingRegistry()` carga el registro por defecto

2. **Encoding**
   ```java
   Encoding tokenizer = registry.getEncodingForModel(ModelType.TEXT_DAVINCI_003);
   ```
   - Obtiene el tokenizer de un modelo específico
   - Diferentes modelos = diferentes vocabularios = diferente tokenización

3. **Métodos principales del Encoding**:
   - `encode(String)`: Convierte texto a lista de IDs de tokens (`IntArrayList`)
   - `decode(IntArrayList)`: Convierte IDs de tokens de vuelta a texto

### Flujo de Trabajo

#### 1. Cargar texto
```java
String raw_text = Files.readString(Path.of(PATH)).lines().reduce(String::concat).orElse("");
```
- Lee el archivo de texto
- Convierte líneas en un solo string (elimina saltos de línea)

#### 2. Tokenizar
```java
var enc_text = tokenizer.encode(raw_text);  // IntArrayList de tokens
var enc_text_boxed = enc_text.boxed();       // Convierte a List<Integer>
```
- Convierte texto → secuencia de IDs numéricos
- Cada token tiene un ID único según el vocabulario del modelo

#### 3. Crear pares input-target (Sliding Window)
```java
int maxLength = 4;
IntStream.range(0, tokensIds.size() - maxLength)
    .forEach(i -> {
        var inputChunk = tokensIds.subList(i, i + maxLength);
        var targetChunk = tokensIds.subList(i + 1, i + maxLength + 1);
        dataset.add(new DatasetItem(inputChunk, targetChunk));
    });
```

**¿Qué hace?**
- Ventana deslizante de tamaño `maxLength`
- Para cada posición `i`:
  - **input**: tokens `[i, i+1, i+2, i+3]`
  - **target**: tokens `[i+1, i+2, i+3, i+4]` (desplazados 1 posición)

**Ejemplo visual**:
```
Texto: "El gato persigue al perro"
Tokens: [101, 205, 881, 412, 56, 330]

i=0: input=[101,205,881,412]  target=[205,881,412,56]
i=1: input=[205,881,412,56]   target=[881,412,56,330]
i=2: input=[881,412,56,330]   target=[412,56,330,...]
```

### Resumen del flujo

```
Texto original
     ↓
Tokenizer (encode)
     ↓
Lista de tokens (IDs numéricos)
     ↓
Sliding Window (crear pares)
     ↓
DatasetItem (input, target) → List<DatasetItem>
```

### Conceptos importantes para el examen

1. **Tokenización**: Proceso de convertir texto en tokens numéricos
2. **Vocabulario**: Conjunto de palabras/subpalabras que el modelo conoce
3. **Sliding Window**: Técnica para crear secuencias solapadas de entrenamiento
4. **Desplazamiento (shift)**: El target es el input deslocado 1 posición a la derecha
5. **Por qué tokenizar**: Los modelos de ML trabajan con números, no texto

### Alternativas de ModelType

- `GPT_4`, `GPT_3_5_TURBO`, `CLIP` → diferentes vocabularios
- Elige según el modelo que vas a emular/entrenar

---

*Notas generadas para revisión - Taller III*