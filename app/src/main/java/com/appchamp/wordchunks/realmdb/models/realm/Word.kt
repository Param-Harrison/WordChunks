package com.appchamp.wordchunks.realmdb.models.realm

import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required


@RealmClass
open class Word : RealmModel {

    @PrimaryKey
    @Required
    var id: String = ""
    @Required
    var levelId: String = ""
    @Required
    var word: String = ""
    var state: Int = 0  // 0 = not solved, 1 = solved
    var position: Int = 0

    fun getProperIndex() = when (position) {
        0 -> "1"
        1 -> "4"
        2 -> "2"
        3 -> "5"
        4 -> "3"
        5 -> "6"
        else -> throw IllegalArgumentException("Invalid index param value")
    }
}

// States of word
enum class WordState(val value: Int) {
    NOT_SOLVED(0),
    SOLVED(1)
}