package com.appchamp.wordchunks.extensions

import java.util.*


/**
 * Returns a randomly shuffled array of ints, implements Fisher-Yates shuffle.
 *
 * @return a randomly shuffled array of ints.
 */
fun IntArray.shuffleIntArray(): IntArray {
    val rand = Random()
    for (i in 0..size - 1) {
        val randPos = rand.nextInt(this.size)
        // Simple swap;
        val a = this[randPos]
        this[randPos] = this[i]
        this[i] = a
    }
    return this
}