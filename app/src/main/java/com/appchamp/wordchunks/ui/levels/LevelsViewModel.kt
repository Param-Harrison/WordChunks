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
import com.appchamp.wordchunks.realmdb.models.realm.Level
import com.appchamp.wordchunks.realmdb.models.realm.LevelState
import com.appchamp.wordchunks.realmdb.utils.LiveRealmResults
import com.appchamp.wordchunks.realmdb.utils.asLiveData
import com.appchamp.wordchunks.realmdb.utils.levelModel
import io.realm.Realm


class LevelsViewModel(application: Application?) : AndroidViewModel(application) {

    private val db: Realm = Realm.getDefaultInstance()

    private var levels: LiveRealmResults<Level>
    private lateinit var packId: String

    init {
        // Load packs from realm db
        //packs = db.packModel().findAllPacks()
        levels = db.where(Level::class.java).findAllAsync().asLiveData()
    }

    fun getLevels(packId: String): LiveRealmResults<Level> {
        // Load levels from realm db
        levels = db.levelModel().findLevelsByPackId(packId)
        this.packId = packId
        return levels
    }

    fun getLastLevelPos() = levels.value?.indexOfLast {
        it.state == LevelState.IN_PROGRESS.value || it.state == LevelState.FINISHED.value } ?: 0

    /**
     * This method will be called when this ViewModel is no longer used and will be destroyed.
     *
     * It is useful when ViewModel observes some data and you need to clear this subscription to
     * prevent a leak of this ViewModel... Like RealmResults and the instance of Realm!
     */
    override fun onCleared() {
        db.close()
        super.onCleared()
    }
}