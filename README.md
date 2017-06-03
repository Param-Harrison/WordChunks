# WordChunks 

### Some pleasant surprises for me while refactoring Java -> Kotlin code:

---

> Java (250 characters)

```java
private String chunksToString(List<Chunk> chunks) {
  StringBuilder str = new StringBuilder();
  for (Chunk chunk : chunks) {
      str.append(chunk.getChunk());
  }
  return str.toString();
}

tvInputChunks.setText(chunksToString(chunks));
```

> Kotlin (extension method) (134 characters)

```kotlin
fun List<Chunk>.chunksToString(): String = map { it.chunk }.joinToString(separator = "")

tvInputChunks.text = chunks.chunksToString()
```

---
