package com.appchamp.wordchunks.realmdb.utils

import android.arch.lifecycle.LiveData
import android.support.annotation.MainThread
import io.realm.RealmModel
import io.realm.RealmObject
import io.realm.RealmObjectChangeListener


/**
 * Class connecting the Realm lifecycle to that of LiveData object.
 */
class LiveRealmObject<T : RealmModel> @MainThread
constructor(_object: T) : LiveData<T>() {

    private val listener = RealmObjectChangeListener<T> {
        obj, _ -> value = obj
    }

    init {
        value = _object
    }

    override fun onActive() {
        super.onActive()
        RealmObject.addChangeListener(value, listener)
    }

    override fun onInactive() {
        super.onInactive()
        RealmObject.removeChangeListener(value, listener)
    }
}