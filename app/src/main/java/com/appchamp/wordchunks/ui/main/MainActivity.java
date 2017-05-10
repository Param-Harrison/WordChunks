package com.appchamp.wordchunks.ui.main;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.models.pojo.LevelPojo;
import com.appchamp.wordchunks.models.pojo.PackPojo;
import com.appchamp.wordchunks.models.realm.Chunk;
import com.appchamp.wordchunks.models.realm.Level;
import com.appchamp.wordchunks.models.realm.Pack;
import com.appchamp.wordchunks.models.realm.Word;
import com.appchamp.wordchunks.util.ActivityUtils;
import com.appchamp.wordchunks.util.JsonUtils;
import com.appchamp.wordchunks.util.RealmUtils;
import com.orhanobut.logger.Logger;

import java.lang.ref.WeakReference;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;

import static com.appchamp.wordchunks.util.Constants.CHUNKS_SEPARATOR;
import static com.appchamp.wordchunks.util.Constants.LEVEL_STATE_CURRENT;
import static com.appchamp.wordchunks.util.Constants.LEVEL_STATE_LOCKED;
import static com.appchamp.wordchunks.util.Constants.PACK_STATE_CURRENT;
import static com.appchamp.wordchunks.util.Constants.PREFS_REALM_CREATE_OBJECTS;
import static com.appchamp.wordchunks.util.Constants.REALM_FIELD_STATE;
import static com.appchamp.wordchunks.util.Constants.WORDS_SEPARATOR;
import static com.appchamp.wordchunks.util.Constants.WORD_CHUNKS_PREFERENCES;
import static com.appchamp.wordchunks.util.RealmUtils.getUUID;


public class MainActivity extends AppCompatActivity {

