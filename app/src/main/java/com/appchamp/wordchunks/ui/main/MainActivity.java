package com.appchamp.wordchunks.ui.main;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.data.GameRealmHelper;
import com.appchamp.wordchunks.data.PacksRealmHelper;
import com.appchamp.wordchunks.models.pojo.PackJson;
import com.appchamp.wordchunks.ui.game.GameActivity;
import com.appchamp.wordchunks.ui.packs.PacksActivity;
import com.appchamp.wordchunks.ui.tutorial.TutorialActivity;
import com.appchamp.wordchunks.util.ActivityUtils;
import com.appchamp.wordchunks.util.JsonUtils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.orhanobut.logger.Logger;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.appchamp.wordchunks.util.Constants.EXTRA_LEVEL_ID;
import static com.appchamp.wordchunks.util.Constants.PREFS_REALM_CREATE_OBJECTS;
import static com.appchamp.wordchunks.util.Constants.WORD_CHUNKS_PREFS;


public class MainActivity extends AppCompatActivity implements OnMainFragmentClickListener {
    private SlidingMenu menu;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_main);

        SharedPreferences sp = getSharedPreferences(WORD_CHUNKS_PREFS, Context.MODE_PRIVATE);
        boolean isRealmFileExists = sp.getBoolean(PREFS_REALM_CREATE_OBJECTS, true);
        if (isRealmFileExists) {
            // update an existing realm objects here
            // updateRealmDb();
            showMainFragment();
        } else {
            // create realm objects for the first time
            startImport();
        }
        initLeftMenu();

        Button btnHowToPlay = (Button) findViewById(R.id.btnHowToPlay);
        btnHowToPlay.setOnClickListener(v -> showTutorial());
    }

    private void showTutorial() {
        Intent intent = new Intent(this, TutorialActivity.class);
        startActivity(intent);
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
        menu.setMenu(R.layout.frag_sliding_menu);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void startImport() {
        // Delete realm db before creating new objects.
        Realm.deleteRealm(new RealmConfiguration.Builder().build());

        final List<PackJson> packsJson = JsonUtils.getPacksListFromJson(getAssets());
        processAndAddData(packsJson);
    }

    private void processAndAddData(final List<PackJson> packs) {
        if (packs.isEmpty()) return;
        final Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(bgRealm -> {
            Logger.d("Starting import");

            // Create "packs" <- "levels" <- "words" <- "chunks" realm objects.
            PacksRealmHelper.createPacks(bgRealm, packs);

            // Initialize game state for the first time in the beginning.
            GameRealmHelper.initFirstGameState(bgRealm);
        }, () -> {
            // Transaction was a success.
            Logger.d("Data was successfully saved");
            realm.close();
            showMainFragment();
        }, throwable -> {
            // OnError
            Logger.d("Could not save data" + throwable.toString());
            realm.close();
        });
    }

    private void showMainFragment() {
        ActivityUtils.addFragment(
                getSupportFragmentManager(),
                MainFragment.newInstance(),
                R.id.flActMain);
    }

    @Override
    public void startGameActivity(String levelId) {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        intent.putExtra(EXTRA_LEVEL_ID, levelId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    @Override
    public void showGameFinishedFragment() {
        Toast.makeText(this, "Game finished", Toast.LENGTH_SHORT).show();
//        ActivityUtils.replaceFragment(
//                getSupportFragmentManager(),
//                GameFinishedFrag.newInstance(),
//                R.id.flActMain);
    }

    @Override
    public void showPacksActivity() {
        Intent intent = new Intent(MainActivity.this, PacksActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    @Override
    public void onBackPressed() {
        if (menu.isMenuShowing()) {
            menu.toggle();  // close the sliding menu
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void showSlidingMenu() {
        menu.toggle();
    }
}