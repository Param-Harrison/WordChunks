package com.appchamp.wordchunks.util

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
 * Query to the database with RealmQuery instance as argument
 */
fun <T : RealmObject> T.queryAll(query: (RealmQuery<T>) -> Unit): List<T> {

    Realm.getDefaultInstance().use {
        val result = it.forEntity(this).withQuery(query).findAll()
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