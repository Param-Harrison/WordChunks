package com.appchamp.wordchunks.realmdb.dao

import com.appchamp.wordchunks.realmdb.models.realm.Chunk
import com.appchamp.wordchunks.realmdb.utils.LiveRealmResults
import com.appchamp.wordchunks.realmdb.utils.asLiveData
import com.appchamp.wordchunks.util.Constants
import io.realm.Realm


class ChunkDao(private val realm: Realm) {


    /**
     * Custom set methods.
     */
    fun setChunkState(chunk: Chunk, state: Long) {
        realm.executeTransaction {
            chunk.state = state
        }
    }

    fun setChunkPosition(chunk: Chunk, position: Int) {
        realm.executeTransaction {
            chunk.position = position
        }
    }

    /**
     * Custom finder methods.
     */
    fun findChunksByLevelId(levelId: String): LiveRealmResults<Chunk> {
        return realm
                .where(Chunk::class.java)
                .equalTo(Constants.REALM_FIELD_LEVEL_ID, levelId)
                .findAllAsync()
                .asLiveData()
    }
}