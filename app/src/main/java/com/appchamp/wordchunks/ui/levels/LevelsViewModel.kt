package com.appchamp.wordchunks.ui.levels

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.Intent
import com.appchamp.wordchunks.realmdb.models.realm.Level
import com.appchamp.wordchunks.realmdb.utils.LiveRealmData
import com.appchamp.wordchunks.realmdb.utils.levelModel
import com.appchamp.wordchunks.ui.game.GameActivity
import com.appchamp.wordchunks.ui.packs.PacksLevelsAdapter
import com.appchamp.wordchunks.util.Constants.EXTRA_LEVEL_ID
import com.appchamp.wordchunks.util.Constants.EXTRA_PACK_ID
import io.realm.Realm
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor


class LevelsViewModel(application: Application?) : AndroidViewModel(application) {

    private val db: Realm = Realm.getDefaultInstance()

    private lateinit var packId: String

    // LiveData which publicly exposes setValue(T) and postValue(T) method.
    private val selectedLevelId = MutableLiveData<String>()

    fun getAdapter(): PacksLevelsAdapter<Level> {
        return PacksLevelsAdapter {
            selectedLevelId.value = it.id
        }
    }

    fun getLevels(): LiveRealmData<Level> {
        val levels = db.levelModel().findLevelsByPackId(packId)
        return levels
    }

    fun getSelectedLevelId(): LiveData<String> {
        return selectedLevelId
    }

    fun extractPackId(intent: Intent) {
        packId = intent.getStringExtra(EXTRA_PACK_ID)
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

    companion object {

        fun createIntent(context: Context, levelId: String): Intent {
            return context.intentFor<GameActivity>(EXTRA_LEVEL_ID to levelId).clearTop()
        }
    }
}