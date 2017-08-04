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


//class AfterGameViewModel(application: Application, levelId: String) : AndroidViewModel(application) {
//    private val realmDb: Realm = Realm.getDefaultInstance()
//    private var level: LiveRealmObject<Level>
//    private var nextLevel: Level? = null
//    private var pack: Pack? = null
//    private var i: Int
//
//    init {
//        // Load level from the Realm realmDb as LiveData
//        level = realmDb.levelModel().findLevelById(levelId)
//        if (!isDailyLevel()) {
//            nextLevel = realmDb.levelModel().findLevelByState(LOCKED)
//            pack = level.value?.packId?.let { realmDb.packModel().findPackById(it) }
//        }
//        i = Random().nextInt(72 - 1)  //todo!!
//    }
//
//    fun getLevel(): LiveRealmObject<Level> = level
//
//    fun getLevelState(): Int = level.value?.state!!
//
//    fun isDailyLevel(): Boolean = level.value?.daily!!
//
//    fun getNextLevelId(): String? = nextLevel?.id
//
//    fun getLevelClue(): String? = nextLevel?.clue
//
//    fun getPackColor(): String = nextLevel?.color ?: "#febb5a"
//
//    fun getPackId(): String = pack?.id!!
//
//    fun getRand(): Int = i
//
//    /**
//     * Resets all level data.
//     */
//    private fun resetLevel() {
//        realmDb.wordModel().findWordsByLevelId(level.value?.id!!).value?.map {
//            realmDb.wordModel().setWordState(it, WORD_STATE_NOT_SOLVED)
//        }
//        realmDb.chunkModel().findChunksByLevelId(level.value?.id!!).value?.map {
//            realmDb.chunkModel().setChunkState(it, CHUNK_STATE_NORMAL)
//        }
//    }
//
//    private fun deleteLevelFromRealm() {
//        val id = level.value?.id!!
//        realmDb.chunkModel().deleteChunksByLevelId(id)
//        realmDb.wordModel().deleteWordsByLevelId(id)
//        realmDb.levelModel().deleteLevelById(id)
//    }
//
//    /**
//     * A creator is used to inject the level ID into the ViewModel.
//     */
//    class Factory(private val application: Application, private val levelId: String)
//        : ViewModelProvider.NewInstanceFactory() {
//
//        @Suppress("UNCHECKED_CAST")
//        override fun <T : ViewModel> create(modelClass: Class<T>): T {
//            return AfterGameViewModel(application, levelId) as T
//        }
//    }
//
//    /**
//     * This method will be called when this ViewModel is no longer used and will be destroyed.
//     *
//     * It is useful when ViewModel observes some data and you need to clear this subscription to
//     * prevent a leak of this ViewModel... Like RealmResults and the instance of Realm!
//     */
//    override fun onCleared() {
//        Log.d("AAAAAAAAAAA", "CLEARED AFTER GAME VIEW MODEL")
//        realmDb.where(Level::class.java).findAll().map {
//            Log.d("AAAAAAAAAAA", "LEVELLLLLLLLLL1 = " + it)
//        }
//        // No matter in what state level was solved, always reset it
//        if (isDailyLevel()) {
//            deleteLevelFromRealm()
//        } else {
//            resetLevel()
//        }
//        realmDb.where(Level::class.java).findAll().map {
//            Log.d("AAAAAAAAAAA", "LEVELLLLLLLLLL2 = " + it)
//        }
//        realmDb.close()
//        super.onCleared()
//    }
//}