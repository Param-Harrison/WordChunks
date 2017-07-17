WordChunks Word Game
====================

<img src='https://github.com/jkozh/WordChunks/blob/master/images/logo.jpg' width='400' align="right" hspace="20">

*Find hidden words in a grid of chunks.*

WordChunks is a word game that test your knowledge of dictionary and spelling. The six hidden words are broken into chunks of two, three or four letters and shuffled in a grid. You have to tap the chunks in the correct order to unscramble the words. There is no time limit and you can find the words in any order.

<a href='https://play.google.com/store/apps/details?id=com.appchamp.wordchunks&pcampaignid=MKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='Get it on Google Play' src='https://play.google.com/intl/en_us/badges/images/generic/en_badge_web_generic.png' width=20%/></a>

- Easy to play, hard to beat!
- Unlock levels and packsLiveData by solving them. The more you play, the better you get at it.
- Made with love, and with pets.
- No pay to play, no pay to win.

### Screenshots

<img src='https://github.com/jkozh/WordChunks/blob/master/images/Phone%20Screenshot%201.jpg' width=25% hspace="10"><img src='https://github.com/jkozh/WordChunks/blob/master/images/Phone%20Screenshot%202.jpg' width=25% hspace="10"><img src='https://github.com/jkozh/WordChunks/blob/master/images/Phone%20Screenshot%203.jpg' width=25% hspace="10">


<img src='https://github.com/jkozh/WordChunks/blob/master/images/Phone%20Screenshot%204.jpg' width=25% hspace="10"><img src='https://github.com/jkozh/WordChunks/blob/master/images/Phone%20Screenshot%205.jpg' width=25% hspace="10">


### Some pleasant surprises for me while refactoring Java -> Kotlin

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
