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

    const val FILE_NAME_DATA = "data"
    const val FILE_NAME_DATA_RU = "data-ru"
    const val JSON = ".json"
    const val WORDS_SEPARATOR = " "
    const val CHUNKS_SEPARATOR = ","

    const val WORD_CHUNKS_PREFS = "WORD_CHUNKS_PREFS"
    const val PREFS_HOW_TO_PLAY = "PREFS_HOW_TO_PLAY"
    const val LANG_RU = "ru"

    const val EXTRA_PACK_ID = "EXTRA_PACK_ID"
    const val EXTRA_LEVEL_ID = "EXTRA_LEVEL_ID"

    const val REALM_FIELD_ID = "id"
    const val REALM_FIELD_STATE = "state"
    const val REALM_FIELD_WORD_ID = "wordId"
    const val REALM_FIELD_LEVEL_ID = "levelId"
    const val REALM_FIELD_PACK_ID = "packId"

    const val WORDS_GRID_NUM = 2
    const val CHUNKS_GRID_NUM = 4
}
