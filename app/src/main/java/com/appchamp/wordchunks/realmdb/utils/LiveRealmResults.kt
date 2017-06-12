package com.appchamp.wordchunks.realmdb.utils

import android.arch.lifecycle.LiveData

import io.realm.RealmChangeListener
import io.realm.RealmModel
import io.realm.RealmResults


/**
 * Class connecting the Realm lifecycle to that of LiveData objects.
 */
class LiveRealmResults<T : RealmModel>(private val results: RealmResults<T>) :
        LiveData<RealmResults<T>>() {

    private val listener = RealmChangeListener<RealmResults<T>> {
        results -> value = results
    }

    override fun onActive() {
        super.onActive()
        results.addChangeListener(listener)
    }

    override fun onInactive() {
        super.onInactive()
        results.removeChangeListener(listener)
    }
}