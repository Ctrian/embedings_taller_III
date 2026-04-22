# Explicacion de las Clases del Tokenizador

## vision General

Este proyecto implementa un sistema de tokenizacion de texto similar al usado por LLMs como GPT. Las clases trabajan en conjunto para convertir texto a tokens numericos (encode) y viceversa (decode).

---

## 1. Pair.java

**Proposito:** Estructura de datos simple para representar un token en el vocabulario.

```java
public record Pair(Integer tokenId, String token) {}
```

**Como funciona:**
- Es un **record** de Java (clase inmutable con solo getters automaticos)
- Almacena un par: `tokenId` (Integer) y `token` (String)
- `tokenId`: identificador numerico unico del token
- `token`: la palabra o subpalabra real

**Ejemplo:** `Pair(0, "Hello")` significa que "Hello" tiene el ID 0.

---

## 2. Tokenizerv1.java

**Proposito:** Version basica del tokenizador que convierte texto a IDs y viseversa.

### Atributos:
```java
private Map<String, Integer> strToInt;  // palabra -> ID
private Map<Integer, String> intToStr;  // ID -> palabra
```

### Constructor:
```java
public Tokenizerv1(List<Pair> vocab)
```
Recibe una lista de `Pair` y construye dos mapas bidireccionales para busqueda rapida O(1).

### Metodo `encode` (texto -> IDs):
```java
public List<Integer> encode(String text)
```
1. Divide el texto usando regex: `(?=[,.:;?_!"()']|--|\\s)|(?<=[,.:;?_!"()']|--|\\s)`
2. Limpia cada token (trim, filtra vacios)
3. Busca el ID en `strToInt`
4. **Problema:** Si un token no existe, retorna `null`

### Metodo `decode` (IDs -> texto):
```java
public String decode(List<Integer> ids)
```
1. Convierte cada ID a texto usando `intToStr`
2. Une todos los tokens con espacios

---

## 3. Tokenizerv2.java

**Proposito:** Version mejorada del tokenizador que maneja tokens desconocidos.

### Diferencia clave con V1:
- V1: Si un token no existe, retorna `null` -> pierde informacion
- V2: Si un token no existe, usa el token especial `<|unk|>` como fallback

### Metodo `encode` mejorado:
```java
.map(token -> strToInt.getOrDefault(token, strToInt.get("<|unk|>")))
```

Esto significa:
- Si el token existe -> usa su ID normal
- Si no existe -> usa el ID de `<|unk|>` (token para contenido desconocido)

### Vocabulario extendido incluye:
- `<|unk|>`: Token para texto/desconocidos
- `<|endoftext|>`: Token para separar documentos/textos diferentes

---

## 4. TestTokenizerMain.java

**Proposito:** Clase de prueba que demuestra el funcionamiento y genera los vocabularios.

### Metodo `vocabulary()`:
1. Lee un archivo de texto
2. Lo divide en tokens usando la misma regex
3. Extrae palabras unicas y las ordena alfabeticamente
4. Asigna IDs secuenciales (0, 1, 2, ...)
5. Retorna `List<Pair>`

### Metodo `vocabularioExtendido()`:
1. Hace lo mismo que `vocabulary()`
2. **Agrega al final:** `<|endoftext|>` y `<|unk|>`
3. Esto permite manejar multiples documentos y tokens desconocidos

### `main()` - Demostracion:

**Prueba V1:**
```java
var vocab = vocabulary(PATH);
TokenizerV1 tokenizer = new TokenizerV1(vocab);
var ids = tokenizer.encode(text);
tokenizer.decode(ids);  // reconstruye el texto
```

**Prueba V2:**
```java
var vocabExt = vocabularioExtendido(PATH);
TokenizerV2 tokenizer2 = new TokenizerV2(vocabExt);
// Permite combinar textos con <|endoftext|> entre ellos
var textCombinado = text1 + " <|endoftext|> " + text2;
tokenizer2.encode(textCombinado);
```

