package com.appchamp.wordchunks.models.realm

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey


open class Level : RealmObject() {

    @PrimaryKey
    var id: String? = null
    @Index
    var packId: String? = null
    @Index
    var clue: String? = null
    @Index
    var fact: String? = null
    @Index
    var state: Int = 0  // 0 = locked, 1 = current, 2 = finished
    var words: RealmList<Word>? = null
    var chunks: RealmList<Chunk>? = null

}
