package com.appchamp.wordchunks.realmdb.dao

import com.appchamp.wordchunks.realmdb.models.realm.Pack
import com.appchamp.wordchunks.realmdb.utils.LiveRealmResults
import com.appchamp.wordchunks.realmdb.utils.asLiveData
import io.realm.Realm


class PackDao(private val realm: Realm) {


    /**
     * Custom finder methods.
     */

    fun findAllPacks(): LiveRealmResults<Pack> {
        return realm.where(Pack::class.java).findAllAsync().asLiveData()
    }

}
