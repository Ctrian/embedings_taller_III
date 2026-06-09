# Guia de Estudio - Taller III: Tokenizacion, Embeddings y Chat APIs

> **Rol del maestro:** Esta guia te pone a prueba. Cada ejercicio tiene un **porque** fundamentado, 
> una dificultad estimada y un espacio donde escribir tu respuesta. 
> No copies el codigo del proyecto — intenta resolver desde tu entendimiento.
> Al final encontraras las **claves de correccion** con lo que espero ver en cada respuesta.

---

## Temario Cubierto

| Bloque | Temas | Subproyecto |
|--------|-------|-------------|
| A | Tokenizacion: V1, V2, regex, vocabulario, special tokens | `01.embeddings` |
| B | BPE con jtokkit, sliding window, DatasetItem | `01.embeddings` |
| C | Embeddings: lookup table (DJL), modelos preentrenados (LangChain4j) | `01.embeddings` |
| D | Similitud coseno y busqueda semantica | `02.conexionAPI` |
| E | Chat APIs: ChatModel, streaming, memoria | `02.conexionAPI` |
| F | AiServices: @SystemMessage, @UserMessage, @V, structured output | `02.conexionAPI` |
| G | Function Calling: @Tool, @P, comportamiento agentic | `02.conexionAPI` |
| H | Spring AI: ChatClient, REST endpoint, application.yml | `03.SpringAI` |

---

## BLOQUE A: Tokenizacion

### Ejercicio A1 — Concepto fundamental (Dificultad: ★☆☆☆☆)

**Por que este ejercicio:** Sin entender que es un token y como se construye un vocabulario, 
no puedes comprender nada de lo que viene despues (embeddings, training data, chat). 
Es la base de toda la cadena.

**Pregunta:**
Explica con tus palabras: que es un token, que es un vocabulario, y por que un LLM 
necesita convertir texto a numeros (token IDs) antes de procesarlo.

**Tu respuesta:**
```
Un token es una representación de una palabra, conjunto de palabras o incluso caracteres
Un vocabulario es una representacion a modo de clave valor donde el token tiene la posicion del token
Los LLMs no entienden texto, por lo cual se usa un calculo probabilistico para predecir el siguiente token, y 
para eso necesitan convertir el texto en numeros
```

---

### Ejercicio A2 — Regex de tokenizacion (Dificultad: ★★★☆☆)

**Por que este ejercicio:** La regex que separa texto en tokens es el corazon de los 
tokenizers V1 y V2. Si no entiendes como funciona, no puedes explicar por que 
`"Hello, world"` se convierte en `["Hello", ",", " world"]` y no en `["Hello, world"]`. 
En un examen te pueden pedir escribir o explicar esta regex.

**Pregunta:**
Dada la regex usada en el proyecto:
```
(?=[,.:;?_!"()']|--|\s)|(?<=[,.:;?_!"()']|--|\s)
```

a) Explica que hace el lookahead `(?=...)` y el lookbehind `(?<=...)`.
b) Por que la puntuacion queda como token separado?
c) Que pasa con `"Hello--world"`? Cuales son los tokens resultantes?

**Tu respuesta:**
```
// A)
Le dice que haga un corte antes de una coma, un punto, dos puntos, etc. o un espacio



// B)
Por que hay un corte con lookahead y otro con lookbehind, entonces la puntuacion queda entre ambos cortes, como un token separado



// C)
"Hello--world" se convierte en ["Hello", "--", "world"]



```

---

### Ejercicio A3 — TokenizerV1 vs TokenizerV2 (Dificultad: ★★★☆☆)

**Por que este ejercicio:** Esta es una pregunta clasica de examen. V1 tiene un bug 
silencioso (pierde tokens desconocidos), V2 lo resuelve con `<|unk|>`. Entender esta 
diferencia demuestra que comprendes el problema de los out-of-vocabulary tokens y 
como manejarlos — un tema central en NLP.

**Pregunta:**
Dado el siguiente vocabulario: `{0:"hello", 1:"world", 2:"test"}`

a) Con TokenizerV1, que IDs produce `"hello python world"`? 
b) Con TokenizerV2 (con `<|unk|>` = 3), que IDs produce `"hello python world"`?
c) Cual es la diferencia clave en el codigo entre V1 y V2? (menciona el metodo especifico)
d) Por que V1 es problematico para un modelo de lenguaje?

**Tu respuesta:**
```
// A)
Pair[tokenId=0, token=hello]
Pair[tokenId=1, token=test]
Pair[tokenId=2, token=world]

// B)
Pair[tokenId=0, token=hello]
Pair[tokenId=1, token=test]
Pair[tokenId=2, token=world]
Pair[tokenId=3, token=<|unk|>]

// C)
En V1 se usa `strToInt.get(token)` que devuelve null para tokens desconocidos, mientras que en V2 se usa
`strToInt.getOrDefault(token, strToInt.get("<|unk|>"))` que devuelve el ID de `<|unk|>` para tokens desconocidos.

// D)
Pierde informacion importante, el modelo ve secuencias con huecos (nulls) en vez de una señal clara de "token desconocido".



```

---

### Ejercicio A4 — Construccion de vocabulario (Dificultad: ★★★★☆)

**Por que este ejercicio:** En el examen pueden pedirte escribir o explicar el proceso 
de construir un vocabulario desde un corpus. Esto integra regex, Sets para unicidad, 
ordenamiento, y asignacion de IDs — multiples conceptos a la vez.

