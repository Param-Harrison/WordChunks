package com.appchamp.wordchunks.ui.game;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.data.LevelsRealmHelper;
import com.appchamp.wordchunks.data.PacksRealmHelper;
import com.appchamp.wordchunks.models.realm.Level;
import com.appchamp.wordchunks.models.realm.Pack;
import com.appchamp.wordchunks.ui.game.fragments.GameFinishedFrag;
import com.appchamp.wordchunks.ui.game.fragments.GameFrag;
import com.appchamp.wordchunks.ui.game.fragments.LevelAlreadyCompletedFrag;
import com.appchamp.wordchunks.ui.game.fragments.LevelSolvedFrag;
import com.appchamp.wordchunks.ui.game.listeners.OnBackToLevelsListener;
import com.appchamp.wordchunks.ui.game.listeners.OnLevelSolvedListener;
import com.appchamp.wordchunks.ui.game.listeners.OnNextLevelListener;
import com.appchamp.wordchunks.ui.levels.LevelsActivity;
import com.appchamp.wordchunks.util.ActivityUtils;
import com.appchamp.wordchunks.util.AnimUtils;
import com.orhanobut.logger.Logger;

import io.realm.Realm;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.appchamp.wordchunks.util.Constants.EXTRA_LEVEL_ID;
import static com.appchamp.wordchunks.util.Constants.EXTRA_PACK_ID;
import static com.appchamp.wordchunks.util.Constants.LEVEL_STATE_CURRENT;
import static com.appchamp.wordchunks.util.Constants.LEVEL_STATE_LOCKED;
import static com.appchamp.wordchunks.util.Constants.LEVEL_STATE_SOLVED;
import static com.appchamp.wordchunks.util.Constants.PACK_STATE_CURRENT;
import static com.appchamp.wordchunks.util.Constants.PACK_STATE_LOCKED;
import static com.appchamp.wordchunks.util.Constants.PACK_STATE_SOLVED;


public class GameActivity extends AppCompatActivity implements OnLevelSolvedListener,
        OnNextLevelListener, OnBackToLevelsListener {

    private String levelId;
    private String nextlevelId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_game);

        initGameFragment();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startLevelsActivity();
    }

    public void onBackArrowClick(View v) {
        super.onBackPressed();
        AnimUtils.startAnimationFadeIn(GameActivity.this, v);
        startLevelsActivity();
    }

    private void initGameFragment() {
        // Getting level id via Intents
        if (getIntent() != null) {
            levelId = getIntent().getStringExtra(EXTRA_LEVEL_ID);

            ActivityUtils.addFragment(
                    getSupportFragmentManager(),
                    GameFrag.newInstance(),
                    R.id.flActMain,
                    EXTRA_LEVEL_ID,
                    levelId);
        }
    }

    /**
     * Back navigation. Navigates from GameActivity to LevelsActivity passing Pack id in the Intent.
     */
    private void startLevelsActivity() {
        Intent intent = new Intent(GameActivity.this, LevelsActivity.class);
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(bgRealm -> {
            String packId = LevelsRealmHelper.findLevelById(bgRealm, levelId).getPackId();
            intent.putExtra(EXTRA_PACK_ID, packId);
        });
        realm.close();
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    /**
     * This callback method called from the GameFrag when the level is solved.
     */
    @Override
    public void onLevelSolved() {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(bgRealm -> {

            Level currentLevel = LevelsRealmHelper.findLevelById(bgRealm, levelId);

            // Getting the state of the current level, if "current":
            if (currentLevel.getState() == LEVEL_STATE_CURRENT) {

                // Change current level state into "solved", if current level state is "current".
                currentLevel.setState(LEVEL_STATE_SOLVED);

                String currentLevelPackId = currentLevel.getPackId();


                // Reset the data of this solved level via its id.
                LevelsRealmHelper.resetLevelById(bgRealm, levelId);


                // Getting the next level to play in.

                Level nextLevel = LevelsRealmHelper.findFirstLevelByState(realm, LEVEL_STATE_LOCKED);

                // If the next locked level exists
                if (nextLevel != null) {

                    // Change its state from "locked to "current".
                    nextLevel.setState(LEVEL_STATE_CURRENT);

                    // Set this "locked" level id as the "current" level id.
                    nextlevelId = nextLevel.getId();

                } else {
                    // If there is no more locked levels, show the game finished fragment.
                    // Current level stays current.
                    //levelId = currentLevel.getId();

                    showGameFinishedFragment();
                }
                isPackSolved(bgRealm, currentLevelPackId);
                showLevelSolvedFragment();
            } else {
                // if current level has state "solved" then navigate back to levels activity
                // todo show level already solved screen

//                levelId = LevelsRealmHelper
//                        .findLastLevelByState(bgRealm, LEVEL_STATE_CURRENT).getId();

                showLevelAlreadyCompletedFragment();
            }
        });
        realm.close();
    }

    private boolean isPackSolved(Realm realm, String packId) {
        // Count the number of current levels in pack, if 0 then we solved the whole pack

        if (LevelsRealmHelper
                .countLevelsByPackIdAndState(realm, packId, LEVEL_STATE_CURRENT) == 0) {

            Logger.d("GEEEEEEEEEEEEEEEEEEEEEEEEE");

            PacksRealmHelper.findFirstPackById(realm, packId).setState(PACK_STATE_SOLVED);
            // Find the next locked pack and set it as "current"
            Pack nextLockedPack = PacksRealmHelper.findFirstPackByState(realm, PACK_STATE_LOCKED);
            if (nextLockedPack != null) {
                nextLockedPack.setState(PACK_STATE_CURRENT);
            }
            return true;
        } else {

            Logger.d("GAAAAAAAAAAAAAAAAAAAAAAAAA");
            return false;
        }
    }

    private void showLevelAlreadyCompletedFragment() {
        // level already completed fragment
        ActivityUtils.replaceFragment(
                getSupportFragmentManager(),
                LevelAlreadyCompletedFrag.newInstance(),
                R.id.flActMain);
    }

    private void showGameFinishedFragment() {
        ActivityUtils.replaceFragment(
                getSupportFragmentManager(),
                GameFinishedFrag.newInstance(),
                R.id.flActMain);
    }

    private void showLevelSolvedFragment() {
        ActivityUtils.replaceFragment(
                getSupportFragmentManager(),
                LevelSolvedFrag.newInstance(),
                R.id.flActMain);
    }

    @Override
    public void onNextLevelSelected() {
        ActivityUtils.replaceFragment(
                getSupportFragmentManager(),
                GameFrag.newInstance(),
                R.id.flActMain,
                EXTRA_LEVEL_ID,
                nextlevelId);
    }

    @Override
    public void onBackToLevelsSelected() {
        super.onBackPressed();
        startLevelsActivity();
    }
}
