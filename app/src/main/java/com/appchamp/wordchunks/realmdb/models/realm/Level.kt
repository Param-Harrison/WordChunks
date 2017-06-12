package com.appchamp.wordchunks.realmdb.models.realm

import io.realm.RealmList
import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required


@RealmClass
open class Level : RealmModel {

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
    var state: Int = 0  // LOCKED = 0, IN_PROGRESS = 1, FINISHED = 2
    var words: RealmList<Word> = RealmList()
    var chunks: RealmList<Chunk> = RealmList()

}

// States of level
enum class LevelState(val value: Int) {
    LOCKED(0),
    IN_PROGRESS(1),
    FINISHED(2)
}