**Pregunta:**
Escribe pseudocodigo (o codigo Java) del metodo `vocabulary(filename)` de 
`TestTokenizerMain.java`. Explica cada paso. No mires el codigo original.

**Tu respuesta:**

```java
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

record Pair(Integer tokenId, String token){}

// lee el archivo .txt
String text = Files.readString(Path.of(filename));

        se usa
        una regex
        para dividir
        el texto
        en tokens
        String regex = "(?=[,.:;?_!\"()']|--|\\s)|(?<=[,.:;?_!\"()']|--|\\a)";

        // preservamos valores usando split
        var tokens = text.split(regex);

        // limpieza
        var pre = Stream.of(tokens)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        // ordenar
        var all = pre.stream()
                .distinct()
                .sorted()
                .toList();

// asignar IDs con AtomicInteger x el lambda
        AtomicInteger i = new AtomicInteger(0);
        return all.stream()
        .map(it->new Pair(i.getAndIncrement(), it))
        .toList();




```

---

### Ejercicio A5 — Special tokens `<|unk|>` y `<|endoftext|>` (Dificultad: ★★☆☆☆)

**Por que este ejercicio:** Los special tokens son convencion en la industria. 
`<|unk|>` es universal en NLP. `<|endoftext|>` (o `<|endoftext|>`) es el equivalente 
a `<|endoftext|>` de OpenAI. Entender para que sirven es clave para temas de 
training data y multi-documento.

**Pregunta:**
a) Para que sirve `<|unk|>` en el vocabulario?
b) Para que sirve `<|endoftext|>`? Da un ejemplo concreto de uso.
c) Por que estos tokens se agregan DESPUES de construir el vocabulario del corpus, 
   no antes?

**Tu respuesta:**
```
// A)
sirve para identificar caracteres o palabras que no se encuentran en el vocabulario

// B)
sirve para identificar cual es el final de una entrada de texto, si se quiere entrenar con varios textos, el poder 
separarlos con un token de este tipo es fundamental para que el modelo aprenda a identificar los limites entre textos

// C)
Por convención, normalmente se agregan al final por que no son parte del vocabulario



```

---

## BLOQUE B: BPE con jtokkit y Sliding Window

### Ejercicio B1 — jtokkit: encode vs encodeOrdinary (Dificultad: ★★☆☆☆)

**Por que este ejercicio:** La diferencia entre `encode()` y `encodeOrdinary()` 
es un detalle que aparece en el codigo y que puede ser preguntado. 
`encodeOrdinary()` ignora special tokens; `encode()` los procesa. 
Esto conecta con el tema de special tokens del Bloque A.

**Pregunta:**
a) Que hace `EncodingRegistry.getDefaultRegistry().getEncoding(ModelType.GPT_4)`?
b) Cual es la diferencia entre `encodeOrdinary("Hello<|endoftext|>World")` y 
   `encode("Hello<|endoftext|>World")`?
c) Cuantos tokens tiene aproximadamente el vocabulario de GPT-4 vs TEXT_DAVINCI_003?

**Tu respuesta:**
```
// A)
Carga un vocabulario de GPT4, valores por defecto del modelo

// B)
En encodeOrdinary hay 9 tokens lo cual quiere decir que se esta dividiendo el caracter especial <|endoftext|> en tokens separados
encode Lanza UnsupportedOperationException al detectar `<

// C)
gpt tiene alrededor de 100k tokens, da vinci solo muestra com.knuddels.jtokkit.GptBytePairEncoding@4e8f0bb9



```

---

### Ejercicio B2 — Sliding Window paso a paso (Dificultad: ★★★★☆)

**Por que este ejercicio:** El sliding window es la tecnica fundamental para generar 
datos de entrenamiento de un LLM autoregresivo. Es casi seguro que aparezca en el 
examen, ya sea pidiendote explicarlo, escribirlo, o calcular los pares input-target.

**Pregunta:**
Dada la secuencia de token IDs: `[10, 20, 30, 40, 50, 60, 70]` y un 
context size = 3:

a) Escribe TODOS los pares `(input, target)` que genera el sliding window.
b) Cuantos pares se generan en total? Cual es la formula general para `n` tokens 
   y context size `k`?
c) Por que el target es el input desplazado 1 posicion? Que aprendizaje representa?

**Tu respuesta:**
```
// A)
input: 10,20,30  target: 40
input: 20,30,40  target: 50
input: 30,40,50  target: 60
input: 40,50,60  target: 70

// B)
pares = n - k
pares = 7 - 3
pares = 4

// C)
la idea es predecir la siguiente palabra, usando las anteriores como contexto, entonces el target es el input
desplazado 1 posicion para que el modelo aprenda a predecir la siguiente palabra, se usa slice windows

```

---

### Ejercicio B3 — Metodo .boxed() (Dificultad: ★★☆☆☆)

**Por que este ejercicio:** `.boxed()` es un detalle tecnico pero importante. 
jtokkit devuelve `IntArrayList` (primitivos), pero `List.subList()` necesita 
objetos `Integer`. Este tipo de pregunta de "por que el codigo hace X" es tipica 
en examenes.

**Pregunta:**
a) Por que es necesario llamar `.boxed()` en `DataSampling.java`?
b) Que tipo devuelve `jtokkit.encode()` y que tipo necesita `List.subList()`?
c) Que pasaria si NO llamas `.boxed()`?

**Tu respuesta:**
```
// A)
Para convertir el IntArrayList de tokens (que es una lista de primitivos int) a una List<Integer> que es una
lista de objetos Integer, ya que subList no funciona con tipos primitivos

