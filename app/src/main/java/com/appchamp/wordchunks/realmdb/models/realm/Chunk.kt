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

package com.appchamp.wordchunks.realmdb.models.realm

import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required


@RealmClass
open class Chunk : RealmModel {

    @PrimaryKey
    @Required
    var id: String = ""
    @Required
    var chunk: String = ""
    @Required
    var levelId: String = ""
    // var wordId: String = ""
    var state: Long = 0  // should be Long because of the currentTimeMillis
    var position: Int = 0

}

fun List<Chunk>.chunksToString(): String = map { it.chunk }.joinToString(separator = "")

// States of chunk
enum class ChunkState(val value: Long) {
    NORMAL(0),
    GONE(-1)
}