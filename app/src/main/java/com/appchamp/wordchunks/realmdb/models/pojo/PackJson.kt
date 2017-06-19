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

package com.appchamp.wordchunks.realmdb.models.pojo

import android.content.Context
import android.content.res.AssetManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.nio.charset.Charset


data class PackJson(
        val title: String,
        val color: String,
        val levels: List<LevelJson>
)

/**
 * Reads this asset file content as a list of PackJson objects.
 *
 * @param context the Context used to access asset folder.
 * @param filename the name of the asset to open.
 */
fun packsFromJSONFile(context: Context, filename: String): List<PackJson> = Gson()
        .fromJson<List<PackJson>>(
                context.assets.fileAsString(filename),
                object : TypeToken<List<PackJson>>() {}.type)

/**
 * Reads this asset file completely into a byte array.
 *
 * @param filename the name of the asset to open.
 * @return the string with corresponding file content.
 */
fun AssetManager.fileAsString(filename: String) = open(filename)
        .use { it.readBytes().toString(Charset.defaultCharset()) }