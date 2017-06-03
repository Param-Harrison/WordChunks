package com.appchamp.wordchunks.models.pojo

import android.content.Context
import com.appchamp.wordchunks.util.readAsset
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


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
fun packsFromJSONFile(context: Context, filename: String): List<PackJson> =
        Gson().fromJson<List<PackJson>>(
                context.readAsset(filename),
                object : TypeToken<List<PackJson>>() {}.type)