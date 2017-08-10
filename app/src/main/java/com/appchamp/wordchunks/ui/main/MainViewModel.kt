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

package com.appchamp.wordchunks.ui.main

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.appchamp.wordchunks.models.gson.ChunkGson
import com.appchamp.wordchunks.models.gson.LevelGson
import com.appchamp.wordchunks.models.gson.PackGson
import com.appchamp.wordchunks.models.gson.WordGson
import com.appchamp.wordchunks.models.realm.*
import com.appchamp.wordchunks.realmdb.firebaseDao.fetchFirebaseList
import com.appchamp.wordchunks.realmdb.utils.*
import com.appchamp.wordchunks.util.Constants
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import io.realm.Realm
import java.util.*


class MainViewModel(application: Application?) : AndroidViewModel(application) {
    private val TAG: String = javaClass.simpleName
    private val realmDb: Realm = Realm.getDefaultInstance()
    private val firebaseDb: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val gson: Gson = Gson()
    private val isRealmLoaded = MutableLiveData<Boolean>()
    private var dailyPuzzleLevelId: String? = null
    private lateinit var levels: LiveRealmResults<Level>

    fun isRealmLoaded(): LiveData<Boolean> {
        isRealmLoaded.value?.let {
            if (it) {
                levels = realmDb.levelModel().findAllLevelsLive()
            }
        }
        return isRealmLoaded
    }

    fun getLevelId(): String {
        val firstLevelInProgress = realmDb.levelModel().findLevelByState(IN_PROGRESS)
        if (firstLevelInProgress == null) {
            val firstLevelLocked = realmDb.levelModel().findLevelByState(LOCKED)
            if (firstLevelLocked != null) {
                return firstLevelLocked.id
            }
        } else {
            return firstLevelInProgress.id
        }
        return ""
    }

    fun getDailyPuzzleLevelId() = dailyPuzzleLevelId

    fun fetchDailyLevel() {
        isRealmLoaded.postValue(false)
        firebaseDb.fetchFirebaseList<LevelGson>({
            loadLevelsIntoRealm(it)
            dailyPuzzleLevelId = it.last().id
            Log.d(TAG, "LAST ID" + it.last().id)
        },
                { Log.d(TAG, "ERROR:" + it) },
                getLang(),
                "daily/levels")
        firebaseDb.fetchFirebaseList<WordGson>(
                { loadWordsIntoRealm(it) },
                { Log.d(TAG, "ERROR:" + it) },
                getLang(),
                "daily/words")
        firebaseDb.fetchFirebaseList<ChunkGson>(
                { loadChunksIntoRealm(it) },
                { Log.d(TAG, "ERROR:" + it) },
                getLang(),
                "daily/chunks")
    }

    private fun getLang() = when (Locale.getDefault()) {
        Constants.SUPPORTED_LOCALES[1] -> "rus"
        else -> "eng"
    }

    fun fetchDataFromFirebase() {
        firebaseDb.fetchFirebaseList<PackGson>(
                { loadPacksIntoRealm(it) },
                { Log.d(TAG, "ERROR:" + it) },
                getLang(),
                "packs")
        firebaseDb.fetchFirebaseList<LevelGson>(
                { loadLevelsIntoRealm(it) },
                { Log.d(TAG, "ERROR:" + it) },
                getLang(),
                "levels")
        firebaseDb.fetchFirebaseList<WordGson>(
                { loadWordsIntoRealm(it) },
                { Log.d(TAG, "ERROR:" + it) },
                getLang(),
                "words")
        firebaseDb.fetchFirebaseList<ChunkGson>(
                { loadChunksIntoRealm(it) },
                { Log.d(TAG, "ERROR:" + it) },
                getLang(),
                "chunks")
    }

    private fun loadPacksIntoRealm(firebaseList: MutableList<PackGson>) {
        val json = gson.toJson(firebaseList)
        realmDb.packModel().createOrUpdatePacksFromJson(json)
        realmDb.packModel().setPackNumbers()
    }

    private fun loadLevelsIntoRealm(firebaseList: MutableList<LevelGson>) {
        val json = gson.toJson(firebaseList)
        realmDb.levelModel().createOrUpdateLevelsFromJson(json)
        realmDb.levelModel().setLevelTitles(getLang())
    }

    private fun loadWordsIntoRealm(firebaseList: MutableList<WordGson>) {
        val json = gson.toJson(firebaseList)
        realmDb.wordModel().createOrUpdateWordsFromJson(json)
    }

    private fun loadChunksIntoRealm(firebaseList: MutableList<ChunkGson>) {
        val json = gson.toJson(firebaseList)
        realmDb.chunkModel().createOrUpdateChunksFromJson(json)
        isRealmLoaded.postValue(true)
    }

    fun getCircularProgressValue(): Float {
        realmDb.where(Level::class.java).findAll().map {
            Log.d(TAG, "LEVEL====" + it)
        }
        realmDb.where(Word::class.java).findAll().map {
            Log.d(TAG, "WORD====" + it)
        }
        val levels = realmDb.where(Level::class.java)
                .equalTo("daily", false)
                .findAll()
        levels.count { it.state == FINISHED }
                .let { return it * 100F / (levels.size) }
    }

    /**
     * This method will be called when this ViewModel is no longer used and will be destroyed.
     *
     * It is useful when ViewModel observes some data and you need to clear this subscription to
     * prevent a leak of this ViewModel... Like RealmResults and the instance of Realm!
     */
    override fun onCleared() {
        realmDb.close()
        super.onCleared()
    }
}
