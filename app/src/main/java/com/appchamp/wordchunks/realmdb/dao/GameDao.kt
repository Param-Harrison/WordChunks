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

package com.appchamp.wordchunks.realmdb.dao

import com.appchamp.wordchunks.extensions.isEven
import com.appchamp.wordchunks.extensions.shuffleIntArray
import com.appchamp.wordchunks.realmdb.models.pojo.LevelJson
import com.appchamp.wordchunks.realmdb.models.pojo.PackJson
import com.appchamp.wordchunks.realmdb.models.realm.*
import com.appchamp.wordchunks.realmdb.utils.levelModel
import com.appchamp.wordchunks.realmdb.utils.packModel
import com.appchamp.wordchunks.util.Constants
import io.realm.Realm
import io.realm.RealmList
import java.util.*

/**
 * Initializes the first state of the game. Loads all the needed data to play.
 */
class GameDao(private val realm: Realm) {

    /**
     * Creates the list of Realm Pack objects using the PackJson list.
     *
     * @param packJsonList the list of PackJson objects.
     */
    fun createPacks(packJsonList: List<PackJson>) {
        realm.executeTransaction {
            val packs = RealmList<Pack>()
            for (i in packJsonList.indices) {
                val pack = it.createObject(Pack::class.java, UUID.randomUUID().toString())
                val packPojo = packJsonList[i]
                pack.title = packPojo.title
                pack.color = packPojo.color

                it.createLevels(pack, packPojo.levels)

                packs.add(pack)
            }
        }
    }

    /**
     * Creates the list of Realm Level objects using the LevelJson objects.
     *
     * @param pack          the pack object that will be used to create levels into it.
     * @param levelJsonList the list of LevelJson objects.
     */
    private fun Realm.createLevels(pack: Pack, levelJsonList: List<LevelJson>) {
        val levels = RealmList<Level>()
        for ((clue, fact, wordsJson) in levelJsonList) {
            val level = this.createObject(Level::class.java, UUID.randomUUID().toString())
            level.clue = clue
            level.fact = fact
            level.packId = pack.id
            level.color = pack.color

            val wordsSplit = wordsJson.split(Constants.WORDS_SEPARATOR)
            // After split "AB,CD EF,GH" becomes ["AB,CD", "EF,GH"]

            level.words = createWords(wordsSplit, level.id)

            level.chunks = createChunks(wordsSplit, level.words, level.id)

            levels.add(level)
            pack.levels = levels
        }
    }


    /**
     * Returns the list of Realm Word objects created from json string.
     *
     * @param wordsSplit an array of string of words to be split.
     * @return the list of Realm Word objects.
     */
    private fun Realm.createWords(wordsSplit: List<String>, levelId: String): RealmList<Word> {
        val words = RealmList<Word>()
        for ((wordPos, w) in wordsSplit.withIndex()) {
            val word = this.createObject(Word::class.java, UUID.randomUUID().toString())
            val replaced = w.replace(Constants.CHUNKS_SEPARATOR, "")
            // After replaced "AB,CD" becomes "ABCD"
            word.word = replaced
            word.levelId = levelId
            word.position = wordPos
            words.add(word)
        }
        return words
    }

    /**
     * Returns the list of Realm Chunk objects created from the array of split chunks.
     */
    private fun Realm.createChunks(wordsSplit: List<String>, wordsRealm: RealmList<Word>,
                                   levelId: String): RealmList<Chunk> {
        val chunks = RealmList<Chunk>()
        for (i in wordsSplit.indices) {
            val splitChunks = wordsSplit[i].split(Constants.CHUNKS_SEPARATOR)
            for (chunkStr in splitChunks) {
                val chunk = this.createObject(Chunk::class.java)
                chunk.chunk = chunkStr
                chunk.levelId = levelId
                chunk.wordId = wordsRealm[i].id
                chunks.add(chunk)
            }
        }
        val chunksSize = chunks.size
        val shuffledArray = IntArray(chunksSize, { it }).shuffleIntArray()
        val size = if (chunksSize.isEven()) chunksSize / 2 - 1 else chunksSize / 2
        for (i in 0..size) {
            chunks[shuffledArray[i]].position = shuffledArray[chunksSize - i - 1]
            chunks[shuffledArray[chunksSize - i - 1]].position = shuffledArray[i]
        }
        return chunks
    }

    fun initFirstGameSatate() {
        val pack = realm.packModel().findPackByState(PackState.LOCKED.value)
        pack?.let { realm.packModel().setPackState(it, PackState.IN_PROGRESS.value) }
        val level = realm.levelModel().findLevelByState(LevelState.LOCKED.value)
        level?.let { realm.levelModel().setLevelState(it, LevelState.IN_PROGRESS.value) }
        realm.executeTransaction { it.createObject(Game::class.java) }

    }

    fun findGame(): Game {
        return realm.where(Game::class.java).findFirst()
    }

    fun setShowTutorial(game: Game, value: Boolean) {
        realm.executeTransaction {
            game.showTutorial = value
        }
    }
}