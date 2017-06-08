package com.appchamp.wordchunks.data

import com.appchamp.wordchunks.models.realm.Word
import com.appchamp.wordchunks.util.Constants.CHUNKS_SEPARATOR
import io.realm.Realm
import io.realm.RealmList
import java.util.*


/**
 * Words
 */
object WordsDao {

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
}
