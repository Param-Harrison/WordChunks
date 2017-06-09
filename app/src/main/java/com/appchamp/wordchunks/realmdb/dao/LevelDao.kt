package com.appchamp.wordchunks.realmdb.dao

import com.appchamp.wordchunks.realmdb.models.realm.Level
import com.appchamp.wordchunks.realmdb.utils.LiveRealmData
import com.appchamp.wordchunks.realmdb.utils.asLiveData
import com.appchamp.wordchunks.util.Constants.REALM_FIELD_PACK_ID
import io.realm.Realm


class LevelDao(private val realm: Realm) {

    /**
     * Custom finder methods.
     */

    fun findLevelsByPackId(packId: String): LiveRealmData<Level> = realm
            .where(Level::class.java)
            .equalTo(REALM_FIELD_PACK_ID, packId)
            .findAllAsync()
            .asLiveData()

}