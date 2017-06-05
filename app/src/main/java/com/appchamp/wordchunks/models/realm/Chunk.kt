package com.appchamp.wordchunks.models.realm

import io.realm.RealmObject
import io.realm.annotations.Required


open class Chunk (

    @Required
    var chunk: String = "",
    var state: Long = 0,  // should be Long as the currentTimeMillis
    var position: Int = 0

): RealmObject()

fun List<Chunk>.chunksToString(): String = map { it.chunk }.joinToString(separator = "")
