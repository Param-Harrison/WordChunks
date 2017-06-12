package com.appchamp.wordchunks.data

import com.appchamp.wordchunks.extensions.shuffleIntArray
import com.appchamp.wordchunks.realmdb.models.realm.Chunk
import com.appchamp.wordchunks.realmdb.models.realm.Word
import com.appchamp.wordchunks.util.Constants.CHUNKS_SEPARATOR
import io.realm.Realm
import io.realm.RealmList


/**
 * Chunks
 */
object ChunksDao {

    /**
     * Returns the list of Realm Chunk objects created from the array of split chunks.
     */
    internal fun createChunks(realm: Realm, wordsSplit: List<String>, wordsRealm: RealmList<Word>,
                              levelId: String): RealmList<Chunk> {
        val chunks = RealmList<Chunk>()
        for (i in wordsSplit.indices) {
            val splitChunks = wordsSplit[i].split(CHUNKS_SEPARATOR)
            for (chunkStr in splitChunks) {
                val chunk = realm.createObject(Chunk::class.java)
                chunk.chunk = chunkStr
                chunk.levelId = levelId
                chunk.wordId = wordsRealm[i].id
                chunks.add(chunk)
            }
        }
        val chunksSize = chunks.size
        val shuffledArray = IntArray(chunksSize, { it }).shuffleIntArray()
        val size = if (chunksSize % 2 == 0) chunksSize / 2 - 1 else chunksSize / 2
        for (i in 0..size) {
            chunks[shuffledArray[i]].position = shuffledArray[chunksSize - i - 1]
            chunks[shuffledArray[chunksSize - i - 1]].position = shuffledArray[i]
        }
        return chunks
    }
}