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

package com.appchamp.wordchunks.ui.aftergame

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.appchamp.wordchunks.realmdb.models.realm.Level
import com.appchamp.wordchunks.realmdb.models.realm.LevelState
import com.appchamp.wordchunks.realmdb.models.realm.Pack
import com.appchamp.wordchunks.realmdb.models.realm.PackState
import com.appchamp.wordchunks.realmdb.utils.LiveRealmObject
import com.appchamp.wordchunks.realmdb.utils.levelModel
import com.appchamp.wordchunks.realmdb.utils.packModel
import io.realm.Realm
import java.util.*


class AfterGameViewModel(application: Application, levelId: String) : AndroidViewModel(application) {

    private val db: Realm = Realm.getDefaultInstance()
    // Live data
    private var level: LiveRealmObject<Level>
    private var nextLevel: Level?
    private var pack: Pack?
    private var i: Int

    init {
        // Load level from the Realm db as LiveData
        level = db.levelModel().findLevelById(levelId)
        nextLevel = db.levelModel().findLevelByState(LevelState.LOCKED.value)
        pack = level.value?.packId?.let { db.packModel().findPackById(it) }
        i = Random().nextInt(72 - 1)  //todo
    }

    fun getLevel(): LiveRealmObject<Level> = level

    fun getLevelState(): Int? {
        return level.value?.state
    }

    /**
     * Finds next locked level to play.
     */
    fun unlockNextLevel() {
        // If next level exists
        nextLevel?.let {
            // Set next level state as in progress
            db.levelModel().setLevelState(it, LevelState.IN_PROGRESS.value)

        }
    }

    fun getNextLevelId(): String? {
        return nextLevel?.id
    }

    /**
     * Resets all level data.
     */
    fun resetLevel() {
        level.value?.let {
            //  it.words.forEach { db.wordModel().setWordState(it, WordState.NOT_SOLVED.value) }
            // it.chunks.forEach { db.chunkModel().setChunkState(it, ChunkState.NORMAL.value) }
        }
    }

    /**
     * Returns true, if was solved the whole pack.
     */
    fun isPackSolved(): Boolean {
        if (pack?.state == PackState.IN_PROGRESS.value) {
//            if (pack?.levels?.count { it.state == PackState.IN_PROGRESS.value } == 0) {
//                // Changes pack state as "solved"
//                pack?.let {
//                    db.packModel().setPackState(it, PackState.FINISHED.value)
//                }
//                return true
//            }
        }
        return false
    }

    /**
     * Finds next locked pack to play.
     */
    fun unlockNextPack() {
        val nextPack = db.packModel().findPackByState(PackState.LOCKED.value)
        // Changes pack state as "in progress"
        nextPack?.let { db.packModel().setPackState(it, PackState.IN_PROGRESS.value) }
    }

    fun getLevelClue(): String? {
        return nextLevel?.clue
    }

    fun getPackColor(): String {
        return  nextLevel?.color ?: "#febb5a"
    }

    fun getFunFact(): String? {
        return level.value?.fact
    }
//
//    fun getLevelsLeft(): Int {
//        return pack?.levels?.count { it.state == PackState.LOCKED.value } as Int
//    }

    fun  getPackId(): String {
        return pack?.id!!
    }

    fun getRand(): Int {
        return i
    }

    /**
     * This method will be called when this ViewModel is no longer used and will be destroyed.
     *
     * It is useful when ViewModel observes some data and you need to clear this subscription to
     * prevent a leak of this ViewModel... Like RealmResults and the instance of Realm!
     */
    override fun onCleared() {
        if (getLevelState() == LevelState.IN_PROGRESS.value) {
            level.value?.let {
                db.levelModel().setLevelState(it, LevelState.FINISHED.value)
            }
            unlockNextLevel()
            if (isPackSolved()) {
                unlockNextPack()
            }
        }
        // No matter in what state level was solved, always reset it
        resetLevel()
        db.close()
        super.onCleared()
    }

    /**
     * A creator is used to inject the level ID into the ViewModel.
     */
    class Factory(private val application: Application, private val levelId: String)
        : ViewModelProvider.NewInstanceFactory() {

        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AfterGameViewModel(application, levelId) as T
        }
    }

}