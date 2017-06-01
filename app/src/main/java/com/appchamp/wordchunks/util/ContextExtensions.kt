package com.appchamp.wordchunks.util

import android.content.Context


/**
 * Reads this asset file completely as a String.
 *
 * @param filename the name of the asset to open.
 * @return the string with corresponding file content.
 */
fun Context.readAsset(filename: String): String? {
    val inputStream = assets.open(filename)
    val json = inputStream.bufferedReader().use { it.readText() }
    return json
}