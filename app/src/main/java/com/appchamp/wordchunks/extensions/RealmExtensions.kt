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

package com.appchamp.wordchunks.extensions

import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmQuery


/**
 * Query to the database with RealmQuery instance as argument and returns all items founded
 */
fun <T : RealmObject> T.queryAll(): List<T> {
    Realm.getDefaultInstance().use {
        val result = it.forEntity(this).findAll()
        return it.copyFromRealm(result)
    }
}

/**
 * Query to the database with RealmQuery instance as argument. Return first result, or null.
 */
fun <T : RealmObject> T.queryFirst(query: (RealmQuery<T>) -> Unit): T? {
    Realm.getDefaultInstance().use {
        val item : T? = it.forEntity(this).withQuery(query).findFirst()
        return if(item != null && item.isValid) it.copyFromRealm(item) else null
    }
}

/**
 * Query to the database with RealmQuery instance as argument. Return last result, or null.
 */
fun <T : RealmObject> T.queryLast(query: (RealmQuery<T>) -> Unit): T? {
    Realm.getDefaultInstance().use {
        val result = it.forEntity(this).withQuery(query).findAll()
        return if(result != null && result.isNotEmpty()) it.copyFromRealm(result.last()) else null
    }
}

/**
 * Utilities
 */
private fun <T : RealmObject> Realm.forEntity(instance : T) : RealmQuery<T> {
    return RealmQuery.createQuery(this, instance.javaClass)
}

private fun <T> T.withQuery(block: (T) -> Unit): T { block(this); return this }