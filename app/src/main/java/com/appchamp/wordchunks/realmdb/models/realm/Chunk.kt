package com.appchamp.wordchunks.realmdb.models.realm

import io.realm.RealmModel
import io.realm.annotations.RealmClass
import io.realm.annotations.Required


@RealmClass
open class Chunk : RealmModel {

    @Required
    var chunk: String = ""
    @Required
    var levelId: String = ""
    var wordId: String = ""
    var state: Long = 0  // should be Long because of the currentTimeMillis
    var position: Int = 0

}

fun List<Chunk>.chunksToString(): String = map { it.chunk }.joinToString(separator = "")

// States of chunk
enum class ChunkState(val value: Long) {
    NORMAL(0),
    GONE(-1)
}