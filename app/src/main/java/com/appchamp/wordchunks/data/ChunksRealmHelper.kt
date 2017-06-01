package com.appchamp.wordchunks.data

import com.appchamp.wordchunks.models.realm.Chunk
import com.appchamp.wordchunks.models.realm.Word
import com.appchamp.wordchunks.util.Constants.*
import com.appchamp.wordchunks.util.shuffleIntArray
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmResults


/**
 * Chunks
 */
object ChunksRealmHelper {

    /**
     * Returns the list of Realm Chunk objects created from the array of split chunks.
     */
    internal fun createChunks(realm: Realm, wordsSplit: List<String>,
                              levelId: String, wordsRealm: RealmList<Word>): RealmList<Chunk> {
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
        val shuffledArray = IntArray(chunksSize, { i -> (i) }).shuffleIntArray()
        val size = if (chunksSize % 2 == 0) chunksSize / 2 else chunksSize / 2 + 1

        for (i in 0..size - 1) {
            chunks[shuffledArray[i]].position = shuffledArray[chunksSize - i - 1]
            chunks[shuffledArray[chunksSize - i - 1]].position = shuffledArray[i]
        }
        return chunks
    }

    fun findSelectedChunksByLevelIdSorted(realm: Realm, levelId: String): List<Chunk> = realm
            .where(Chunk::class.java)
            .equalTo(REALM_FIELD_LEVEL_ID, levelId)
            .greaterThan(REALM_FIELD_STATE, CHUNK_STATE_NORMAL)
            .findAllSorted(REALM_FIELD_STATE)

    fun findSelectedChunksByLevelId(realm: Realm, levelId: String): RealmResults<Chunk> = realm
            .where(Chunk::class.java)
            .equalTo(REALM_FIELD_LEVEL_ID, levelId)
            .greaterThan(REALM_FIELD_STATE, CHUNK_STATE_NORMAL)
            .findAll()
}