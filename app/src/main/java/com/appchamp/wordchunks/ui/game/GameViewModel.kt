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

package com.appchamp.wordchunks.ui.game

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.util.Log
import com.appchamp.wordchunks.extensions.isEven
import com.appchamp.wordchunks.extensions.shuffleIntArray
import com.appchamp.wordchunks.models.realm.*
import com.appchamp.wordchunks.realmdb.utils.*
import io.realm.Realm


class GameViewModel(application: Application, levelId: String) : AndroidViewModel(application) {

    private val db: Realm = Realm.getDefaultInstance()
    // Live data
    private var level: LiveRealmObject<Level>
    private var chunks: LiveRealmResults<Chunk>
    private var words: LiveRealmResults<Word>

    init {
        // Load level from the Realm db as LiveData
        level = db.levelModel().findLevelById(levelId)
        // Load chunks from the Realm db as LiveData
        chunks = db.chunkModel().findChunksByLevelId(levelId)
        // Load words from the Realm db as LiveData
        words = db.wordModel().findWordsByLevelId(levelId)

        Log.d("MAIN", "INIT")
    }

    fun getLiveLevel(): LiveRealmObject<Level> = level

    fun getPackId(): String {
        return level.value?.packId!!
    }

    fun getLiveChunks(): LiveRealmResults<Chunk> {
        return chunks
    }

    fun getLiveWords(): LiveRealmResults<Word> = words

    /**
     * Gets filtered and sorted by time-pressed chunks, and transforms them into string.
     */
    fun getSelectedChunks(): List<Chunk>? {
        return getLiveChunks().value
                ?.filter { it.state > ChunkState.NORMAL.value }
                ?.sortedBy { it.state }
    }

    fun getSelectedChunksString(): String {
        return getSelectedChunks()?.chunksToString() ?: ""
    }

    /**
     * Returns the length of the selected chunks.
     */
    fun getSelectedChunksLength(): Int {
        return getSelectedChunksString().length
    }

    fun getClearIconVisibility(): Boolean {
        return getSelectedChunksLength() != 0
    }

    /**
     * Flips chunk state on click, and stores it in the db.
     *
     * @param chunk The clicked chunk object.
     */
    fun onChunkClick(chunk: Chunk) {
        if (chunk.state == ChunkState.NORMAL.value) {
            db.chunkModel().setChunkState(chunk, System.currentTimeMillis())
        } else {
            db.chunkModel().setChunkState(chunk, ChunkState.NORMAL.value)
        }
    }

    /**
     * Returns modified chunks indices
     */
    fun onClearClick(): List<Int> {
        val clickedIndices: MutableList<Int> = mutableListOf()
        getSelectedChunks()?.map { it ->
            db.chunkModel().setChunkState(it, ChunkState.NORMAL.value)
            clickedIndices.add(it.position)
        }
        return clickedIndices
    }

    fun isWordSolved(): Int {
        getLiveWords().value?.filter { it.state == WordState.NOT_SOLVED.value }
                ?.forEach {
                    if (it.word == getSelectedChunksString()) {
                        //removeChunks()
                        db.wordModel().setWordState(it, WordState.SOLVED.value)
                        return it.position
                    }
                }
        return -1
    }

    /**
     * Returns true if the level is solved, and false otherwise.
     */
    fun isLevelSolved(): Boolean {
        return words.value?.filter { it.state == WordState.NOT_SOLVED.value }?.isEmpty() ?: false
    }

    /**
     * Returns the list of indices of the selected chunks to be updated by the adapter in the fragment.
     */
    fun onWordSolved(): List<Int> {
        val clickedIndices: MutableList<Int> = mutableListOf()
        getSelectedChunks()?.map { it ->
            db.chunkModel().setChunkState(it, ChunkState.GONE.value)
            clickedIndices.add(it.position)
        }
        return clickedIndices
    }

    /**
     * This method is called whenever a user clicks on shuffle icon, it stores states of every
     * chunk in the RecyclerView in the db.
     */
    fun onShuffleClick() {
        val numberOfChunks = getLiveChunks().value?.size
        numberOfChunks?.let {
            val shuffledArray = IntArray(numberOfChunks, { it }).shuffleIntArray()

            // Reduced size for the efficiency
            val size = when {
                numberOfChunks.isEven() -> numberOfChunks / 2
                else -> numberOfChunks / 2 + 1
            }

            for (i in 0 until size) {
                val pos1 = shuffledArray[i]
                val pos2 = shuffledArray[numberOfChunks - i - 1]
                getLiveChunks().value?.let {
                    // Swapping chunk positions
                    it[pos1]?.let { chunk -> db.chunkModel().setChunkPosition(chunk, pos2) }
                    it[pos2]?.let { chunk -> db.chunkModel().setChunkPosition(chunk, pos1) }
                }
            }
        }
    }

//    fun isShowTutorial(): Boolean {
//        val game = db.gameModel().findGame()
//        return game.showTutorial
//    }

    /**
     * This method will be called when this ViewModel is no longer used and will be destroyed.
     *
     * It is useful when ViewModel observes some data and you need to clear this subscription to
     * prevent a leak of this ViewModel... Like RealmResults and the instance of Realm!
     */
    override fun onCleared() {
//        db.gameModel().setShowTutorial(db.gameModel().findGame(), false)
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
            return GameViewModel(application, levelId) as T
        }
    }
}