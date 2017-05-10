package com.appchamp.wordchunks.data;

import com.appchamp.wordchunks.models.realm.Chunk;
import com.appchamp.wordchunks.models.realm.Level;
import com.appchamp.wordchunks.models.realm.Pack;
import com.appchamp.wordchunks.models.realm.Word;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;

import static com.appchamp.wordchunks.util.Constants.CHUNK_STATE_NORMAL;
import static com.appchamp.wordchunks.util.Constants.REALM_FIELD_ID;
import static com.appchamp.wordchunks.util.Constants.REALM_FIELD_LEVEL_ID;
import static com.appchamp.wordchunks.util.Constants.REALM_FIELD_STATE;
import static com.appchamp.wordchunks.util.Constants.WORD_STATE_NOT_SOLVED;


public class DatabaseHelperRealm {


    public DatabaseHelperRealm() {}

    /**
     * Packs
     */
    public List<Pack> findAllPacks(Realm realm) {
        return realm
                .where(Pack.class)
                .findAll();
    }

    public String findPackIdByState(Realm realm, int state) {
        return realm
                .where(Pack.class)
                .equalTo(REALM_FIELD_STATE, state)
                .findFirst()
                .getId();
    }

    /**
     * Levels
     */
    public List<Level> findLevelsByPackId(Realm realm, String packId) {
        return realm
                .where(Pack.class)
                .equalTo(REALM_FIELD_ID, packId)
                .findFirst()
                .getLevels();
    }

    public String findLastLevelIdByState(Realm realm, int state) {
        return realm
                .where(Level.class)
                .equalTo(REALM_FIELD_STATE, state)
                .findAll()
                .first()
               // .last()
                .getId();
    }

    public Level findLevelById(Realm realm, String levelId) {
        return realm
                .where(Level.class)
                .equalTo(REALM_FIELD_ID, levelId)
                .findFirst();
    }

    /**
     * Chunks
     */
    public List<Chunk> findSelectedChunksByLevelIdSorted(Realm realm, String levelId) {
        return realm
                .where(Chunk.class)
                .equalTo(REALM_FIELD_LEVEL_ID, levelId)
                .greaterThan(REALM_FIELD_STATE, CHUNK_STATE_NORMAL)
                .findAllSorted(REALM_FIELD_STATE);
    }

    public RealmResults<Chunk> findSelectedChunksByLevelId(Realm realm, String levelId) {
        return realm
                .where(Chunk.class)
                .equalTo(REALM_FIELD_LEVEL_ID, levelId)
                .greaterThan(REALM_FIELD_STATE, CHUNK_STATE_NORMAL)
                .findAll();
    }

    /**
     * Words
     */
    public Word findWordById(Realm realm, String wordId) {
        return realm
                .where(Word.class)
                .equalTo(REALM_FIELD_ID, wordId)
                .findFirst();
    }

    public long countNotSolvedWords(Realm realm, String levelId) {
        return realm
                .where(Level.class)
                .equalTo(REALM_FIELD_ID, levelId)
                .findFirst()
                .getWords()
                .where()
                .equalTo(REALM_FIELD_STATE, WORD_STATE_NOT_SOLVED)
                .count();
    }
}
