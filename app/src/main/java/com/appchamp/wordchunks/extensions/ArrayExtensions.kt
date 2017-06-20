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