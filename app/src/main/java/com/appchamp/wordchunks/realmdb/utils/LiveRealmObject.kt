/*
 * Copyright 2017 Julia Kozhukhovskaya
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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