    private ImportAsyncTask importAsyncTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sp = getSharedPreferences(WORD_CHUNKS_PREFERENCES, Context.MODE_PRIVATE);
        boolean isRealmExists = sp.getBoolean(PREFS_REALM_CREATE_OBJECTS, true);
        if (isRealmExists) {
            // update an existing realm objects here
            // updateRealmDb();
            showMainFragment();
        } else {
            // create realm objects for the first time
            startImportAsyncTask();
        }
    }

    private void startImportAsyncTask() {
        // Delete realm db before creating new objects.
        Realm.deleteRealm(new RealmConfiguration.Builder().build());

        if (importAsyncTask != null) {
            importAsyncTask.cancel(true);
        }
        // Show MainFragment after AsyncTask completes
        importAsyncTask = new ImportAsyncTask(
                MainActivity.this, this::showMainFragment);
        importAsyncTask.execute();
    }

    private void showMainFragment() {
        MainFragment mainFragment = (MainFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);
        if (mainFragment == null) {
            // Create the fragment
            mainFragment = MainFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), mainFragment, R.id.contentFrame);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (importAsyncTask != null) {
            importAsyncTask.cancel(false);
        }
    }

    // AsyncTask that imports Realm game data from json file, and initializes the game state
    // in the end.
    //
    // Note:
    // doInBackground() runs in its own background thread while all other methods are executed on the
    // UI thread. This means that it is not possible to reuse RealmObjects or RealmResults created
    // in doInBackground() in the other methods. Nor is it possible to use RealmObjects as Progress
    // or Result objects.
    private static class ImportAsyncTask extends AsyncTask<Void, Void, Void> {

        private final WeakReference<Context> context;
        private OnImportFinished callback;

        ImportAsyncTask(Context context, OnImportFinished callback) {
            this.context = new WeakReference<>(context);
            this.callback = callback;
        }

        @Override
        protected void onPreExecute() {
            final Context c = context.get();
            if (c == null) {
                cancel(false);
                return;
            }
            Logger.d("Starting import into realm");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Context c = context.get();
            if (c != null) {
                List<PackPojo> packs = JsonUtils.getPacksListFromJson(c);

                Realm realm = Realm.getDefaultInstance();

                realm.executeTransaction(bgRealm -> {
                    // Create "packs" <- "levels" <- "words" <- "chunks" realm objects.
                    createPacks(bgRealm, packs);

                    // Initialize game state for the first time in the beginning.
                    initializeGameState(bgRealm);
                });
                realm.close();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            Logger.d("Import was finished");
            if (callback != null) {
                callback.onSuccess();
            }
        }

        /**
         * Creates the list of Realm Pack objects using the PackPojo objects.
         *
         * @param realm        instance of Realm object executed in the background transaction.
         * @param packPojoList the list of PackPojo objects.
         */
        private void createPacks(Realm realm, List<PackPojo> packPojoList) {
            RealmList<Pack> packs = new RealmList<>();
            for (int i = 0; i < packPojoList.size(); i++) {
                Pack pack = realm.createObject(Pack.class, getUUID());
                PackPojo packPojo = packPojoList.get(i);
                pack.setTitle(packPojo.getTitle());
                createLevels(realm, pack, packPojo.getLevels());
                packs.add(pack);
            }
        }

        /**
         * Creates the list of Realm Level objects using the LevelPojo objects.
         *
         * @param realm         instance of Realm object executed in the background transaction.
         * @param pack          the pack object that will be used to create levels into it.
         * @param levelPojoList the list of LevelPojo objects.
         */
        private void createLevels(Realm realm, Pack pack, List<LevelPojo> levelPojoList) {
            RealmList<Level> levels = new RealmList<>();
            for (LevelPojo lvl : levelPojoList) {
                Level level = realm.createObject(Level.class, getUUID());
                level.setClue(lvl.getClue());
                String wordsJson = lvl.getWords();

                RealmList<Word> wordsRealm = refactorIntoWords(realm, wordsJson);
                level.setWords(wordsRealm);

                RealmList<Chunk> chunksList = refactorIntoChunks(realm, wordsJson, level.getId(), wordsRealm);
                level.setChunks(chunksList);

                levels.add(level);
                pack.setLevels(levels);
            }
        }

        /**
         * Returns the list of Realm Word objects created from json string.
         *
         * @param realm     instance of Realm object executed in the background transaction.
         * @param wordsJson a string of words to be split.
         * @return the list of Realm Word objects.
         */
        private RealmList<Word> refactorIntoWords(Realm realm, String wordsJson) {
            String[] wordsSplit = wordsJson.split(WORDS_SEPARATOR);
            // After split "AB,CD EF,GH" becomes ["AB,CD", "EF,GH"]
            RealmList<Word> words = new RealmList<>();
            int wordPos = 0;
            for (String w : wordsSplit) {
                Word word = realm.createObject(Word.class, getUUID());
                String wordReplaced = w.replaceAll(CHUNKS_SEPARATOR, "");
                // After replaceAll "AB,CD" becomes "ABCD"
                word.setWord(wordReplaced);
                word.setPosition(wordPos++);
                words.add(word);
            }
            return words;
        }

        /**
         * Returns the list of Realm Chunk objects created from the array of split chunks.
         */
        private RealmList<Chunk> refactorIntoChunks(Realm realm, String wordsJson, String levelId,
                                                    RealmList<Word> wordsRealm) {
            String[] wordsSplit = wordsJson.split(WORDS_SEPARATOR);
            // After split "AB,CD EF,GH" becomes ["AB,CD", "EF,GH"]
            RealmList<Chunk> chunks = new RealmList<>();

            for (int i = 0; i < wordsSplit.length; i++) {
                String[] wordSplitIntoChunks = wordsSplit[i].split(CHUNKS_SEPARATOR);
                for (String chunkStr : wordSplitIntoChunks) {
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

        /**
         * Initializes the game state in the beginning of the game.
         *
         * @param realm instance of Realm object executed in the background transaction.
         */
        private void initializeGameState(Realm realm) {
            Pack pack = realm.where(Pack.class).findFirst();
            pack.setState(PACK_STATE_CURRENT);
            Level level = realm.where(Level.class).findFirst();
            level.setState(LEVEL_STATE_CURRENT);
            realm.where(Level.class)
                    .equalTo(REALM_FIELD_STATE, LEVEL_STATE_LOCKED)
                    .findFirst()
                    .setState(LEVEL_STATE_CURRENT);
        }
    }
}