// B)
jtokkit.encode() devuelve IntArrayList y List.subList() necesita List<Integer>

// C)
da un error de compilacion, no se puede convertir IntArrayList a List<Integer>

```

---

### Ejercicio B4 — DatasetItem (Dificultad: ★★☆☆☆)

**Por que este ejercicio:** `DatasetItem` es el `record` que encapsula cada muestra 
de entrenamiento. Entender su estructura es entender el formato de datos que 
alimenta al modelo.

**Pregunta:**
a) Escribe la definicion del record `DatasetItem`.
b) Si `input = [10, 20, 30, 40]`, cual es `target`? (context size = 4, secuencia = [10, 20, 30, 40, 50])
c) Por que `input` y `target` son `List<Integer>` y no `int[]`?

**Tu respuesta:**
```java
// A)
Es una clase inmutable que tiene dos campos: List<Integer> input y List<Integer> target, se usa para representar una
muestra de entrenamiento con su secuencia de entrada y su secuencia objetivo

```
```
// B)
50

// C)
se usan List debido a que permite .add .remove y otras operaciones de coleccion, ademas de ser mas flexible en tamaño,
mientras que un int[] es de tamaño fijo y no tiene metodos de coleccion



```

---

## BLOQUE C: Embeddings

### Ejercicio C1 — Embedding como lookup table (Dificultad: ★★★★☆)

**Por que este ejercicio:** Este es el concepto mas profundo del proyecto. 
Un embedding no es "magia" — es una fila de una matriz de pesos indexada por 
el token ID. Si no entiendes esto, no entiendes como funciona el interior 
de un transformer. Es probable que te pidan explicar este mecanismo.

**Pregunta:**
a) Que es una embedding lookup table? Relaciona con el codigo de `EmbeddingTest.java`.
b) Si la matriz de pesos es de forma `(50257, 256)`, que significa el 50257 y 
   que significa el 256?
c) Si el token ID es 42, como obtienes su embedding vector de la matriz?
d) Por que los embeddings aleatorios (random uniform) no capturan significado 
   semantico, pero los preentrenados si?

**Tu respuesta:**
```
// A)
Es la capa que contiene a cada token del vocabulario representado como un vector de numeros, en el codigo se representa
con la clase NDArray weights, donde cada fila es el embedding de un token

// B)
50257 es el numero de tokens en el vocabulario, 256 es la dimension del vector de embedding para cada token
filas y columnas del embedding

// C)
weights.get(42) devuelve el vector de embedding para el token con ID 42, es decir la fila 42 de la matriz de pesos

// D)
debido a que no cuentan con el mecanismo de atencion, no se entrenan para capturar relaciones semanticas entre
palabras, mientras que los preentrenados si se entrenan con grandes corpus de texto y aprenden a representar el
significado de las palabras en los vectores de embedding

```

---

### Ejercicio C2 — NDManager y NDArray (Dificultad: ★★★☆☆)

**Por que este ejercicio:** DJL (Deep Java Library) es el framework que permite 
hacer operaciones de tensores en Java. `NDManager` maneja memoria y `NDArray` 
es el tensor. Entender esto es entender como Java interactua con operaciones 
de ML de bajo nivel.

**Pregunta:**
a) Para que sirve `NDManager` en DJL? Que pasa si no lo cierras?
b) Como se crea una matriz aleatoria de forma `(50257, 256)` con DJL?
c) `weights.get(indices)` — que hace exactamente este metodo? 
   Por que los indices deben ser `long` y no `int`?

**Tu respuesta:**
```
// A)
NDManager es el encargado de administrar la memoria de los tensores (NDArray) en DJL, si no lo cierras, puedes tener
fugas de memoria ya que los tensores no se liberan correctamente

// B)
NDManager manager = NDManager.newBaseManager()

NDArray weights =
        manager.randomNormal(
            0f,
            1f,
            new Shape(50257, 256),
            DataType.FLOAT32
        );

    System.out.println(weights.getShape());

// C)
obtiene los valores del embedding, los valores representativos del vector dado un indice, los indices deben ser long
por que el metodo get de NDArray espera un array de long como parametro, no un array de int


```

---

### Ejercicio C3 — AllMiniLmL6V2 y ONNX (Dificultad: ★★★☆☆)

**Por que este ejercicio:** El modelo AllMiniLmL6V2 es el primer modelo "real" 
que usas en el proyecto. Entender que es un modelo ONNX cuantizado, que 
dimension produce (384), y como se diferencia de un embedding aleatorio, 
es clave para responder preguntas sobre embeddings preentrenados.

**Pregunta:**
a) Que es ONNX y por que LangChain4j lo usa para ejecutar modelos localmente?
b) Que significa "cuantizado" (quantized) en `AllMiniLmL6V2QuantizedEmbeddingModel`?
c) Cual es la dimension del vector que produce AllMiniLmL6V2?
d) En `EmbeddingModelMain.java`, que es `PoolingMode.MEAN` y por que es necesario?

**Tu respuesta:**
```
// A)
es un formato para representar modelo de ML, Langchain4j lo usa para ejecutar modelos localmente sin necesidad de una
API externa, permite usar modelos preentrenados de forma eficiente

// B)
cuantizado significa que los pesos del modelo se han convertido a un formato de menor precision (por ejemplo, de
float32 a int8) para reducir el tamaño del modelo y acelerar la inferencia

// C)
dimension: 384

// D)
se usa PoolingMode.MEAN para promediar los vectores de embedding de cada token y obtener un solo vector representativo

