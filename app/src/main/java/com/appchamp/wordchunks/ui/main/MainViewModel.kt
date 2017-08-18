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
import com.appchamp.wordchunks.models.realm.FINISHED
import com.appchamp.wordchunks.models.realm.IN_PROGRESS
import com.appchamp.wordchunks.models.realm.LOCKED
import com.appchamp.wordchunks.models.realm.Level
import com.appchamp.wordchunks.realmdb.firebaseDao.fetchFirebaseList
import com.appchamp.wordchunks.realmdb.utils.*
import com.appchamp.wordchunks.util.Constants.FIREBASE_CHUNKS_CHILD
import com.appchamp.wordchunks.util.Constants.FIREBASE_DAILY_CHILD
import com.appchamp.wordchunks.util.Constants.FIREBASE_LEVELS_CHILD
import com.appchamp.wordchunks.util.Constants.FIREBASE_PACKS_CHILD
import com.appchamp.wordchunks.util.Constants.FIREBASE_WORDS_CHILD
import com.appchamp.wordchunks.util.Constants.LANG_EN
import com.appchamp.wordchunks.util.Constants.LANG_RU
import com.appchamp.wordchunks.util.Constants.SUPPORTED_LOCALES
import com.appchamp.wordchunks.util.Constants.USER_INITIAL_HINTS
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

    fun getFirstLevelIdForTutorial(): String {
        return realmDb.levelModel().findFirstLevel()?.id!!
    }

    fun getLevel(): Level? {
        val firstLevelInProgress = realmDb.levelModel().findLevelByState(IN_PROGRESS)
        if (firstLevelInProgress == null) {
            val firstLevelLocked = realmDb.levelModel().findLevelByState(LOCKED)
            if (firstLevelLocked != null) {
                return firstLevelLocked
            }
        } else {
            return firstLevelInProgress
        }
        return null
    }

    fun getDailyPuzzleLevelId() = dailyPuzzleLevelId

    fun fetchDailyLevel() {
        isRealmLoaded.postValue(false)
        firebaseDb.fetchFirebaseList<LevelGson>(
                {
                    loadLevelsIntoRealm(it)
                    dailyPuzzleLevelId = it.last().id
                },
                { Log.d(TAG, "ERROR:" + it) },
                getLang(),
                FIREBASE_DAILY_CHILD + "/" + FIREBASE_LEVELS_CHILD)
        firebaseDb.fetchFirebaseList<WordGson>(
                { loadWordsIntoRealm(it) },
                { Log.d(TAG, "ERROR:" + it) },
                getLang(),
                FIREBASE_DAILY_CHILD + "/" + FIREBASE_WORDS_CHILD)
        firebaseDb.fetchFirebaseList<ChunkGson>(
                { loadChunksIntoRealm(it) },
                { Log.d(TAG, "ERROR:" + it) },
                getLang(),
                FIREBASE_DAILY_CHILD + "/" + FIREBASE_CHUNKS_CHILD)
    }

    private fun getLang() = when (Locale.getDefault()) {
        SUPPORTED_LOCALES[1] -> LANG_RU
        else -> LANG_EN
    }

    fun fetchDataFromFirebase() {
        firebaseDb.fetchFirebaseList<PackGson>(
                { loadPacksIntoRealm(it) },
                { Log.d(TAG, "ERROR:" + it) },
                getLang(),
                FIREBASE_PACKS_CHILD)
        firebaseDb.fetchFirebaseList<LevelGson>(
                { loadLevelsIntoRealm(it) },
                { Log.d(TAG, "ERROR:" + it) },
                getLang(),
                FIREBASE_LEVELS_CHILD)
        firebaseDb.fetchFirebaseList<WordGson>(
                { loadWordsIntoRealm(it) },
                { Log.d(TAG, "ERROR:" + it) },
                getLang(),
                FIREBASE_WORDS_CHILD)
        firebaseDb.fetchFirebaseList<ChunkGson>(
                { loadChunksIntoRealm(it) },
                { Log.d(TAG, "ERROR:" + it) },
                getLang(),
                FIREBASE_CHUNKS_CHILD)
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
        val levels = realmDb.levelModel().findAllLevelsList()
        levels.count { it.state == FINISHED }.let {
            return it * 100F / (levels.size)
        }
    }

    fun updateUser() {
        if (realmDb.userModel().findUser() == null) {
            val newUser = realmDb.userModel().createUser()
            newUser?.let { user ->
                realmDb.userModel().setUserHints(user, USER_INITIAL_HINTS)
            }
        }
    }

    fun isRealmDatabaseExists() = realmDb.levelModel().findFirstLevel() != null

    fun increaseHints(hintsNumber: Int) = realmDb.userModel().findUser()?.value?.let {
        realmDb.userModel().increaseUserHints(it, hintsNumber)
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
