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

import com.appchamp.wordchunks.realmdb.dao.*
import io.realm.Realm
import io.realm.RealmModel
import io.realm.RealmResults


fun Realm.packModel(): PackDao = PackDao(this)
fun Realm.levelModel(): LevelDao = LevelDao(this)
fun Realm.chunkModel(): ChunkDao = ChunkDao(this)
fun Realm.wordModel(): WordDao = WordDao(this)
fun Realm.userModel(): UserDao = UserDao(this)

fun <T: RealmModel> RealmResults<T>.asLiveData() = LiveRealmResults(this)
fun <T: RealmModel> T.asLiveData() = LiveRealmObject(this)