```

---

### Ejercicio C4 — Flujo de datos completo (Dificultad: ★★★★★)

**Por que este ejercicio:** Este es el ejercicio integrador mas importante del 
bloque de embeddings. Conectar todo el flujo — texto a tokens, tokens a IDs, 
IDs a vectores — demuestra comprension holistica. En un examen de Taller III, 
pedirte dibujar o explicar este flujo es muy probable.

**Pregunta:**
Dibuja/explica el flujo completo de datos desde texto crudo hasta embedding vector, 
cubriendo AMBOS caminos del proyecto:

1. **Camino manual (DJL):** texto -> regex split -> vocab lookup -> token IDs -> weight matrix lookup -> vectors
2. **Camino preentrenado (LangChain4j):** texto -> modelo ONNX -> embedding vector

Se explicito en cada paso. Menciona las clases y metodos involucrados.

**Tu respuesta:**
```
// CAMINO 1: MANUAL (DJL)




// CAMINO 2: PREENTRENADO (LangChain4j)




```

---

## BLOQUE D: Similitud Coseno y Busqueda Semantica

### Ejercicio D1 — Formula de similitud coseno (Dificultad: ★★★☆☆)

**Por que este ejercicio:** La similitud coseno es la metrica central de busqueda 
semantica. Si no la entiendes, no puedes explicar POR QUE dos oraciones con 
palabras diferentes pero significado similar obtienen un score alto. 
Es pregunta garantizada en cualquier examen de embeddings.

**Pregunta:**
a) Escribe la formula de la similitud coseno entre dos vectores A y B.
b) Cual es el rango de valores? Que significa 1, 0, y -1?
c) Si A = [1, 0, 1] y B = [0, 1, 0], calcula la similitud coseno paso a paso.
d) En `SimilitudMain.java`, por que "Me gustan los perros" y "Amo a los caninos" 
   tienen alta similitud a pesar de no compartir palabras?

**Tu respuesta:**
```
// A)




// B)




// C)




// D)




```

---

### Ejercicio D2 — Busqueda semantica manual (Dificultad: ★★★★☆)

**Por que este ejercicio:** `BusquedaSemanticaMain.java` implementa busqueda 
semantica desde cero — sin abstracciones. Entender este algoritmo paso a paso 
te permite explicarlo en un examen y detectar el bug off-by-one que tiene.

**Pregunta:**
Dado un conjunto de documentos y un query, describe el algoritmo de 
busqueda semantica manual paso a paso (como lo hace `BusquedaSemanticaMain.java`):

a) Paso 1: Que se hace con los documentos?
b) Paso 2: Que se hace con el query?
c) Paso 3: Como se determina cual documento es el mas relevante?
d) **BUG HUNTING:** En `BusquedaSemanticaMain.java` hay un bug con `mejorIndice`. 
   Encuentralo y explicalo.

**Tu respuesta:**
```
// A)




// B)




// C)




// D)




```

---

### Ejercicio D3 — InMemoryEmbeddingStore (Dificultad: ★★★☆☆)

**Por que este ejercicio:** `InMemoryEmbeddingStore` es la forma "correcta" de 
hacer busqueda semantica en LangChain4j. Es la version production-ready del 
algoritmo manual. En un examen te pueden pedir comparar ambos enfoques.

**Pregunta:**
a) Que es `TextSegment` y para que sirve?
b) Escribe el codigo para: crear un `InMemoryEmbeddingStore`, agregar 2 documentos, 
   y buscar el mas similar a un query con `maxResults(1)`.
c) Que ventaja tiene este enfoque sobre la busqueda manual del Ejercicio D2?
d) Que es `minScore` en `EmbeddingSearchRequest` y cuando lo usarias?

**Tu respuesta:**
```java
// B)


```
```
// A)




// C)




// D)




```

---

## BLOQUE E: Chat APIs

### Ejercicio E1 — ChatModel basico (Dificultad: ★★☆☆☆)

**Por que este ejercicio:** `OpenAiChatModel` es la clase fundamental para 
interactuar con un LLM. Entender como se configura y usa es la base para 
todo lo demas (memoria, streaming, AiServices). Sin esto, no puedes 
construir ninguna aplicacion de chat.

**Pregunta:**
a) Escribe el codigo para crear un `OpenAiChatModel` que se conecte a 
   `http://localhost:8080` con apiKey "mi-clave" y modelo "llama-3".
b) Como se envia un mensaje y se obtiene la respuesta?
c) Por que el `apiKey` puede ser cualquier string ("cualquiera") cuando 
   usas llama.cpp localmente?

**Tu respuesta:**
```java
// A)


```
```
// B)




// C)




```

---

### Ejercicio E2 — Chat con memoria (Dificultad: ★★★☆☆)

**Por que este ejercicio:** Sin memoria, un chatbot es stateless — no recuerda 
nada de la conversacion. `MessageWindowChatMemory` resuelve esto con una 
ventana deslizante de mensajes. Este concepto conecta directamente con el 
sliding window del Bloque B — misma idea, diferente contexto.

**Pregunta:**
a) Que es `MessageWindowChatMemory` y como funciona?
b) Si `maxMessages = 10`, que pasa cuando hay 12 mensajes en la conversacion?
c) Por que es necesario un `maxMessages`? Por que no guardar TODOS los mensajes?
d) Escribe el codigo para crear un AiService con memoria de 10 mensajes.

**Tu respuesta:**
```
// A)




// B)




// C)




```
```java
// D)


```

---

