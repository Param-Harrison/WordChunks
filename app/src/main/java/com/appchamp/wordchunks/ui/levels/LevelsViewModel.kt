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

package com.appchamp.wordchunks.ui.levels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.appchamp.wordchunks.models.realm.*
import com.appchamp.wordchunks.realmdb.utils.LiveRealmResults
import com.appchamp.wordchunks.realmdb.utils.levelModel
import com.appchamp.wordchunks.realmdb.utils.wordModel
import io.realm.Realm


class LevelsViewModel(application: Application?) : AndroidViewModel(application) {

    private val dbRealm: Realm = Realm.getDefaultInstance()

    private lateinit var levels: LiveRealmResults<Level>

    private var currentLevel: Level?


    init {
        // Retrieves all of the pack's levels from the Realm as LiveRealmResults objects list.
        currentLevel = dbRealm.levelModel().findLevelByState(IN_PROGRESS)
    }

    fun getLiveLevels(id: String): LiveRealmResults<Level> {
        if (currentLevel != null) {
            currentLevel?.id?.let {
                if (isLevelSolved(it)) {
                    markLevelSolved()
                    makeNewCurrentLevel()
                }
            }
        } else {
            makeNewCurrentLevel()
        }
        levels = dbRealm.levelModel().findLevelsByPackId(id)
        return levels
    }

    private fun markLevelSolved() {
        currentLevel?.let { dbRealm.levelModel().setLevelState(it, FINISHED) }
    }

    private fun isLevelSolved(id: String): Boolean {
        val words = dbRealm.wordModel().findWordsByLevelIdList(id)
        return words.all { it.state == WORD_STATE_SOLVED }
    }

    private fun makeNewCurrentLevel() {
        val firstLockedLevel = dbRealm.levelModel().findLevelByState(LOCKED)
        firstLockedLevel?.let {
            dbRealm.levelModel().setLevelState(it, IN_PROGRESS)
            currentLevel = firstLockedLevel
        }
    }

    fun getLastLevelPos() = levels.value?.indexOfLast {
        it.state == IN_PROGRESS || it.state == FINISHED
    } ?: 0

    /**
     * This method will be called when this ViewModel is no longer used and will be destroyed.
     *
     * It is useful when ViewModel observes some data and you need to clear this subscription to
     * prevent a leak of this ViewModel... Like RealmResults and the instance of Realm!
     */
    override fun onCleared() {
        dbRealm.close()
        super.onCleared()
    }
}