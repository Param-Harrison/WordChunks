package com.appchamp.wordchunks.ui.packs

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import com.appchamp.wordchunks.realmdb.models.realm.Pack
import com.appchamp.wordchunks.realmdb.models.realm.PackState
import com.appchamp.wordchunks.realmdb.utils.LiveRealmResults
import com.appchamp.wordchunks.realmdb.utils.packModel
import io.realm.Realm


class PacksViewModel(application: Application?) : AndroidViewModel(application) {

    private val db: Realm = Realm.getDefaultInstance()

    private var packs: LiveRealmResults<Pack>

    init {
        // Load packs from realm db
        packs = db.packModel().findAllPacks()
    }

    fun getPacks(): LiveRealmResults<Pack> {
        return packs
    }

    fun getLastPackPos() = packs.value?.indexOfLast {
        it.state == PackState.IN_PROGRESS.value || it.state == PackState.FINISHED.value } ?: 0

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
