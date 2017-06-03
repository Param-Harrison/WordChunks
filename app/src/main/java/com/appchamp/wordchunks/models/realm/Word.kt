package com.appchamp.wordchunks.models.realm


import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import java.util.*

open class Word : RealmObject() {

    @PrimaryKey
    var id: String? = UUID.randomUUID().toString()
    @Index
    var word: String? = null
    @Index
    var state: Int = 0  // 0 = not solved, 1 = solved
    @Index
    var position: Int = 0
}