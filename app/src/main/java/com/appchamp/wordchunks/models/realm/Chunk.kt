package com.appchamp.wordchunks.models.realm

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.Required


open class Chunk (

    @Index
    @Required
    var levelId: String = "",
    @Index
    @Required
    var chunk: String = "",
    @Index
    var state: Long = 0,  // should be Long as the currentTimeMillis
    @Index
    var position: Int = 0

): RealmObject()

fun List<Chunk>.chunksToString(): String = map { it.chunk }.joinToString(separator = "")
