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


/**
 * GameViewModel shares the level ID between the GameActivity and GameFragment.
 */
class GameViewModel(application: Application, levelId: String) : AndroidViewModel(application) {
    private val TAG: String = javaClass.simpleName
    private val realmDb: Realm = Realm.getDefaultInstance()
    private var level: LiveRealmObject<Level>
    private var chunks: LiveRealmResults<Chunk>
    private var words: LiveRealmResults<Word>

    init {
        // Loads levels, chunks, and words from the Realm realmDb as LiveData
        level = realmDb.levelModel().findLevelById(levelId)
        chunks = realmDb.chunkModel().findChunksByLevelId(levelId)
        words = realmDb.wordModel().findWordsByLevelId(levelId)
    }

    fun getLiveLevel(): LiveRealmObject<Level> = level

    fun getPackId(): String? = level.value?.packId

    fun getLiveChunks(): LiveRealmResults<Chunk> = chunks

    fun getLiveWords(): LiveRealmResults<Word> = words

    fun getSelectedChunksString(): String = getSelectedChunks()?.chunksToString() ?: ""

    /**
     * Gets filtered and sorted by time-pressed chunks, and transforms them into string.
     */
    private fun getSelectedChunks(): List<Chunk>? {
        return getLiveChunks().value
                ?.filter { it.state > CHUNK_STATE_NORMAL }
                ?.sortedBy { it.state }
    }

    /**
     * Returns the length of the selected chunks.
     */
    fun getSelectedChunksLength(): Int = getSelectedChunksString().length

    fun getClearIconVisibility(): Boolean = getSelectedChunksLength() != 0

    /**
     * Flips chunk state on click, and stores it in the realmDb.
     *
     * @param chunk The clicked chunk object.
     */
    fun onChunkClick(chunk: Chunk) {
        if (chunk.state == CHUNK_STATE_NORMAL) {
            realmDb.chunkModel().setChunkState(chunk, System.currentTimeMillis())
        } else {
            realmDb.chunkModel().setChunkState(chunk, CHUNK_STATE_NORMAL)
        }
    }

    /**
     * Returns modified chunks indices
     */
    fun onClearClick(): List<Int> {
        val clickedIndices: MutableList<Int> = mutableListOf()
        getSelectedChunks()?.map { it ->
            realmDb.chunkModel().setChunkState(it, CHUNK_STATE_NORMAL)
            clickedIndices.add(it.position)
        }
        return clickedIndices
    }

    fun isWordSolved(): Int {
        getLiveWords().value
                ?.filter { it.state == WORD_STATE_NOT_SOLVED }
                ?.forEach {
                    if (it.word == getSelectedChunksString()) {
                        realmDb.wordModel().setWordState(it, WORD_STATE_SOLVED)
                        realmDb.wordModel().setWordVisibleLettersNum(it, it.word.length)
                        return it.position
                    }
                }
        return -1
    }

    /**
     * Returns true if the level is solved, and false otherwise.
     */
    fun isLevelSolved() = words.value
            ?.filter { it.state == WORD_STATE_NOT_SOLVED }?.isEmpty() ?: false

    /**
     * Returns the list of indices of the selected chunks to be updated by the adapter in the fragment.
     */
    fun onWordSolved(): List<Int> {
        val clickedIndices: MutableList<Int> = mutableListOf()
        getSelectedChunks()?.map { it ->
            realmDb.chunkModel().setChunkState(it, CHUNK_STATE_GONE)
            clickedIndices.add(it.position)
        }
        return clickedIndices
    }

    /**
     * This method is called whenever a user clicks on shuffle icon, it stores states of every
     * chunk in the RecyclerView in the realmDb.
     */
    fun onShuffleClick() {
        val numberOfChunks = getLiveChunks().value?.size
        numberOfChunks?.let {
            val shuffledArray = IntArray(numberOfChunks, { it }).shuffleIntArray()

            // Reduces the size for the efficiency
            val size = when {
                numberOfChunks.isEven() -> numberOfChunks / 2
                else -> numberOfChunks / 2 + 1
            }

            for (i in 0 until size) {
                val pos1 = shuffledArray[i]
                val pos2 = shuffledArray[numberOfChunks - i - 1]
                getLiveChunks().value?.let {
                    // Swapping chunk positions
                    it[pos1]?.let { chunk -> realmDb.chunkModel().setChunkPosition(chunk, pos2) }
                    it[pos2]?.let { chunk -> realmDb.chunkModel().setChunkPosition(chunk, pos1) }
                }
            }
        }
    }

    fun setupWordsPositions() {
        Log.d(TAG, "SETTING WORDS POSITIONS!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!")
        var wordPos = 0
        getLiveWords().value?.forEach {
            realmDb.wordModel().setWordPosition(it, wordPos++)
        }
    }

    fun isDailyLevel(): Boolean = level.value?.daily!!

    fun onHintClicked(): Int {
        getLiveWords().value
                ?.filter { it.state == WORD_STATE_NOT_SOLVED && it.visibleLettersNum < it.word.length }
                ?.minBy { it.visibleLettersNum }
                ?.let {
                    Log.d(TAG, "IT.WORD = " + it.word)
                    Log.d(TAG, "IT.POS = " + it.position)
                    realmDb.wordModel()
                            .setWordVisibleLettersNum(it, it.visibleLettersNum + 1)
                    return it.position
                }
        return -1
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