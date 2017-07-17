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
import com.appchamp.wordchunks.realmdb.firebaseDao.fetchFirebaseList
import com.appchamp.wordchunks.realmdb.models.firebase.UserFirebase
import com.appchamp.wordchunks.realmdb.models.gson.ChunkGson
import com.appchamp.wordchunks.realmdb.models.gson.LevelGson
import com.appchamp.wordchunks.realmdb.models.gson.PackGson
import com.appchamp.wordchunks.realmdb.models.gson.WordGson
import com.appchamp.wordchunks.realmdb.models.realm.Chunk
import com.appchamp.wordchunks.realmdb.models.realm.LevelState
import com.appchamp.wordchunks.realmdb.models.realm.User
import com.appchamp.wordchunks.realmdb.models.realm.Word
import com.appchamp.wordchunks.realmdb.utils.chunkModel
import com.appchamp.wordchunks.realmdb.utils.levelModel
import com.appchamp.wordchunks.realmdb.utils.packModel
import com.appchamp.wordchunks.realmdb.utils.wordModel
import com.appchamp.wordchunks.util.Constants
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.gson.Gson
import io.realm.Realm
import java.util.*


class MainViewModel(application: Application?) : AndroidViewModel(application) {

    private val TAG: String = javaClass.simpleName
    private val realmDb: Realm = Realm.getDefaultInstance()
    private val firebaseDb: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val gson: Gson = Gson()
    private val isRealmLoaded = MutableLiveData<Boolean>()

    fun isRealmLoaded(): LiveData<Boolean> {
        return isRealmLoaded
    }

    fun getLevelId() = realmDb.levelModel().findLevelByState(LevelState.IN_PROGRESS.value)?.id

    private fun getLang() = when (Locale.getDefault()) {
        Constants.SUPPORTED_LOCALES[0] -> "eng"
        Constants.SUPPORTED_LOCALES[1] -> "rus"
        else -> "eng"
    }

    init {
        firebaseDb.fetchFirebaseList<PackGson>(
                { loadPacksIntoRealm(it) },
                { Log.d(TAG, "ERROR:" + it) },
                getLang(),
                "packs"
        )
        firebaseDb.fetchFirebaseList<LevelGson>(
                { loadLevelsIntoRealm(it) },
                { Log.d(TAG, "ERROR:" + it) },
                getLang(),
                "levels"
        )
        firebaseDb.fetchFirebaseList<WordGson>(
                { loadWordsIntoRealm(it) },
                { Log.d(TAG, "ERROR:" + it) },
                getLang(),
                "words"
        )
        firebaseDb.fetchFirebaseList<ChunkGson>(
                { loadChunksIntoRealm(it) },
                { Log.d(TAG, "ERROR:" + it) },
                getLang(),
                "chunks"
        )
    }

    private fun loadPacksIntoRealm(firebaseList: MutableList<PackGson>) {
        val json = gson.toJson(firebaseList)
        Log.d(TAG, "PACKS AS GSON = " + json)
        realmDb.packModel().createOrUpdatePacksFromJson(json)
//        packsLiveData = realmDb.packModel().findAllPacks()
    }

    private fun loadLevelsIntoRealm(firebaseList: MutableList<LevelGson>) {
        val json = gson.toJson(firebaseList)
        Log.d(TAG, "LEVELS AS GSON = " + json)
        realmDb.levelModel().createOrUpdateLevelsFromJson(json)
    }

    private fun loadWordsIntoRealm(firebaseList: MutableList<WordGson>) {
        val json = gson.toJson(firebaseList)
        Log.d(TAG, "WORDS GSON = " + json)
        realmDb.wordModel().createOrUpdateWordsFromJson(json)
        // For debugging purposes
        realmDb.where(Word::class.java).findAll().forEach {
            Log.d(TAG, it.toString())
        }
    }

    private fun loadChunksIntoRealm(firebaseList: MutableList<ChunkGson>) {
        val json = gson.toJson(firebaseList)
        Log.d(TAG, "CHUNKS GSON = " + json)
        realmDb.chunkModel().createOrUpdateChunksFromJson(json)
        // For debugging purposes
        realmDb.where(Chunk::class.java).findAll().forEach {
            Log.d(TAG, it.toString())
        }
        isRealmLoaded.postValue(true)
    }

    fun writeNewUser(firebaseUser: FirebaseUser) {
        val userId = firebaseUser.uid
        val userEmail = firebaseUser.email ?: ""
        val user = UserFirebase(userId, userEmail, 12, 1)
        firebaseDb.child("users").child(userId).child(getLang()).setValue(user)
    }

    fun fetchFirebaseUser(uid: String) {
        firebaseDb.child("users").child(uid).child(getLang())
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        println(snapshot.value)
                        val user = snapshot.getValue(UserFirebase::class.java)
                        Log.d(TAG, "USER FETCHED FROM FIREBASE = " + user.toString())
                        val realmUser = User()
                        realmUser.id = user?.id ?: ""
                        realmUser.email = user?.email ?: ""
                        realmUser.hints = user?.hints ?: 0
                        realmUser.levelsSolved = user?.levelsSolved ?: 0
                        realmDb.executeTransaction {
                            it.copyToRealmOrUpdate(realmUser)
                        }

                    }

                    override fun onCancelled(databaseError: DatabaseError) {}
                })
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