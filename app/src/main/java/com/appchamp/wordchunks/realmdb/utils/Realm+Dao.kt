package com.appchamp.wordchunks.realmdb.utils

import com.appchamp.wordchunks.realmdb.dao.LevelDao
import com.appchamp.wordchunks.realmdb.dao.PackDao
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmResults


fun Realm.packModel(): PackDao = PackDao(this)
fun Realm.levelModel(): LevelDao = LevelDao(this)
//fun Realm.loanModel(): LoanDao = LoanDao(this)

fun <T: RealmModel> RealmResults<T>.asLiveData() = LiveRealmData<T>(this)
