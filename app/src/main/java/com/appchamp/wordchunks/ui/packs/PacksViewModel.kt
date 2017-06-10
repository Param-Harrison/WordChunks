package com.appchamp.wordchunks.ui.packs

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.appchamp.wordchunks.realmdb.models.realm.Pack
import com.appchamp.wordchunks.realmdb.utils.LiveRealmData
import com.appchamp.wordchunks.realmdb.utils.packModel
import com.appchamp.wordchunks.util.Constants.STATE_CURRENT
import com.appchamp.wordchunks.util.Constants.STATE_SOLVED
import io.realm.Realm


class PacksViewModel(application: Application?) : AndroidViewModel(application) {

    private val db: Realm = Realm.getDefaultInstance()

    private lateinit var packs: LiveRealmData<Pack>

    fun getPacks(): LiveRealmData<Pack> {
        // Load packs from realm db
        packs = db.packModel().findAllPacks()
        return packs
    }

    fun getLastPackPos() = packs.value
            ?.indexOfLast { it.state == STATE_CURRENT || it.state == STATE_SOLVED } ?: 0

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
