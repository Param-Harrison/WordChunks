package com.appchamp.wordchunks.ui.aftergame

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.appchamp.wordchunks.realmdb.models.realm.*
import com.appchamp.wordchunks.realmdb.utils.*
import io.realm.Realm
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info


class AfterGameViewModel(application: Application, levelId: String) : AndroidViewModel(application),
        AnkoLogger {

    private val db: Realm = Realm.getDefaultInstance()
    // Live data
    private var level: LiveRealmObject<Level>

    init {
        // Load level from the Realm db as LiveData
        level = db.levelModel().findLevelById(levelId)
    }

    fun getLevel(): LiveRealmObject<Level> = level

    fun getLevelState(): Int? {
        return level.value?.state
    }

    /**
     * Finds next locked level to play.
     */
    fun unlockNextLevel() {
        val nextLevel = db.levelModel().findLevelByState(LevelState.LOCKED.value)
        // If next level exists
        nextLevel?.let {
            // Set next level state as in progress
            db.levelModel().setLevelState(it, LevelState.IN_PROGRESS.value)

        }
    }

    fun getNextLevelId(): String? {
        val nextLevelId = db.levelModel().findLevelByState(LevelState.LOCKED.value)?.id
        info { "NEXT LEVEL ID=" + nextLevelId }
        return nextLevelId
    }

    /**
     * Resets all level data.
     */
    fun resetLevel() {
        level.value?.let {
            it.words.forEach { db.wordModel().setWordState(it, WordState.NOT_SOLVED.value) }
            it.chunks.forEach { db.chunkModel().setChunkState(it, ChunkState.NORMAL.value) }
        }
    }

    /**
     * Returns true, if was solved the whole pack.
     */
    fun isPackSolved(): Boolean {
        val pack = level.value?.packId?.let { db.packModel().findPackById(it) }

        if (pack?.state == PackState.IN_PROGRESS.value) {
            if (pack.levels.count { it.state == PackState.IN_PROGRESS.value } == 0) {
                // Changes pack state as "solved"
                db.packModel().setPackState(pack, PackState.FINISHED.value)
                return true
            }
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