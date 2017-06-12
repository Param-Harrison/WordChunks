package com.appchamp.wordchunks.ui.aftergame

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.appchamp.wordchunks.realmdb.models.realm.Level
import com.appchamp.wordchunks.realmdb.utils.LiveRealmObject
import com.appchamp.wordchunks.realmdb.utils.levelModel
import io.realm.Realm
import org.jetbrains.anko.AnkoLogger


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

    fun getLevelState(): Int {
        level.value?.state
        return -1
    }

    fun resetLevel() {

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
            return AfterGameViewModel(application, levelId) as T
        }
    }
}