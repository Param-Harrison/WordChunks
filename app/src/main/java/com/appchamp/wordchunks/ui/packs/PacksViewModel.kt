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

package com.appchamp.wordchunks.ui.packs

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.appchamp.wordchunks.models.realm.FINISHED
import com.appchamp.wordchunks.models.realm.IN_PROGRESS
import com.appchamp.wordchunks.models.realm.LOCKED
import com.appchamp.wordchunks.models.realm.Pack
import com.appchamp.wordchunks.realmdb.utils.LiveRealmResults
import com.appchamp.wordchunks.realmdb.utils.levelModel
import com.appchamp.wordchunks.realmdb.utils.packModel
import io.realm.Realm


class PacksViewModel(application: Application?) : AndroidViewModel(application) {

    private val dbRealm: Realm = Realm.getDefaultInstance()

    private var packs: LiveRealmResults<Pack>

    private var currentPack: Pack?

    init {
        // Retrieves all of the packs from the Realm as LiveRealmResults objects list.
        packs = dbRealm.packModel().findAllPacks()
        currentPack = dbRealm.packModel().findPackByState(IN_PROGRESS)
    }

    /**
     * Expose the LiveData Packs query so the UI can observe it.
     */
    fun getLivePacks(): LiveRealmResults<Pack> {
        if (currentPack != null) {
            currentPack?.id?.let {
                if (isPackSolved(it)) {
                    markPackSolved()
                    makeNewCurrentPack()
                }
            }
        } else {
            makeNewCurrentPack()
        }
        return packs
    }

    private fun markPackSolved() {
        currentPack?.let { dbRealm.packModel().setPackState(it, FINISHED) }
    }

    private fun makeNewCurrentPack() {
        val firstLockedPack = dbRealm.packModel().findPackByState(LOCKED)
        firstLockedPack?.let {
            dbRealm.packModel().setPackState(it, IN_PROGRESS)
            currentPack = firstLockedPack
        }
    }

    /**
     * Returns true if the pack was solved, and false otherwise.
     */
    private fun isPackSolved(packId: String): Boolean {
        val levels = dbRealm.levelModel().findLevelsByPackIdList(packId)
        return levels.all { it.state == FINISHED }
    }

    /**
     * This method helps the RecyclerView to determine the position of the last "current" or
     * "finished" pack state.
     */
    fun getLastPackPos() = packs.value?.indexOfLast {
        it.state == IN_PROGRESS
                || it.state == FINISHED
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
