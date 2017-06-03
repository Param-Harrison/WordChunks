package com.appchamp.wordchunks.data

import com.appchamp.wordchunks.models.realm.Level
import com.appchamp.wordchunks.models.realm.Word
import com.appchamp.wordchunks.util.Constants.CHUNKS_SEPARATOR
import com.appchamp.wordchunks.util.Constants.REALM_FIELD_ID
import com.appchamp.wordchunks.util.Constants.REALM_FIELD_STATE
import com.appchamp.wordchunks.util.Constants.WORD_STATE_NOT_SOLVED

import io.realm.Realm
import io.realm.RealmList
import java.util.*


/**
 * Words
 */
object WordsRealmHelper {

    /**
     * Returns the list of Realm Word objects created from json string.
     *
     * @param realm     instance of Realm object executed in the background transaction.
     * @param wordsSplit an array of string of words to be split.
     * @return the list of Realm Word objects.
     */
    fun createWords(realm: Realm, wordsSplit: List<String>): RealmList<Word> {
        val words = RealmList<Word>()
        for ((wordPos, w) in wordsSplit.withIndex()) {
            val word = realm.createObject(Word::class.java, UUID.randomUUID().toString())
            val replaced = w.replace(CHUNKS_SEPARATOR, "")
            // After replaced "AB,CD" becomes "ABCD"
            word.word = replaced
            word.position = wordPos
            words.add(word)
        }
        return words
    }

    fun findWordById(realm: Realm, wordId: String?): Word = realm
            .where(Word::class.java)
            .equalTo(REALM_FIELD_ID, wordId)
            .findFirst()

    fun countNotSolvedWords(realm: Realm, levelId: String): Long = realm
            .where(Level::class.java)
            .equalTo(REALM_FIELD_ID, levelId)
            .findFirst()
            .words!!
            .where()
            .equalTo(REALM_FIELD_STATE, WORD_STATE_NOT_SOLVED)
            .count()
}
