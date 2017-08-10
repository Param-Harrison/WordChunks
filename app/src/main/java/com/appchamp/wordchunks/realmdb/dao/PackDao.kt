/*
 * Copyright 2017 Julia Kozhukhovskaya
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appchamp.wordchunks.realmdb.dao

import com.appchamp.wordchunks.models.realm.Pack
import com.appchamp.wordchunks.realmdb.utils.LiveRealmResults
import com.appchamp.wordchunks.realmdb.utils.asLiveData
import com.appchamp.wordchunks.util.Constants
import io.realm.Realm


class PackDao(private val realm: Realm) {

    fun createOrUpdatePacksFromJson(json: String) {
        realm.executeTransaction { realm ->
            realm.createOrUpdateAllFromJson(Pack::class.java, json)
        }
    }

    /**
     * Custom finder methods.
     */

    fun findAllPacks(): LiveRealmResults<Pack> = realm
            .where(Pack::class.java)
            .findAllAsync()
            .asLiveData()

    fun findAllPacksList(): List<Pack> = realm
            .where(Pack::class.java)
            .findAll()

    fun findPackById(packId: String): Pack? = realm
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

    fun setPackNumbers() {
        val packs = findAllPacksList()
        realm.executeTransaction {
            packs.mapIndexed { index, pack ->
                pack.number = index
            }

        }
    }
}
