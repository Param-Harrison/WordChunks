package com.appchamp.wordchunks.data;

import com.appchamp.wordchunks.models.realm.Chunk;
import com.appchamp.wordchunks.models.realm.Word;
import com.appchamp.wordchunks.util.RealmUtils;
import com.appchamp.wordchunks.util.StringUtils;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

import static com.appchamp.wordchunks.util.Constants.CHUNKS_SEPARATOR;
import static com.appchamp.wordchunks.util.Constants.CHUNK_STATE_NORMAL;
import static com.appchamp.wordchunks.util.Constants.REALM_FIELD_LEVEL_ID;
import static com.appchamp.wordchunks.util.Constants.REALM_FIELD_STATE;

/**
 * Chunks
 */
public final class ChunksRealmHelper {

    private ChunksRealmHelper() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * Returns the list of Realm Chunk objects created from the array of split chunks.
     */
    public static RealmList<Chunk> createChunks(Realm realm, String[] wordsSplit,
                                                String levelId, RealmList<Word> wordsRealm) {
        RealmList<Chunk> chunks = new RealmList<>();
        for (int i = 0; i < wordsSplit.length; i++) {
            String[] splitChunks = StringUtils.getSplits(wordsSplit[i], CHUNKS_SEPARATOR);
            for (String chunkStr : splitChunks) {
                Chunk chunk = realm.createObject(Chunk.class);
                chunk.setChunk(chunkStr);
                chunk.setLevelId(levelId);
                chunk.setWordId(wordsRealm.get(i).getId());
                chunks.add(chunk);
            }
        }
        int chunksSize = chunks.size();
        int[] shuffledArray = RealmUtils.shuffleArray(chunksSize);
        for (int i = 0; i < chunksSize / 2; i++) {
            chunks.get(shuffledArray[i]).setPosition(shuffledArray[chunksSize - i - 1]);
            chunks.get(shuffledArray[chunksSize - i - 1]).setPosition(shuffledArray[i]);
        }
        return chunks;
    }

    public static List<Chunk> findSelectedChunksByLevelIdSorted(Realm realm, String levelId) {
        return realm
                .where(Chunk.class)
                .equalTo(REALM_FIELD_LEVEL_ID, levelId)
                .greaterThan(REALM_FIELD_STATE, CHUNK_STATE_NORMAL)
                .findAllSorted(REALM_FIELD_STATE);
    }

    public static RealmResults<Chunk> findSelectedChunksByLevelId(Realm realm, String levelId) {
        return realm
                .where(Chunk.class)
                .equalTo(REALM_FIELD_LEVEL_ID, levelId)
                .greaterThan(REALM_FIELD_STATE, CHUNK_STATE_NORMAL)
                .findAll();
    }

}
