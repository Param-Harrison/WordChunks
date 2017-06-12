package com.appchamp.wordchunks.ui.game

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.appchamp.wordchunks.extensions.isEven
import com.appchamp.wordchunks.extensions.shuffleIntArray
import com.appchamp.wordchunks.realmdb.models.realm.*
import com.appchamp.wordchunks.realmdb.utils.*
import io.realm.Realm
import io.realm.RealmList
import org.jetbrains.anko.AnkoLogger


class GameViewModel(application: Application, levelId: String) : AndroidViewModel(application),
        AnkoLogger {

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
    }

    fun getLevel(): LiveRealmObject<Level> = level

    /**
     * To avoid conflicts between transactions while reading and writing chunks states,
     * I've separated them: liveData, and RealmList
     */
    fun getChunks(): RealmList<Chunk>? = level.value?.chunks

    fun getLiveChunks(): LiveRealmResults<Chunk> = chunks

    fun getLiveWords(): LiveRealmResults<Word> = words

    /**
     * Gets filtered and sorted by time-pressed chunks, and transforms them into string.
     */
    fun getSelectedChunks(): List<Chunk>? {
        return getChunks()
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
        level.value?.words?.filter { it.state == WordState.NOT_SOLVED.value }
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
        if (words.value?.filter { it.state == WordState.NOT_SOLVED.value }?.isEmpty()!!) {
            //resetLevel()
            return true
        }
        return false
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
        val numberOfChunks = getChunks()?.size
        numberOfChunks?.let {
            val shuffledArray = IntArray(numberOfChunks, { it }).shuffleIntArray()

            // Reduced size for the efficiency
            val size = when {
                numberOfChunks.isEven() -> numberOfChunks / 2
                else -> numberOfChunks / 2 + 1
            }

            for (i in 0..size - 1) {
                getChunks()?.get(shuffledArray[i])?.let { chunk ->
                    db.chunkModel().setChunkPosition(
                            chunk,
                            position = shuffledArray[numberOfChunks - i - 1])
                }
                getChunks()?.get(shuffledArray[numberOfChunks - i - 1])?.let { chunk ->
                    db.chunkModel().setChunkPosition(
                            chunk,
                            position = shuffledArray[i])
                }
            }
        }
    }

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
