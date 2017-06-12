package com.appchamp.wordchunks.realmdb.utils

import com.appchamp.wordchunks.realmdb.dao.ChunkDao
import com.appchamp.wordchunks.realmdb.dao.LevelDao
import com.appchamp.wordchunks.realmdb.dao.PackDao
import com.appchamp.wordchunks.realmdb.dao.WordDao
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmResults


fun Realm.packModel(): PackDao = PackDao(this)
fun Realm.levelModel(): LevelDao = LevelDao(this)
fun Realm.chunkModel(): ChunkDao = ChunkDao(this)
fun Realm.wordModel(): WordDao = WordDao(this)

fun <T: RealmModel> RealmResults<T>.asLiveData() = LiveRealmResults<T>(this)
fun <T: RealmModel> T.asLiveData() = LiveRealmObject<T>(this)
