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

package com.appchamp.wordchunks.models.realm

import io.realm.RealmModel
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.Required


@RealmClass
open class Pack : RealmModel {

    @PrimaryKey
    @Required
    var id: String = ""
    @Required
    var title: String = ""
    @Required
    var color: String = ""
    var state: Int = 0  // LOCKED = 0, IN_PROGRESS = 1, FINISHED = 2
    //var levels: RealmList<Level> = RealmList()
}

// States of the pack
enum class PackState(val value: Int) {
    LOCKED(0),
    IN_PROGRESS(1),
    FINISHED(2)
}