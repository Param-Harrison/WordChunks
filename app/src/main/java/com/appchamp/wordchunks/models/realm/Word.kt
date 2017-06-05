package com.appchamp.wordchunks.models.realm

import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.Required


open class Word : RealmObject() {

    @Index
    @Required
    var word: String = ""
    @Index
    var state: Int = 0  // 0 = not solved, 1 = solved
    @Index
    var position: Int = 0

    fun getProperIndex(): String = when (position) {
        0 -> "1"
        1 -> "4"
        2 -> "2"
        3 -> "5"
        4 -> "3"
        5 -> "6"
        else -> throw IllegalArgumentException("Invalid index param value")
    }
}