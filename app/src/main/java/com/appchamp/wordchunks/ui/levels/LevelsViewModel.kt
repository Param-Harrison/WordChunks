package com.appchamp.wordchunks.ui.levels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.appchamp.wordchunks.realmdb.models.realm.Level
import com.appchamp.wordchunks.realmdb.models.realm.LevelState
import com.appchamp.wordchunks.realmdb.utils.LiveRealmResults
import com.appchamp.wordchunks.realmdb.utils.levelModel
import io.realm.Realm


class LevelsViewModel(application: Application?) : AndroidViewModel(application) {

    private val db: Realm = Realm.getDefaultInstance()

    private lateinit var levels: LiveRealmResults<Level>
    private lateinit var packId: String

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