### Ejercicio E3 — Streaming vs Sincrono (Dificultad: ★★★☆☆)

**Por que este ejercicio:** Streaming es fundamental para UX — el usuario ve 
la respuesta aparecer token por token en vez de esperar la respuesta completa. 
Entender la diferencia arquitectural entre ambos enfoques es importante.

**Pregunta:**
a) Cual es la diferencia entre `OpenAiChatModel` y `OpenAiStreamingChatModel`?
b) Que metodos de `StreamingChatResponseHandler` debes implementar?
c) Por que `ChatStreamingMain.java` usa `AtomicInteger` como contador?
d) En que situacion prefieres sincrono y en cual streaming?

**Tu respuesta:**
```
// A)




// B)




// C)




// D)




```

---

## BLOQUE F: AiServices

### Ejercicio F1 — AiServices con @SystemMessage y @UserMessage (Dificultad: ★★★★☆)

**Por que este ejercicio:** AiServices es el patron declarativo mas poderoso 
de LangChain4j. Define una interfaz, anotala, y el framework genera la 
implementacion. Es el patron que usaran en cualquier proyecto real. 
En un examen, te pueden pedir crear una interfaz de AiService desde cero.

**Pregunta:**
a) Crea una interfaz `Traductor` que use `@SystemMessage` para definir 
   al AI como "Eres un traductor profesional espanol-ingles" y `@UserMessage` 
   con un template `{{texto}}` para el texto a traducir.
b) Escribe el codigo para construir el AiService usando esa interfaz.
c) Que hace `@V("texto")` exactamente? Por que es necesario?
d) Cual es la ventaja de AiServices sobre `chatModel.chat(prompt)` directo?

**Tu respuesta:**
```java
// A)


```
```java
// B)


```
```
// C)




// D)




```

---

### Ejercicio F2 — Structured Output (Dificultad: ★★★★☆)

**Por que este ejercicio:** Extraer datos estructurados de un LLM es uno de 
los patrones mas utiles en produccion. En vez de parsear texto libre, 
obtienes un objeto Java tipado. Esto conecta LLMs con el resto de tu sistema.

**Pregunta:**
a) Define un record `Libro` con campos `titulo`, `autor`, `anio`.
b) Crea una interfaz `ExtractorLibro` con `@UserMessage` que pida al LLM 
   extraer informacion de un texto y devolver JSON.
c) Escribe el codigo para construir el AiService y usarlo.
d) Que pasa si el LLM no devuelve JSON valido? Como lo maneja LangChain4j?

**Tu respuesta:**
```java
// A)


```
```java
// B)


```
```java
// C)


```
```
// D)




```

---

## BLOQUE G: Function Calling

### Ejercicio G1 — @Tool y @P (Dificultad: ★★★★★)

**Por que este ejercicio:** Function calling es lo que convierte un LLM de 
"generador de texto" a "agente que puede actuar". Es el tema mas avanzado 
del proyecto y el mas probable en una pregunta de alto valor en el examen. 
Entender que el LLM DECIDE cuando llamar una funcion (no es el programador) 
es el concepto clave.

**Pregunta:**
a) Crea una clase `HerramientasClima` con un metodo anotado con `@Tool` que 
   reciba una ciudad y devuelva un String con " Soleado, 25C en [ciudad]". 
   Usa `@P` para describir el parametro.
b) Crea una interfaz `AsistenteClima` con `@SystemMessage` apropiado.
c) Escribe el codigo para construir el AiService con las tools registradas.
d) Explica el flujo completo: cuando el usuario pregunta "Que clima hay en Madrid?", 
   que decide el LLM, que se ejecuta, y que se devuelve?
e) Diferencia entre `@Tool("descripcion")` y `@P("descripcion")` — para que 
   sirve cada uno y quien los lee?

**Tu respuesta:**
```java
// A)


```
```java
// B)


```
```java
// C)


```
```
// D)




// E)




```

---

### Ejercicio G2 — Pensamiento agentic (Dificultad: ★★★★☆)

**Por que este ejercicio:** En un examen de nivel avanzado, te pueden pedir 
explicar POR QUE function calling es "agentic" y que lo diferencia de un 
simple if/else. Esto demuestra comprension profunda, no solo mecanica.

**Pregunta:**
a) Por que function calling es diferente a un `if (pregunta.contains("hora")) { llamarMetodo(); }`?
b) Quien decide si una funcion se ejecuta o no? El programador o el LLM?
c) Que pasa si el LLM decide llamar una funcion que no existe?
d) Da un ejemplo donde el LLM deba llamar MAS DE UNA funcion para responder 
   una pregunta.

**Tu respuesta:**
```
// A)




// B)




// C)




// D)




```

---

## BLOQUE H: Spring AI

### Ejercicio H1 — Configuracion de Spring AI (Dificultad: ★★★☆☆)

**Por que este ejercicio:** Spring AI usa auto-configuracion desde `application.yml`. 
Entender estas propiedades es entender como Spring Boot simplifica la configuracion 
del cliente LLM. Es un tema de configuracion que puede aparecer en preguntas cortas.

**Pregunta:**
Dado el `application.yml` del proyecto:

```yaml
server:
  port: 8081

spring:
  ai:
    openai:
      base-url: http://localhost:8080
      api-key: xyz
      chat:
        options:
          model: "llama 3.2"
```

a) Por que el puerto del servidor es 8081 y no 8080?
b) Que significa `base-url`? A que apunta?
c) Por que `api-key: xyz` funciona con llama.cpp?

