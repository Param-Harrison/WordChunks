package com.appchamp.wordchunks.models.realm

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required


open class Pack : RealmObject() {

    @PrimaryKey
    @Required
    var id: String = ""
    @Index
    @Required
    var title: String = ""
    @Index
    @Required
    var color: String = ""
    @Index
    var state: Int = 0  // 0 = locked, 1 = current, 2 = finished
    var levels: RealmList<Level> = RealmList()

}
