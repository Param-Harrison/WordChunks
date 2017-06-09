package com.appchamp.wordchunks.ui.packs

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.content.Context
import android.content.Intent
import com.appchamp.wordchunks.realmdb.models.realm.Pack
import com.appchamp.wordchunks.realmdb.utils.LiveRealmData
import com.appchamp.wordchunks.realmdb.utils.packModel
import com.appchamp.wordchunks.ui.levels.LevelsActivity
import com.appchamp.wordchunks.util.Constants.EXTRA_PACK_ID
import io.realm.Realm
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor


/**
 * ViewModel - provides a way to create and retrieve objects that are bound to a specific
 * lifecycle. A ViewModel typically stores the state of a view's data and communicates
 * with other components, such as data repositories or the domain layer which handles
 * business logic. To read an introductory guide to this topic, see ViewModel.
 *
 * ViewModel's only responsibility is to manage the data for the UI. It should never access
 * your view hierarchy or hold a reference back to the Activity or the Fragment.
 */
class PacksViewModel(application: Application?) : AndroidViewModel(application) {

    private val db: Realm = Realm.getDefaultInstance()

    // LiveData which publicly exposes setValue(T) and postValue(T) method.
    private val selectedPackId = MutableLiveData<String>()

    private lateinit var packsList: LiveRealmData<Pack>

    fun getAdapter(): PacksLevelsAdapter<Pack> {
        return PacksLevelsAdapter {
            selectedPackId.value = it.id
        }
    }

    fun getPacks(): LiveRealmData<Pack> {
        packsList = db.packModel().findAllPacks()
        return packsList
    }

    fun getSelectedPackId(): LiveData<String> {
        return selectedPackId
    }

    /**
     * This method will be called when this ViewModel is no longer used and will be destroyed.
     *
     *
     * It is useful when ViewModel observes some data and you need to clear this subscription to
     * prevent a leak of this ViewModel... Like RealmResults and the instance of Realm!
     */
    override fun onCleared() {
        db.close()
        super.onCleared()
    }

    companion object {

        fun createIntent(context: Context, packId: String): Intent {
            return context.intentFor<LevelsActivity>(EXTRA_PACK_ID to packId).clearTop()
        }
    }
}
