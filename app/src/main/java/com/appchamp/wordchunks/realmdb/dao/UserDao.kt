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

package com.appchamp.wordchunks.realmdb.dao

import com.appchamp.wordchunks.models.realm.User
import com.appchamp.wordchunks.realmdb.utils.LiveRealmObject
import com.appchamp.wordchunks.realmdb.utils.asLiveData
import io.realm.Realm


class UserDao(private val realm: Realm) {

    fun createUser(): User? {
        var user: User? = null
        realm.executeTransaction {
            user = it.createObject(User::class.java)
        }
        return user
    }

    fun findUser(): LiveRealmObject<User>? = realm
            .where(User::class.java)
            .findFirst()
            ?.asLiveData()

    fun setUserHints(user: User, hints: Int) {
        realm.executeTransaction {
            user.hints = hints
        }
    }

    fun increaseUserHints(user: User, hintsNumber: Int) {
        realm.executeTransaction {
            user.hints += hintsNumber
        }
    }

    fun decreaseUserHints(user: User, hintsNumber: Int) {
        realm.executeTransaction {
            user.hints -= hintsNumber
        }
    }
}