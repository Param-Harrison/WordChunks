package com.appchamp.wordchunks.models.realm

import io.realm.RealmObject
import io.realm.annotations.Index


open class Chunk : RealmObject() {

    @Index
    var levelId: String? = null
    @Index
    var wordId: String? = null
    @Index
    var chunk: String? = null
    @Index
    var state: Long = 0  // must be Long as the currentTimeMillis
    @Index
    var position: Int = 0

}

fun List<Chunk>.chunksToString(): String = map { it.chunk }.joinToString(separator = "")