**Tu respuesta:**
```
// A)




// B)




// C)




```

---

### Ejercicio H2 — REST Chat Endpoint (Dificultad: ★★★★☆)

**Por que este ejercicio:** Crear un endpoint REST que exponga un LLM es el 
patron de despliegue mas comun en produccion. En un examen te pueden pedir 
escribir un Controller desde cero o explicar el flujo request-response.

**Pregunta:**
a) Escribe un `@RestController` con un endpoint `GET /chat` que reciba un 
   `message` como query parameter y devuelva la respuesta del LLM.
b) Que hace exactamente `client.prompt().user(message).call().content()`? 
   Desglosa cada metodo.
c) Como se inyecta `ChatClient.Builder` en Spring? Que patron de diseño usa?
d) Como se configura el `ChatMemory` en `Main.java`? Que `@Bean` se define?

**Tu respuesta:**
```java
// A)


```
```
// B)




// C)




```
```java
// D)


```

---

### Ejercicio H3 — Comparativa LangChain4j vs Spring AI (Dificultad: ★★★★★)

**Por que este ejercicio:** El proyecto usa DOS frameworks diferentes para 
chat con LLMs. Compararlos demuestra que entiendes las abstracciones, no 
solo la mecanica. Es la pregunta integradora final.

**Pregunta:**
Completa la siguiente tabla comparativa:

| Concepto | LangChain4j | Spring AI |
|----------|-------------|-----------|
| Cliente de chat | ? | ? |
| Config del modelo | ? | ? |
| Memoria de chat | ? | ? |
| Patron de llamada | ? | ? |
| AiServices/Tools | ? | ? |
| Ventaja principal | ? | ? |

**Tu respuesta:**
```
// COMPLETA LA TABLA








```

---

## EJERCICIOS DE CODIGO PRACTICO

### Ejercicio P1 — Implementa TokenizerV3 (Dificultad: ★★★★★)

**Por que este ejercicio:** Este es el ejercicio mas desafiante de la guia. 
Te pide crear un tokenizer que MEJORE a V2. Integra todos los conceptos 
del Bloque A: regex, vocabulario, special tokens, encode, decode. 
Si puedes hacer esto, dominas tokenizacion.

**Consigna:**
Crea un archivo `TokenizerV3.java` en el paquete `com.programacion.taller3` 
que mejore TokenizerV2 con las siguientes caracteristicas:

1. En `decode()`, la puntuacion NO debe tener espacios extra. 
   Es decir, si los IDs decodifican a `["Hello", ",", " world"]`, 
   el resultado debe ser `"Hello, world"`, no `"Hello ,  world"`.
2. Agregar un metodo `encodeWithCount(text)` que devuelva un `Map<String, Integer>` 
   con cada token y cuantas veces aparece.
3. Agregar un metodo `vocabularySize()` que devuelva el tamano del vocabulario.

Escribe el codigo completo de la clase.

**Tu codigo:**
```java
package com.programacion.taller3;

import java.util.*;
import java.util.regex.Pattern;

public class TokenizerV3 {

    // ESCRIBE TU IMPLEMENTACION AQUI









}
```

---

### Ejercicio P2 — Busqueda semantica con relevancia (Dificultad: ★★★★☆)

**Por que este ejercicio:** Te pide implementar busqueda semantica pero con 
un twist: filtrar por score minimo. Esto combina conceptos de similitud coseno, 
embeddings, y la API de `InMemoryEmbeddingStore`. Es un ejercicio realista 
que podria aparecer como pregunta de desarrollo.

**Consigna:**
Escribe un programa `BusquedaConFiltro.java` que:
1. Agregue 5 documentos a un `InMemoryEmbeddingStore`
2. Busque con un query, `maxResults(3)` y `minScore(0.5)`
3. Imprima solo los resultados que pasen el filtro de score
4. Para cada resultado, imprima: texto del documento, score, y si es relevante o no

```java
package com.programacion.taller3;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;

public class BusquedaConFiltro {

    // ESCRIBE TU IMPLEMENTACION AQUI









}
```

---

### Ejercicio P3 — Asistente con multiples tools (Dificultad: ★★★★★)

**Por que este ejercicio:** Este es el ejercicio integrador del Bloque G. 
Te pide crear un sistema con multiples herramientas, lo cual demuestra 
que entiendes function calling a nivel de diseno, no solo de codigo aislado. 
En produccion, un agente siempre tiene multiples tools.

**Consigna:**
Crea un sistema con:
1. Una clase `HerramientasMatematicas` con 3 metodos anotados con `@Tool`:
   - `sumar(double a, double b)` 
   - `multiplicar(double a, double b)`
   - `raizCuadrada(double numero)`
2. Una interfaz `AsistenteMatematico` con `@SystemMessage` apropiado
3. Un `main()` que pregunte: "Cuanto es 15 * 8 + la raiz cuadrada de 144?"
4. Imprime la respuesta del asistente

Nota: Para este ejercicio, asume que el ChatModel ya esta configurado 
(puedes usar `ChatMain.chatModel()`).

```java
package com.programacion.taller3;

// ESCRIBE TU IMPLEMENTACION AQUI












```

---

## CLAVES DE CORRECCION

> **NO leas esto hasta haber intentado todos los ejercicios.**
> Estas claves te dicen QUE debe tener tu respuesta, no la respuesta exacta.

### A1 — Debe mencionar:
- Token = unidad minima de texto (palabra, subpalabra, caracter)
- Vocabulario = mapeo de tokens a IDs numericos
- LLMs operan con numeros, no texto; los IDs son indices para lookup en matrices

