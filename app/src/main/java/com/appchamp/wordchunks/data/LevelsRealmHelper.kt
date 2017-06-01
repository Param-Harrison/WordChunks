package com.appchamp.wordchunks.data

import com.appchamp.wordchunks.models.pojo.LevelJson
import com.appchamp.wordchunks.models.realm.Level
import com.appchamp.wordchunks.models.realm.Pack
import com.appchamp.wordchunks.util.Constants.*
import io.realm.Realm
import io.realm.RealmList
import java.util.*

/**
 * Levels
 */
object LevelsRealmHelper {

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

            val wordsSplit = wordsJson.split(WORDS_SEPARATOR)
            // After split "AB,CD EF,GH" becomes ["AB,CD", "EF,GH"]

            val wordsRealm = WordsRealmHelper.createWords(realm, wordsSplit)
            level.words = wordsRealm

            val chunksList = ChunksRealmHelper.createChunks(
                    realm, wordsSplit, level.id!!, wordsRealm)
            level.chunks = chunksList

            levels.add(level)
            pack.levels = levels
        }
    }

    fun findLevelsByPackId(realm: Realm, packId: String): RealmList<Level>? = realm
            .where(Pack::class.java)
            .equalTo(REALM_FIELD_ID, packId)
            .findFirst()
            .levels

    fun findFirstLevelByState(realm: Realm, state: Int): Level = realm
            .where(Level::class.java)
            .equalTo(REALM_FIELD_STATE, state)
            .findFirst()

    fun findLastLevelByState(realm: Realm, state: Int): Level = realm
            .where(Level::class.java)
            .equalTo(REALM_FIELD_STATE, state)
            .findAll()
            .last()

    fun findLevelById(realm: Realm, levelId: String): Level = realm
            .where(Level::class.java)
            .equalTo(REALM_FIELD_ID, levelId)
            .findFirst()

    fun countLevelsByPackIdAndState(realm: Realm, packId: String?, state: Int): Long = realm
            .where(Level::class.java)
            .equalTo(REALM_FIELD_PACK_ID, packId)
            .equalTo(REALM_FIELD_STATE, state)
            .count()

    fun countLevelsByPackId(realm: Realm, packId: String?): Long = realm
            .where(Level::class.java)
            .equalTo(REALM_FIELD_PACK_ID, packId)
            .count()

    fun countLevelsByState(realm: Realm, state: Int): Long = realm
            .where(Level::class.java)
            .equalTo(REALM_FIELD_STATE, state)
            .count()

    fun resetLevelById(realm: Realm, levelId: String) {
        val level = findLevelById(realm, levelId)
        for (word in level.words!!) {
            word.state = WORD_STATE_NOT_SOLVED
        }
        for (chunk in level.chunks!!) {
            chunk.state = CHUNK_STATE_NORMAL.toLong()
        }
    }
}
