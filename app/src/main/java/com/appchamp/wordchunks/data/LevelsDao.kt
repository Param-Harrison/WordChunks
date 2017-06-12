package com.appchamp.wordchunks.data

import com.appchamp.wordchunks.realmdb.models.pojo.LevelJson
import com.appchamp.wordchunks.realmdb.models.realm.Level
import com.appchamp.wordchunks.realmdb.models.realm.Pack
import com.appchamp.wordchunks.util.Constants.WORDS_SEPARATOR
import io.realm.Realm
import io.realm.RealmList
import java.util.*

/**
 * Levels
 */
object LevelsDao {

    /**
     * Creates the list of Realm Level objects using the LevelJson objects.
     *
     * @param realm         instance of Realm object executed in the background transaction.
     * @param pack          the pack object that will be used to create levels into it.
     * @param levelJsonList the list of LevelJson objects.
     */
    fun createLevels(realm: Realm, pack: Pack, levelJsonList: List<LevelJson>) {
        val levels = RealmList<Level>()
        for ((clue, fact, wordsJson) in levelJsonList) {
            val level = realm.createObject(Level::class.java, UUID.randomUUID().toString())
            level.clue = clue
            level.fact = fact
            level.packId = pack.id
            level.color = pack.color

            val wordsSplit = wordsJson.split(WORDS_SEPARATOR)
            // After split "AB,CD EF,GH" becomes ["AB,CD", "EF,GH"]

            level.words = WordsDao.createWords(realm, wordsSplit, level.id)

            level.chunks = ChunksDao.createChunks(realm, wordsSplit, level.words, level.id)

            levels.add(level)
            pack.levels = levels
        }
    }
}
