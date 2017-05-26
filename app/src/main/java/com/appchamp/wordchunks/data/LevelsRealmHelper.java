package com.appchamp.wordchunks.data;

import com.appchamp.wordchunks.models.pojo.LevelJson;
import com.appchamp.wordchunks.models.realm.Chunk;
import com.appchamp.wordchunks.models.realm.Level;
import com.appchamp.wordchunks.models.realm.Pack;
import com.appchamp.wordchunks.models.realm.Word;
import com.appchamp.wordchunks.util.RealmUtils;
import com.appchamp.wordchunks.util.StringUtils;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

import static com.appchamp.wordchunks.util.Constants.CHUNK_STATE_NORMAL;
import static com.appchamp.wordchunks.util.Constants.LEVEL_STATE_CURRENT;
import static com.appchamp.wordchunks.util.Constants.REALM_FIELD_ID;
import static com.appchamp.wordchunks.util.Constants.REALM_FIELD_PACK_ID;
import static com.appchamp.wordchunks.util.Constants.REALM_FIELD_STATE;
import static com.appchamp.wordchunks.util.Constants.WORDS_SEPARATOR;
import static com.appchamp.wordchunks.util.Constants.WORD_STATE_NOT_SOLVED;

/**
 * Levels
 */
public final class LevelsRealmHelper {

    private LevelsRealmHelper() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Creates the list of Realm Level objects using the LevelJson objects.
     *
     * @param realm         instance of Realm object executed in the background transaction.
     * @param pack          the pack object that will be used to create levels into it.
     * @param levelJsonList the list of LevelJson objects.
     */
    public static void createLevels(Realm realm, Pack pack, List<LevelJson> levelJsonList) {
        RealmList<Level> levels = new RealmList<>();
        for (LevelJson lvl : levelJsonList) {
            Level level = realm.createObject(Level.class, RealmUtils.getUUID());
            level.setClue(lvl.getClue());
            level.setFact(lvl.getFact());
            level.setPackId(pack.getId());
            String wordsJson = lvl.getWords();

            String[] wordsSplit = StringUtils.getSplits(wordsJson, WORDS_SEPARATOR);
            // After split "AB,CD EF,GH" becomes ["AB,CD", "EF,GH"]

            RealmList<Word> wordsRealm = WordsRealmHelper.createWords(realm, wordsSplit);
            level.setWords(wordsRealm);

            RealmList<Chunk> chunksList = ChunksRealmHelper.createChunks(
                    realm, wordsSplit, level.getId(), wordsRealm);
            level.setChunks(chunksList);

            levels.add(level);
            pack.setLevels(levels);
        }
    }

    public static List<Level> findLevelsByPackId(Realm realm, String packId) {
        return realm
                .where(Pack.class)
                .equalTo(REALM_FIELD_ID, packId)
                .findFirst()
                .getLevels();
    }

    public static List<Level> findAllLevelsByState(Realm realm, int state) {
        return realm
                .where(Level.class)
                .equalTo(REALM_FIELD_STATE, state)
                .findAll();
    }

    public static Level findFirstLevelByState(Realm realm, int state) {
        return realm
                .where(Level.class)
                .equalTo(REALM_FIELD_STATE, state)
                .findFirst();
    }

    public static Level findLastLevelByState(Realm realm, int state) {
        return realm
                .where(Level.class)
                .equalTo(REALM_FIELD_STATE, state)
                .findAll()
                .last();
    }

    public static Level findLevelById(Realm realm, String levelId) {
        return realm
                .where(Level.class)
                .equalTo(REALM_FIELD_ID, levelId)
                .findFirst();
    }

    public static void resetLevelById(Realm realm, String levelId) {
        Level level = findLevelById(realm, levelId);
        for (Word word : level.getWords()) {
            word.setState(WORD_STATE_NOT_SOLVED);
        }
        for (Chunk chunk : level.getChunks()) {
            chunk.setState(CHUNK_STATE_NORMAL);
        }
    }

    public static long countLevelsByPackIdAndState(Realm realm, String packId, int state) {
        return realm
                .where(Level.class)
                .equalTo(REALM_FIELD_PACK_ID, packId)
                .equalTo(REALM_FIELD_STATE, state)
                .count();
    }

    public static long countLevelsByPackId(Realm realm, String packId) {
        return realm
                .where(Level.class)
                .equalTo(REALM_FIELD_PACK_ID, packId)
                .count();
    }

    public static long countLevelsByState(Realm realm, int state) {
        return realm
                .where(Level.class)
                .equalTo(REALM_FIELD_STATE, LEVEL_STATE_CURRENT)
                .count();
    }

}
