package com.appchamp.wordchunks.models.realm

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Index
import io.realm.annotations.PrimaryKey


open class Pack : RealmObject() {

    @PrimaryKey
    var id: String? = null// = UUID.randomUUID().toString();
    @Index
    var title: String? = null
    @Index
    var color: String? = null
    @Index
    var state: Int = 0  // 0 = locked, 1 = current, 2 = finished
    var levels: RealmList<Level>? = null

}
