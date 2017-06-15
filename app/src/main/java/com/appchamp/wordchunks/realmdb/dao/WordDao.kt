package com.appchamp.wordchunks.realmdb.dao

import com.appchamp.wordchunks.realmdb.models.realm.Word
import com.appchamp.wordchunks.realmdb.utils.LiveRealmObject
import com.appchamp.wordchunks.realmdb.utils.LiveRealmResults
import com.appchamp.wordchunks.realmdb.utils.asLiveData
import com.appchamp.wordchunks.util.Constants
import io.realm.Realm


class WordDao(private val realm: Realm) {


    /**
     * Custom set methods.
     */
    fun setWordState(word: Word, state: Int) {
        realm.executeTransaction {
            word.state = state
        }
    }

    /**
     * Custom finder methods.
     */
    fun findWordsByLevelId(levelId: String): LiveRealmResults<Word> {
        return realm
                .where(Word::class.java)
                .equalTo(Constants.REALM_FIELD_LEVEL_ID, levelId)
                .findAllAsync()
                .asLiveData()
    }

    fun findWordById(id: String): LiveRealmObject<Word> = realm
            .where(Word::class.java)
            .equalTo(Constants.REALM_FIELD_ID, id)
            .findFirst()
            .asLiveData()
}