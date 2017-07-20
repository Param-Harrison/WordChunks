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

import com.appchamp.wordchunks.models.realm.Word
import com.appchamp.wordchunks.realmdb.utils.LiveRealmObject
import com.appchamp.wordchunks.realmdb.utils.LiveRealmResults
import com.appchamp.wordchunks.realmdb.utils.asLiveData
import com.appchamp.wordchunks.util.Constants
import io.realm.Realm


class WordDao(private val realm: Realm) {

    fun createOrUpdateWordsFromJson(json: String) {
        realm.executeTransaction { realm ->
            realm.createOrUpdateAllFromJson(Word::class.java, json)
        }
    }

    /**
     * Custom set methods.
     */
    fun setWordState(word: Word, state: Int) {
        realm.executeTransaction {
            word.state = state
        }
    }

    /**
     * Custom finder methods.
     */
    fun findWordsByLevelId(levelId: String): LiveRealmResults<Word> {
        return realm
                .where(Word::class.java)
                .equalTo(Constants.REALM_FIELD_LEVEL_ID, levelId)
                .findAllAsync()
                .asLiveData()
    }

    fun findWordById(id: String): LiveRealmObject<Word> = realm
            .where(Word::class.java)
            .equalTo(Constants.REALM_FIELD_ID, id)
            .findFirst()
            .asLiveData()
}