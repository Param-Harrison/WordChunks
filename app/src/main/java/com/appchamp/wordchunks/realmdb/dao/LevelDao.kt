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

import com.appchamp.wordchunks.models.realm.Level
import com.appchamp.wordchunks.realmdb.utils.LiveRealmObject
import com.appchamp.wordchunks.realmdb.utils.LiveRealmResults
import com.appchamp.wordchunks.realmdb.utils.asLiveData
import com.appchamp.wordchunks.util.Constants.REALM_FIELD_ID
import com.appchamp.wordchunks.util.Constants.REALM_FIELD_PACK_ID
import com.appchamp.wordchunks.util.Constants.REALM_FIELD_STATE
import io.realm.Realm
import io.realm.RealmResults


class LevelDao(private val realm: Realm) {

    fun createOrUpdateLevelsFromJson(json: String) {
        realm.executeTransaction {
            it.createOrUpdateAllFromJson(Level::class.java, json)
//            it.where(Level::class.java).findAll().mapIndexed { i, level ->
//                // todo
//                // Temporary workaround
//                when (Locale.getDefault()) {
//                    Constants.SUPPORTED_LOCALES[1] -> level.title = "УРОВЕНЬ " + i.toString()
//                    else -> level.title = "LEVEL " + i.toString()
//                }
//            }
            // todo
//            it.where(Level::class.java).findAll().map { level ->
//                level.state = FINISHED
//            }
        }

    }

    fun findFirstLevel(): Level? = realm.where(Level::class.java).findFirst()

    /**
     * Custom finder methods.
     */

    fun findLevelsByPackId(packId: String): LiveRealmResults<Level> = realm
            .where(Level::class.java)
            .equalTo(REALM_FIELD_PACK_ID, packId)
            .findAllAsync()
            .asLiveData()

    fun findLevelsByPackIdList(packId: String): RealmResults<Level> = realm
            .where(Level::class.java)
            .equalTo(REALM_FIELD_PACK_ID, packId)
            .findAll()

    fun findAllLevelsLive(): LiveRealmResults<Level> = realm
            .where(Level::class.java)
            .equalTo("daily", false)
            .findAllAsync()
            .asLiveData()

    fun findAllLevelsList(): List<Level> = realm
            .where(Level::class.java)
            .equalTo("daily", false)
            .findAll()

    fun findLevelById(levelId: String): LiveRealmObject<Level> = realm
            .where(Level::class.java)
            .equalTo(REALM_FIELD_ID, levelId)
            .findFirst()
            .asLiveData()

    // Can be null
    fun findLevelByState(state: Int): Level? = realm
            .where(Level::class.java)
            .equalTo(REALM_FIELD_STATE, state)
            .equalTo("daily", false)
            .findFirst()

    /**
     * Custom set methods.
     */

    fun setLevelState(level: Level, state: Int) {
        realm.executeTransaction {
            level.state = state
        }
    }

    fun setLevelTitles(lang: String) {
        val levels = findAllLevelsList()
        realm.executeTransaction {
            levels.mapIndexed { index, level ->
                when (lang) {
                    "rus" -> level.title = "УРОВЕНЬ"
                    else -> level.title = "LEVEL"
                }
                level.title += " " + (index + 1).toString()

                // for debugging
//                level.state = 2
            }
        }
    }

    fun deleteLevelById(levelId: String) {
        realm.executeTransaction {
            it.where(Level::class.java)
                    .equalTo(REALM_FIELD_ID, levelId)
                    .findAll()
                    .deleteFirstFromRealm()
        }
    }
}