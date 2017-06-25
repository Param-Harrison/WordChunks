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

import io.realm.Realm
import io.realm.RealmConfiguration
import java.io.File




class RealmFactory {

    /* Realm
     * CAUTION: Be careful which thread you call this from, it is not Thread safe.
     */
    fun getRealmInstance(dbName: String): Realm {
        return Realm.getInstance(getRealmConfiguration(dbName))
    }

    /* RealmConfiguration */
    private fun getRealmConfiguration(dbName: String): RealmConfiguration {
        return RealmConfiguration.Builder()
                .name(dbName)
                .deleteRealmIfMigrationNeeded()
                .build()
    }

    /* Check for Realm file */
    fun isRealmFileExists(dbName: String): Boolean {
        val realmConfiguration = RealmConfiguration.Builder()
                .name(dbName)
                .deleteRealmIfMigrationNeeded()
                .build()

        val realmFile = File(realmConfiguration.path)
        return realmFile.exists()
    }
}