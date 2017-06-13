package com.appchamp.wordchunks.realmdb.dao

import com.appchamp.wordchunks.realmdb.models.realm.Pack
import com.appchamp.wordchunks.realmdb.utils.LiveRealmResults
import com.appchamp.wordchunks.realmdb.utils.asLiveData
import com.appchamp.wordchunks.util.Constants
import io.realm.Realm


class PackDao(private val realm: Realm) {


    /**
     * Custom finder methods.
     */

    fun findAllPacks(): LiveRealmResults<Pack> = realm
            .where(Pack::class.java)
            .findAllAsync()
            .asLiveData()

    fun findPackById(packId: String): Pack = realm
            .where(Pack::class.java)
            .equalTo(Constants.REALM_FIELD_ID, packId)
            .findFirst()


    fun findPackByState(state: Int): Pack? = realm
            .where(Pack::class.java)
            .equalTo(Constants.REALM_FIELD_STATE, state)
            .findFirst()

    /**
     * Custom set methods.
     */

    fun setPackState(pack: Pack, state: Int) {
        realm.executeTransaction {
            pack.state = state
        }
    }
}