### A2 — Debe mencionar:
- Lookahead `(?=X)` divide ANTES de X sin consumir caracteres
- Lookbehind `(?<=X)` divide DESPUES de X sin consumir caracteres
- La puntuacion queda separada porque esta en ambos lados de la regex
- `"Hello--world"` produce `["Hello", "--", "world"]` porque `--` esta en la regex

### A3 — Debe mencionar:
- V1: `[0, 2]` (python se pierde silenciosamente)
- V2: `[0, 3, 2]` (python → `<|unk|>` → ID 3)
- Diferencia clave: `strToInt.get()` vs `strToInt.getOrDefault(token, strToInt.get("<|unk|>"))`
- V1 es problematico: pierde informacion, el modelo ve secuencias con huecos

### A4 — Debe incluir:
1. Leer archivo con `Files.readString(Path.of(filename))`
2. Split con regex: `Pattern.compile(regex).split(text)`
3. Nuevo HashSet<>(lista) para eliminar duplicados
4. Ordenar alfabeticamente con `.stream().sorted().toList()`
5. AtomicInteger i = new AtomicInteger(0)
6. Mapear cada palabra a `new Pair(i.getAndIncrement(), palabra)`

### A5 — Debe mencionar:
- `<|unk|>`: reemplaza tokens fuera del vocabulario (OOV)
- `<|endoftext|>`: separa documentos concatenados en training data
- Se agregan despues para que no se les asigne IDs bajos arbitrarios; 
  y porque no son palabras del corpus, son convenciones

### B1 — Debe mencionar:
- `getEncoding(ModelType.GPT_4)` obtiene el encoding BPE del modelo GPT-4
- `encodeOrdinary()` ignora special tokens, `encode()` los procesa
- GPT-4: ~100k tokens; TEXT_DAVINCI_003: 50257 tokens (p50k_base)

### B2 — Debe mencionar:
- Pares: ([10,20,30], [20,30,40]), ([20,30,40], [30,40,50]), ([30,40,50], [40,50,60]), ([40,50,60], [50,60,70])
- Total: 4 pares. Formula: `n - k` donde n=7, k=3 → 4
- Target desplazado 1 posicion = next-token prediction: dado el contexto, predecir el siguiente token

### B3 — Debe mencionar:
- `.boxed()` convierte `IntArrayList` (primitivos) a `List<Integer>` (objetos)
- `encode()` devuelve `IntArrayList`; `subList()` necesita `List<Integer>`
- Sin `.boxed()`: error de compilacion — tipos incompatibles

### B4 — Debe mencionar:
- `record DatasetItem(List<Integer> input, List<Integer> target) {}`
- `target = [20, 30, 40, 50]`
- `List<Integer>` permite usar `subList()`, `stream()`, y otras APIs de Collections

### C1 — Debe mencionar:
- Lookup table = matriz de pesos (vocabSize × embeddingDim), token ID indexa una fila
- 50257 = vocabSize (tokens distintos), 256 = dimension del embedding
- `weights.get(longArrayConId42)` → devuelve la fila 42 como NDArray
- Aleatorios: no tienen significado, son valores random. Preentrenados: entrenados en corpus masivo, capturan relaciones semanticas

### C2 — Debe mencionar:
- `NDManager` = gestor de memoria nativa; si no lo cierras, memory leak
- `manager.randomUniform(0, 1, new Shape(50257, 256))`
- `get(indices)` hace indexacion avanzada (fancy indexing); DJL usa long porque 
  las dimensiones de tensores pueden exceder el rango de int

### C3 — Debe mencionar:
- ONNX = formato portable para modelos ML; permite ejecutar sin Python
- Cuantizado = pesos en menor precision (int8 vs float32), menor tamano y mas rapido
- Dimension: 384
- PoolingMode.MEAN = promedia los vectores de todos los tokens en uno solo (sentence embedding)

### C4 — Debe incluir el flujo:
- **DJL:** texto → `Pattern.split()` → `strToInt.get()` → IDs → `NDManager/NDArray` → `weights.get(ids)` → vectores
- **LangChain4j:** texto → `model.embed(text)` → internamente tokeniza + pasa por ONNX → embedding vector 384-dim

### D1 — Debe mencionar:
- Formula: cos(A,B) = (A·B) / (||A|| × ||B||)
- Rango: [-1, 1]. 1 = misma direccion, 0 = ortogonal, -1 = opuesta
- A·B = 0, ||A|| = √2, ||B|| = 1, cos = 0
- Embeddings preentrenados capturan significado, no palabras. "perros" y "caninos" 
  aparecen en contextos similares durante el entrenamiento

### D2 — Debe mencionar:
- Paso 1: Embed cada documento → lista de Embedding
- Paso 2: Embed el query → Embedding
- Paso 3: Iterar, calcular `CosineSimilarity.between(query, doc)` para cada uno, 
  quedarse con el de mayor score
- Bug: el indice se maneja incorrectamente con `index - 1` y `mejorIndice + 1`, 
  causando off-by-one en el documento seleccionado

### D3 — Debe mencionar:
- `TextSegment` = texto + metadata, unidad almacenada en embedding store
- Codigo: ver `InMemoryEmbeddingStoreMain.java` como referencia
- Ventaja: abstraccion, soporte para minScore, maxResults, escalable a stores reales (Pinecone, etc.)
- `minScore` = umbral de similitud minima para considerar un resultado relevante; 
  se usa para filtrar resultados de baja calidad

