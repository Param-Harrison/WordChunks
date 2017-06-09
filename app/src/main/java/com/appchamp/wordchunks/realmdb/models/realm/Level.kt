package com.appchamp.wordchunks.realmdb.models.realm

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required


open class Level : RealmObject() {

    @PrimaryKey
    @Required
    var id: String = ""
    @Required
    var packId: String = ""
    @Required
    var clue: String = ""
    @Required
    var color: String = ""
    @Required
    var fact: String = ""
    var state: Int = 0  // 0 = locked, 1 = current, 2 = finished
    var words: RealmList<Word> = RealmList()
    var chunks: RealmList<Chunk> = RealmList()

}
