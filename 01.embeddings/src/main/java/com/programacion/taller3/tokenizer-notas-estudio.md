# Notas de Estudio: Sistema de Tokenización

## Visión General

El sistema implementa un tokenizador básico tipo GPT (Byte Pair Encoding simplificado) con dos versiones:
- **V1**: Vocabulario básico solo con palabras del texto
- **V2**: Vocabulario extendido con tokens especiales para manejo de múltiples textos

---

## 1. Pair.java (Estructura de Datos)

```java
public record Pair(Integer tokenId, String token) {}
```

### Propósito
- **Record**: clase inmutable con automática generación de `equals()`, `hashCode()`, `toString()`
- **tokenId**: identificador numérico único para cada token
- **token**: la palabra o símbolo original

### Uso
Sirve como unidad básica del vocabulario, representando la relación **token ↔ ID numérico**

---

## 2. TokenizerV1.java

### Constructor
```java
public TokenizerV1(List<Pair> vocab)
```
Recibe una lista de `Pair` y construye dos mapas:
- `strToInt`: Map<String, Integer> → palabra → ID
- `intToStr`: Map<Integer, String> → ID → palabra

### Métodos

#### encode(String text) → List<Integer>
1. Separa el texto usando regex: `(?=[,.:;?_!"()']|--|\s)|(?<=[,.:;?_!"()']|--|\s)`
   - Esta regex divide en cada delimitador Y lo mantiene
2. Limpia tokens: trim() + filter vacíos
3. Convierte cada token a su ID usando `strToInt.get(token)`
4. Filtra tokens no encontrados (devuelve null)

#### decode(List<Integer> ids) → String
1. Convierte cada ID a su token usando `intToStr.get(id)`
2. Une todos los tokens con espacio

### Limitación
- Si un token no está en el vocabulario, **se ignora** (no aparece en el resultado)

---

## 3. TokenizerV2.java

### Diferencia Principal
Maneja tokens **desconocidos** usando token especial `<|unk|>`

### Constructor
Igual que V1, pero el vocabulario debe contener `<|unk|>`

### Métodos

#### encode(String text)
```java
.map(token -> strToInt.getOrDefault(token, strToInt.get("<|unk|>")))
```
- Si el token no existe → usa el ID de `<|unk|>` en lugar de ignorarlo

#### decode(List<Integer> ids)
Idéntico a V1

---

## 4. TestTokenizerMain.java

### Métodos Estáticos

#### vocabulary(filename)
1. Lee archivo de texto
2. Aplica regex para dividir en tokens
3. Limpia: trim + filter vacíos
4. Obtiene palabras distintas y las ordena
5. Asigna IDs secuenciales (0, 1, 2, ...)
6. Retorna `List<Pair>`

#### vocabularioExtendido(filename)
Igual que `vocabulary()`, pero agrega al final:
- `<|endoftext|>` - marca fin de documento
- `<|unk|>` - token para palabras desconocidas

### Flujo de Prueba

```
1. Genera vocabulario básico → TokenizerV1
2. Prueba encode: "It's the last he painted..." → IDs
3. Prueba decode: IDs → texto reconstruido

4. Genera vocabulario extendido → TokenizerV2
5. Combina dos textos: "Hello... <|endoftext|> In the sunlit..."
6. Prueba encode/decode con múltiples documentos
```

---

## Conexión entre Clases

```
┌─────────────────────────────────────────────────────────┐
│                   TestTokenizerMain                     │
│  - Genera vocabularios (vocabulary, vocabularioExtendido)
│  - Crea instancias de Tokenizer                         │
│  - Ejecuta pruebas                                      │
└──────────────────────┬──────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────┐
│                       Pair                              │
│  - tokenId: Integer                                     │
│  - token: String                                        │
│  - Record inmutable                                     │
└──────────────────────┬──────────────────────────────────┘
                       │
                       ▼
┌──────────────────────┬──────────────────────────────────┐
│      TokenizerV1     │         TokenizerV2              │
│  - strToInt (Map)    │  - strToInt (Map)                │
│  - intToStr (Map)    │  - intToStr (Map)               │
│  - encode() → IDs   │  - encode() con <|unk|>          │
│  - decode() → texto │  - decode() → texto              │
└─────────────────────────────────────────────────────────┘
```

---

## Regex Explicada

```regex
(?=[,.:;?_!"()']|--|\s)|(?<=[,.:;?_!"()']|--|\s)
```

**Partes:**
- `(?=...)` → lookahead positivo (divide ANTES del delimitador)
- `(?<=...)` → lookbehind positivo (divide DESPUÉS del delimitador)
- `[,.:;?_!"()']` → signos de puntuación
- `--` → guiones largos
- `\s` → espacios en blanco

**Ejemplo:** `"Hello, world!"`
- División antes de `,` → ["Hello", ","]
- División después de `,` → ["Hello", ""]
- Resultado: ["Hello", ",", "world", "!"]

---

## Casos de Uso

| Escenario | Versión | Comportamiento |
|-----------|---------|-----------------|
| Texto con palabras conocidas | V1 | Encode/decode perfecto |
| Token desconocido | V1 | **Se ignora** (se pierde) |
| Múltiples documentos | V2 | Usa `<|endoftext|>` como separador |
| Token fuera del vocabulario | V2 | Reemplaza con `<|unk|>` |

---

## Puntos Clave para Examen

1. **Pair**: estructura inmutable (record) para par ID-token
2. **TokenizerV1**: implementación básica, falla con tokens desconocidos
3. **TokenizerV2**: mejora con `getOrDefault()` para manejar unknowns
4. **Regex**: separación preservación de puntuación
5. **Flujo**: Main genera vocabulario → Pasa a Tokenizer → Tokenizer usa Pair para mapas