### E1 — Debe mencionar:
- `OpenAiChatModel.builder().baseUrl("http://localhost:8080/v1").apiKey("mi-clave").modelName("llama-3").build()`
- `String response = model.chat("Hola").aiMessage().text()` o similar
- llama.cpp no valida API keys; es un servidor local sin autenticacion

### E2 — Debe mencionar:
- Mantiene una ventana deslizante de mensajes recientes
- Con 12 mensajes y max=10, se eliminan los 2 mas antiguos
- Limitar mensajes: control de tokens (context window finito) y costos
- Codigo con `.chatMemory(MessageWindowChatMemory.builder().maxMessages(10).build())`

### E3 — Debe mencionar:
- Sincronico: espera respuesta completa. Streaming: recibe tokens parciales via callbacks
- `onPartialResponse`, `onCompleteResponse`, `onError`, `onPartialToolCall`, etc.
- `AtomicInteger` como contador para mantener el hilo principal vivo hasta que 
  `onCompleteResponse` lo incremente
- Sincrono: scripts, APIs internas. Streaming: UI, chatbots en tiempo real

### F1 — Debe incluir:
- Interfaz con `@SystemMessage` y `@UserMessage("Traduce al ingles: {{texto}}")`
- `AiServices.create(Traductor.class, chatModel)`
- `@V` inyecta el parametro del metodo en el placeholder `{{texto}}`
- Ventaja: declarativo, type-safe, separa prompt engineering de logica de negocio

### F2 — Debe incluir:
- `record Libro(String titulo, String autor, int anio) {}`
- Interfaz con `@UserMessage` que pida JSON con esos campos
- `AiServices.create(ExtractorLibro.class, chatModel)` y llamar al metodo
- Si el JSON es invalido, LangChain4j lanza una excepcion de parsing

### G1 — Debe incluir:
- Clase con metodo `@Tool("Obtiene el clima de una ciudad") String obtenerClima(@P("Nombre de la ciudad") String ciudad)`
- Interfaz con `@SystemMessage("Eres un asistente que informa sobre el clima")`
- `AiServices.builder().chatModel(model).tools(new HerramientasClima()).build()`
- Flujo: user pregunta → LLM analiza → LLM decide llamar tool → framework ejecuta 
  el metodo → resultado vuelve al LLM → LLM formula respuesta final
- `@Tool` describe la funcion al LLM; `@P` describe el parametro al LLM

### G2 — Debe mencionar:
- El LLM decide, no un if/else programado. Es dinamico basado en lenguaje natural
- El LLM decide (con la informacion de @Tool y @P)
- Si no existe, el LLM no la llamara (no la conoce) o dara error si la inventa
- Ejemplo: "Que hora es y que clima hace?" → llama obtenerHora Y obtenerClima

### H1 — Debe mencionar:
- 8081 porque 8080 ya esta ocupado por el servidor LLM (llama.cpp)
- `base-url` apunta al servidor OpenAI-compatible (llama.cpp en localhost:8080)
- llama.cpp no valida API keys

### H2 — Debe incluir:
- `@RestController` con `@GetMapping("/chat")` y `@RequestParam String message`
- `prompt()` = inicia el prompt, `user(message)` = agrega mensaje de usuario, 
  `call()` = ejecuta la llamada, `content()` = extrae el texto de la respuesta
- Inyeccion por constructor (Spring DI), patron Builder
- `@Bean ChatMemory` con `MessageWindowChatMemory.builder().maxMessages(20).build()`

### H3 — Tabla esperada:
| Concepto | LangChain4j | Spring AI |
|----------|-------------|-----------|
| Cliente | `OpenAiChatModel` | `ChatClient` |
| Config | codigo Java (builder) | application.yml (auto-config) |
| Memoria | `MessageWindowChatMemory` | `MessageWindowChatMemory` (mismo concepto) |
| Llamada | `model.chat(prompt)` | `client.prompt().user(msg).call().content()` |
| AiServices/Tools | `AiServices` + `@Tool` | No tiene equivalente directo en Spring AI basico |
| Ventaja | Mas flexible, tools, AiServices | Auto-config, integracion Spring ecosistema |

### P1 — TokenizerV3 debe incluir:
- Regex de split igual a V1/V2
- `decode()`: al unir tokens, eliminar espacio antes de puntuacion 
  (ej: si token anterior es puntuacion o token actual empieza con puntuacion)
- `encodeWithCount()`: usar `HashMap<String, Integer>` con `.merge()` o `.getOrDefault()`
- `vocabularySize()`: `return strToInt.size()`

### P2 — BusquedaConFiltro debe incluir:
- Crear `AllMiniLmL6V2EmbeddingModel`
- Crear `InMemoryEmbeddingStore<TextSegment>`
- Agregar 5 `TextSegment` con sus embeddings via `store.add(embedding, textSegment)`
- `EmbeddingSearchRequest.builder().queryEmbedding(queryEmbedding).maxResults(3).minScore(0.5).build()`
- Iterar `result.matches()` e imprimir texto, score, y relevancia

### P3 — AsistenteMatematico debe incluir:
- 3 metodos `@Tool` con descripciones claras
- `@P` en parametros como "Primer numero", "Segundo numero", "Numero para calcular raiz"
- `@SystemMessage("Eres un asistente matematico...")`
- `AiServices.builder().chatModel(model).tools(new HerramientasMatematicas()).build()`
- El LLM deberia decidir llamar `multiplicar(15, 8)` y `raizCuadrada(144)` y `sumar(resultado1, resultado2)`
