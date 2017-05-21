package com.appchamp.wordchunks.data;

import com.appchamp.wordchunks.models.realm.Level;
import com.appchamp.wordchunks.models.realm.Word;
import com.appchamp.wordchunks.util.RealmUtils;
import com.appchamp.wordchunks.util.StringUtils;

import io.realm.Realm;
import io.realm.RealmList;

import static com.appchamp.wordchunks.util.Constants.CHUNKS_SEPARATOR;
import static com.appchamp.wordchunks.util.Constants.REALM_FIELD_ID;
import static com.appchamp.wordchunks.util.Constants.REALM_FIELD_STATE;
import static com.appchamp.wordchunks.util.Constants.WORD_STATE_NOT_SOLVED;

/**
 * Words
 */
public final class WordsRealmHelper {

    private WordsRealmHelper() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Returns the list of Realm Word objects created from json string.
     *
     * @param realm     instance of Realm object executed in the background transaction.
     * @param wordsSplit an array of string of words to be split.
     * @return the list of Realm Word objects.
     */
    public static RealmList<Word> createWords(Realm realm, String[] wordsSplit) {
        RealmList<Word> words = new RealmList<>();
        int wordPos = 0;
        for (String w : wordsSplit) {
            Word word = realm.createObject(Word.class, RealmUtils.getUUID());
            String replaced = StringUtils.getReplaceAll(w, CHUNKS_SEPARATOR, "");
            // After replaced "AB,CD" becomes "ABCD"
            word.setWord(replaced);
            word.setPosition(wordPos++);
            words.add(word);
        }
        return words;
    }

    public static Word findWordById(Realm realm, String wordId) {
        return realm
                .where(Word.class)
                .equalTo(REALM_FIELD_ID, wordId)
                .findFirst();
    }

    public static long countNotSolvedWords(Realm realm, String levelId) {
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
