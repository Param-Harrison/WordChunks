# WordChunks [beta]

<img alt='WordChunks Game' src='https://github.com/jkozh/WordChunks/blob/master/images/logo.jpg' width='80%'/>

### Get it on Google Play
<a href='https://play.google.com/store/apps/details?id=com.appchamp.wordchunks&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' width=20%/></a>

Find hidden words in a grid of chunks.
The six hidden words are broken into chunks of two, three or four letters and shuffled in a grid. You have to tap the chunks in the correct order to unscramble the words. There is no time limit and you can find the words in any order. 

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

## License

    Copyright 2017 Julia Kozhkhovskaya

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
