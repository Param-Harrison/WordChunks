package com.appchamp.wordchunks.models.realm

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required


open class Level : RealmObject() {

    @PrimaryKey
    @Required
    var id: String = ""
    @Index
    @Required
    var packId: String = ""
    @Index
    @Required
    var clue: String = ""
    @Index
    @Required
    var color: String = ""
    @Index
    @Required
    var fact: String = ""
    @Index
    var state: Int = 0  // 0 = locked, 1 = current, 2 = finished
    var words: RealmList<Word> = RealmList()
    var chunks: RealmList<Chunk> = RealmList()

}
