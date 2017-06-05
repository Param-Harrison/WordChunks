package com.appchamp.wordchunks.data

import com.appchamp.wordchunks.models.pojo.LevelJson
import com.appchamp.wordchunks.models.realm.Level
import com.appchamp.wordchunks.models.realm.Pack
import com.appchamp.wordchunks.util.Constants.REALM_FIELD_PACK_ID
import com.appchamp.wordchunks.util.Constants.REALM_FIELD_STATE
import com.appchamp.wordchunks.util.Constants.WORDS_SEPARATOR
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
            level.color = pack.color

            val wordsSplit = wordsJson.split(WORDS_SEPARATOR)
            // After split "AB,CD EF,GH" becomes ["AB,CD", "EF,GH"]

            val wordsRealm = WordsRealmHelper.createWords(realm, wordsSplit)
            level.words = wordsRealm

            val chunksList = ChunksRealmHelper.createChunks(
                    realm, wordsSplit, level.id)
            level.chunks = chunksList

            levels.add(level)
            pack.levels = levels
        }
    }

    fun countLevelsByPackIdAndState(realm: Realm, packId: String?, state: Int): Long = realm
            .where(Level::class.java)
            .equalTo(REALM_FIELD_PACK_ID, packId)
            .equalTo(REALM_FIELD_STATE, state)
            .count()

    fun countLevelsByPackId(realm: Realm, packId: String?): Long = realm
            .where(Level::class.java)
            .equalTo(REALM_FIELD_PACK_ID, packId)
            .count()
}
