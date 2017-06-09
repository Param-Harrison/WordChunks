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