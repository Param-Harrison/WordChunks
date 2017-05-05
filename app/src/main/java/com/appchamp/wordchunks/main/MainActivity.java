package com.appchamp.wordchunks.main;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.game.GameActivity;
import com.appchamp.wordchunks.models.pojo.LevelPojo;
import com.appchamp.wordchunks.models.pojo.PackPojo;
import com.appchamp.wordchunks.models.realm.Chunk;
import com.appchamp.wordchunks.models.realm.Level;
import com.appchamp.wordchunks.models.realm.Pack;
import com.appchamp.wordchunks.models.realm.Word;
import com.appchamp.wordchunks.packs.PacksActivity;
import com.appchamp.wordchunks.util.ActivityUtils;
import com.appchamp.wordchunks.util.AnimUtils;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;

import static com.appchamp.wordchunks.util.Constants.ALL_CHUNKS_SEPARATOR;
import static com.appchamp.wordchunks.util.Constants.CHARSET_NAME;
import static com.appchamp.wordchunks.util.Constants.CHUNKS_SEPARATOR;
import static com.appchamp.wordchunks.util.Constants.EXTRA_LEVEL_ID;
import static com.appchamp.wordchunks.util.Constants.JSON_FILE_NAME;
import static com.appchamp.wordchunks.util.Constants.REALM_FIELD_STATE;
import static com.appchamp.wordchunks.util.Constants.WORDS_SEPARATOR;
import static com.appchamp.wordchunks.util.RealmUtils.getUUID;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();

    private SlidingMenu menu;

    private ImportAsyncTask importAsyncTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initLeftMenu();

        startImportAsyncTask();
    }

    private void startImportAsyncTask() {
        // Delete realm db before creating new objects.
        Realm.deleteRealm(new RealmConfiguration.Builder().build());

        if (importAsyncTask != null) {
            importAsyncTask.cancel(true);
        }

        importAsyncTask = new ImportAsyncTask(MainActivity.this, new OnImportFinished() {
            @Override
            public void onSuccess() {

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
            public void onFailure(Exception e) {
                Log.e(TAG, e.getMessage());
            }
        });
        importAsyncTask.execute();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (importAsyncTask != null) {
            importAsyncTask.cancel(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (menu.isMenuShowing()) {
            menu.showContent(true);
            return;
        }
        super.onBackPressed();
    }

    public void onPlayClick(View v) {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);

        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(bgRealm -> {
            Level level = bgRealm.where(Level.class)
                    .equalTo(REALM_FIELD_STATE, 1)
                    .findAll().last();
            String levelId = level.getId();
            intent.putExtra(EXTRA_LEVEL_ID, levelId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        });
        realm.close();
        //startPacksActivity();
    }

    private void startPacksActivity() {
        Intent intent = new Intent(MainActivity.this, PacksActivity.class);
        startActivity(intent);
    }

    public void onDailyClick(View v) {
    }

    public void onShopClick(View view) {
    }

    public void onSettingsClick(View v) {
        AnimUtils.startAnimationFadeIn(MainActivity.this, v);
        menu.toggle();
    }

    private void initLeftMenu() {
        // Configure the SlidingMenu
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.settings_menu);
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
        private Exception exception;

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
            Log.d(TAG, "STARTING IMPORT");
        }

        @Override
        protected Void doInBackground(Void... params) {
            Context c = context.get();
            if (c != null) {
                List<PackPojo> packs = getPacksListFromJson(c);

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
            Log.d(TAG, "FINISHING");
            final Context c = context.get();
            if (c == null) {
                return;
            }
            if (callback != null) {
                if (exception == null) {
                    callback.onSuccess();
                } else {
                    callback.onFailure(exception);
                }
            }
        }

        /**
         * Returns a list of PackPojo objects deserialized using the Gson library from the json
         * string.
         *
         * @param context used to access assets where json file is located.
         * @return the list of PackPojo objects.
         */
        private List<PackPojo> getPacksListFromJson(Context context) {
            Type listType = new TypeToken<ArrayList<PackPojo>>() {
            }.getType();
            return new GsonBuilder()
                    .create()
                    .fromJson(loadJSONFromAsset(context), listType);
        }

        /**
         * Returns a new String when reading the json file from the assets folder is done.
         *
         * @param context used to access assets where json file is located.
         * @return the json string.
         */
        private String loadJSONFromAsset(Context context) {
            String json;
            try {
                InputStream is = context.getAssets().open(JSON_FILE_NAME);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, CHARSET_NAME);
            } catch (IOException ex) {
                ex.printStackTrace();
                exception = ex;
                return null;
            }
            return json;
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

                RealmList<Chunk> chunksList = refactorIntoChunks(realm, wordsJson);
                level.setChunks(chunksList);

                levels.add(level);
                pack.setLevels(levels);
            }
        }

        /**
         * Returns the list of Realm Chunk objects created from the array of split chunks.
         *
         * @param realm       instance of Realm object executed in the background transaction.
         * @param chunksSplit an array of split chunks.
         * @return the list of Realm Chunk objects.
         */
        private RealmList<Chunk> createChunks(Realm realm, String[] chunksSplit) {
            RealmList<Chunk> chunks = new RealmList<>();
            for (int i = 0; i < chunksSplit.length; i++) {
                Chunk chunk = realm.createObject(Chunk.class);
                chunk.setChunk(chunksSplit[i]);
                chunk.setPosition(i);
                chunks.add(chunk);
            }
            return chunks;
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
            for (String w : wordsSplit) {
                Word word = realm.createObject(Word.class);
                String wordReplaced = w.replaceAll(CHUNKS_SEPARATOR, "");
                // After replaceAll "AB,CD" becomes "ABCD"

                word.setWord(wordReplaced);

                String[] chunksSplit = w.split(CHUNKS_SEPARATOR);
                // After split "AB,CD" becomes ["AB", "CD"]
                RealmList<Chunk> chunks = createChunks(realm, chunksSplit);
                word.setChunks(chunks);
                words.add(word);
            }
            return words;
        }

        /**
         * Returns the list of Realm Chunk objects split from string.
         *
         * @param realm        instance of Realm object executed in the background transaction.
         * @param chunksString a string of chunks to be split.
         * @return the list of Realm Chunk objects.
         */
        private RealmList<Chunk> refactorIntoChunks(Realm realm, String chunksString) {
            String[] chunksSplit = chunksString.split(ALL_CHUNKS_SEPARATOR);
            // After split "AB,CD" becomes ["AB", "CD"]
            return createChunks(realm, chunksSplit);
        }

        /**
         * Initializes the game state in the beginning of the game.
         *
         * @param realm instance of Realm object executed in the background transaction.
         */
        private void initializeGameState(Realm realm) {
            Pack pack = realm.where(Pack.class).findFirst();
            pack.setState(1);
            Level level = realm.where(Level.class).findFirst();
            level.setState(1);
            realm.where(Level.class)
                    .equalTo(REALM_FIELD_STATE, 0)
                    .findFirst()
                    .setState(1);
        }
    }
}