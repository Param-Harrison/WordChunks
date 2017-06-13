package com.appchamp.wordchunks.realmdb.models.realm

import io.realm.RealmList
import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required


@RealmClass
open class Pack : RealmModel {

    @PrimaryKey
    @Required
    var id: String = ""
    @Required
    var title: String = ""
    @Required
    var color: String = ""
    var state: Int = 0  // LOCKED = 0, IN_PROGRESS = 1, FINISHED = 2
    var levels: RealmList<Level> = RealmList()

}


// States of pack
enum class PackState(val value: Int) {
    LOCKED(0),
    IN_PROGRESS(1),
    FINISHED(2)
}