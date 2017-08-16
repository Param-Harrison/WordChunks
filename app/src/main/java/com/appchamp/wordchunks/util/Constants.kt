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

package com.appchamp.wordchunks.util

import java.util.*


object Constants {

    val SUPPORTED_LOCALES: MutableList<Locale> = Arrays.asList(
            Locale("en", "US"),
            Locale("ru", "RU")
    )

    const val PREFS_NAME = "com.appchamp.wordchunks"
    const val PREFS_TUTORIAL = "PREFS_TUTORIAL"
    const val LANG_RU = "ru"
    const val LANG_EN = "en"

    const val EXTRA_PACK_ID = "EXTRA_PACK_ID"
    const val EXTRA_LEVEL_ID = "EXTRA_LEVEL_ID"

    const val REALM_FIELD_ID = "id"
    const val REALM_FIELD_STATE = "state"
    const val REALM_FIELD_WORD_ID = "wordId"
    const val REALM_FIELD_LEVEL_ID = "levelId"
    const val REALM_FIELD_PACK_ID = "packId"

    const val WORDS_GRID_NUM = 1
    const val CHUNKS_GRID_NUM = 4

    const val USER_INITIAL_HINTS = 4
    const val USER_LEVEL_SOLVED_REWARD = 2
    const val USER_DAILY_LEVEL_SOLVED_REWARD = 3

    const val FIREBASE_PACKS_CHILD = "packs"
    const val FIREBASE_LEVELS_CHILD = "levels"
    const val FIREBASE_WORDS_CHILD = "words"
    const val FIREBASE_CHUNKS_CHILD = "chunks"
    const val FIREBASE_DAILY_CHILD = "daily"
}