---

## Diagrama de Conexiones

```
┌─────────────────────────────────────────────────────────────────┐
│                      TestTokenizerMain.java                     │
│                                                                 │
│  ┌─────────────────────┐     ┌────────────────────────────────┐ │
│  │  vocabulary()       │     │  vocabularioExtendido()        │ │
│  │  - Lee archivo      │     │  - Lee archivo                 │ │
│  │  - Tokeniza texto   │     │  - Tokeniza texto              │ │
│  │  - Crea vocab basico│     │  - Crea vocab + <|unk|>        │ │
│  │    (solo palabras)  │     │    + <|endoftext|>             │ │
│  └──────────┬──────────┘     └──────────┬─────────────────────┘ │
│             │                           │                       │
│             │  List<Pair>               │  List<Pair>           │
│             │                           │                       │
│             ▼                           ▼                       │
│  │  TokenizerV1     │         │  TokenizerV2         │          │
│  ┌──────────────────┐         ┌──────────────────────┐          │ 
│  │                  │         │                      │          │
│  │  strToInt Map    │         │  strToInt Map        │          │
│  │  intToStr Map    │         │  intToStr Map        │          │
│  │                  │         │                      │          │
│  │  encode() ───────┼───────▶│  encode()            │          │
│  │  (null si falta) │         │  (<|unk|> si falta)  │          │
│  │                  │         │                      │          │
│  │  decode()        │         │  decode()            │          │
│  └──────────────────┘         └──────────────────────┘          │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘
```

---

## Flujo Completo de Tokenizacion

### Encode (Texto -> Numeros):
```
"Hola mundo" 
    │
    ▼
┌────────────────────────────┐
│ 1. Split con regex        │
│    → ["Hola", " ", "mundo"]│
└────────────┬───────────────┘
             │
             ▼
┌────────────────────────────┐
│ 2. Trim y filtrar vacios  │
│    → ["Hola", "mundo"]     │
└────────────┬───────────────┘
             │
             ▼
┌────────────────────────────┐
│ 3. Buscar ID en mapa      │
│    "Hola"  → 0             │
│    "mundo" → 1             │
└────────────┬───────────────┘
             │
             ▼
       [0, 1]  (List<Integer>)
```

### Decode (Numeros -> Texto):
```
[0, 1]
    │
    ▼
┌────────────────────────────┐
│ 1. Convertir cada ID      │
│    0 → "Hola"              │
│    1 → "mundo"             │
└────────────┬───────────────┘
             │
             ▼
┌────────────────────────────┐
│ 2. Unir con espacios      │
│    → "Hola mundo"          │
└────────────────────────────┘
```

---

## Comparacion V1 vs V2

| Caracteristica         | TokenizerV1              | TokenizerV2                      |
|------------------------|--------------------------|----------------------------------|
| Tokens desconocidos    | Retorna `null`           | Usa `<|unk|>`                    |
| Vocabulario basico     | Solo palabras del texto  | + tokens especiales              |
| Manejo de texto mixto  | Problemas si hay tokens  | Maneja cualquier texto           |
|                      | no reconocidos           | gracefully                       |
| Uso recomendado        | Vocabularios cerrados    | Vocabularios abiertos/reales     |

---

## Regex de Tokenizacion Explicado

```java
String regex = "(?=[,.:;?_!\"()']|--|\\s)|(?<=[,.:;?_!\"()']|--|\\s)";
```

Esta regex divide el texto en **lookahead** y **lookbehind**:

- `(?=[...])`: Divide ANTES de caracteres especiales o espacios
- `(?<=[...])`: Divide DESPUES de caracteres especiales o espacios

**Caracteres detectados:**
- Puntuacion: `, . : ; ? _ ! " ( ) '`
- Guiones: `--`
- Espacios: `\s`

**Ejemplo:**
```
"Hello, world!"
    ↓
["Hello", ",", "world", "!"]
```