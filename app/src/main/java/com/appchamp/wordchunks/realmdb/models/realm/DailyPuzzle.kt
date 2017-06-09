package com.appchamp.wordchunks.realmdb.models.realm


import io.realm.RealmList
import io.realm.RealmObject


open class DailyPuzzle : RealmObject() {

    var clue: String? = null
    var isSolved: Boolean = false
    var words: RealmList<Word>? = null
    var chunks: RealmList<Chunk>? = null
}
