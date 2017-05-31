package com.appchamp.wordchunks.ui.game;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.data.LevelsRealmHelper;
import com.appchamp.wordchunks.data.PacksRealmHelper;
import com.appchamp.wordchunks.models.realm.Level;
import com.appchamp.wordchunks.models.realm.Pack;
import com.appchamp.wordchunks.ui.game.fragments.GameFinishedFrag;
import com.appchamp.wordchunks.ui.game.fragments.GameFrag;
import com.appchamp.wordchunks.ui.game.fragments.LevelSolvedBeforeFrag;
import com.appchamp.wordchunks.ui.game.fragments.LevelSolvedFrag;
import com.appchamp.wordchunks.ui.game.listeners.OnBackToLevelsListener;
import com.appchamp.wordchunks.ui.game.listeners.OnLevelSolvedListener;
import com.appchamp.wordchunks.ui.game.listeners.OnNextLevelListener;
import com.appchamp.wordchunks.ui.levels.LevelsActivity;
import com.appchamp.wordchunks.ui.tutorial.TutorialActivity;
import com.appchamp.wordchunks.util.ActivityUtils;
import com.appchamp.wordchunks.util.AnimUtils;

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
import static com.appchamp.wordchunks.util.Constants.PREFS_HOW_TO_PLAY;
import static com.appchamp.wordchunks.util.Constants.WORD_CHUNKS_PREFS;


public class GameActivity extends AppCompatActivity implements OnLevelSolvedListener,
        OnNextLevelListener, OnBackToLevelsListener {

    private String levelId;
    private Level nextLevel;
    private Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_game);
        realm = Realm.getDefaultInstance();

        SharedPreferences sp = getSharedPreferences(WORD_CHUNKS_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if (sp.getBoolean(PREFS_HOW_TO_PLAY, true)) {
            // show how to play
            showTutorial();
            editor.putBoolean(PREFS_HOW_TO_PLAY, false);
            editor.apply();
        }
        addGameFragment();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close(); // Remember to close Realm when done.
    }

    public void onBackArrowClick(View v) {
        super.onBackPressed();
        AnimUtils.startAnimationFadeIn(GameActivity.this, v);
        startLevelsActivity();
    }

    private void showTutorial() {
        Intent intent = new Intent(this, TutorialActivity.class);
        startActivity(intent);
    }

    private void addGameFragment() {
        // Getting level id via Intents
        levelId = getIntent().getStringExtra(EXTRA_LEVEL_ID);

        ActivityUtils.addFragment(
                getSupportFragmentManager(),
                GameFrag.newInstance(),
                R.id.flActMain,
                EXTRA_LEVEL_ID,
                levelId);
    }

    /**
     * Back navigation. Navigates from GameActivity to LevelsActivity passing Pack id in the Intent.
     */
    private void startLevelsActivity() {
        Intent intent = new Intent(GameActivity.this, LevelsActivity.class);

        // Passing levelId via Intent.
        String packId = LevelsRealmHelper.findLevelById(realm, levelId).getPackId();
        intent.putExtra(EXTRA_PACK_ID, packId);

        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    /**
     * This callback method called from the GameFrag immediately when the level was solved.
     */
    @Override
    public void onLevelSolved() {
        realm.executeTransaction(bgRealm -> {

            Level level = LevelsRealmHelper.findLevelById(bgRealm, levelId);
            int packColor = Color.parseColor(
                    PacksRealmHelper.findFirstPackById(bgRealm, level.getPackId())
                            .getColor());

            if (isLevelSolvedBefore(level)) {
                // Level WAS solved before
                showLevelSolvedBeforeFragment(packColor, level.getFact());

            } else {
                // Level was NOT solved before
                // Change current level state to "solved", current level state is "current"
                level.setState(LEVEL_STATE_SOLVED);

                // Unlocking the next level to play in.

                // Getting the next level to play in.
                nextLevel = LevelsRealmHelper.findFirstLevelByState(bgRealm, LEVEL_STATE_LOCKED);
//                Logger.d("Next Level id  = " + nextLevel.getId());

                String nextLevelClue = "";
                // If the next locked level exists
                if (nextLevel != null) {
                    // Unlock next level
                    nextLevel.setState(LEVEL_STATE_CURRENT);
                    nextLevelClue = nextLevel.getClue();
                }

                showLevelSolvedFragment(
                        packColor,
                        nextLevelClue,
                        level.getFact(),
                        isPackSolved(bgRealm, level.getPackId()));
            }
        });
    }

    private boolean isLevelSolvedBefore(Level level) {
        // Getting the state of the current level, if "current": return false, else true.
        return level.getState() != LEVEL_STATE_CURRENT;
    }

    private long isPackSolved(Realm bgRealm, String packId) {
        // Count the number of current levels in pack, if 0 then we solved the whole pack

        if (LevelsRealmHelper
                .countLevelsByPackIdAndState(bgRealm, packId, LEVEL_STATE_CURRENT) == 0) {

            PacksRealmHelper.findFirstPackById(bgRealm, packId).setState(PACK_STATE_SOLVED);
            // Find the next locked pack and set it as "current"
            Pack nextLockedPack = PacksRealmHelper.findFirstPackByState(bgRealm, PACK_STATE_LOCKED);
            if (nextLockedPack != null) {
                nextLockedPack.setState(PACK_STATE_CURRENT);
            }
            return 0;
        } else {
            long numberOfSolvedLevels = LevelsRealmHelper
                    .countLevelsByPackIdAndState(bgRealm, packId, LEVEL_STATE_SOLVED);
            long numberOfLevels = LevelsRealmHelper
                    .countLevelsByPackId(bgRealm, packId);
            return numberOfLevels - numberOfSolvedLevels;
        }
    }

    private void showLevelSolvedBeforeFragment(int color, String fact) {
        Bundle args = new Bundle();
        Fragment fragment = LevelSolvedBeforeFrag.newInstance();
        args.putInt("color", color);
        args.putString("fact", fact);
        fragment.setArguments(args);
        ActivityUtils.replaceFragment(
                getSupportFragmentManager(),
                fragment,
                R.id.flActMain);
    }

    private void showGameFinishedFragment() {
        ActivityUtils.replaceFragment(
                getSupportFragmentManager(),
                GameFinishedFrag.newInstance(),
                R.id.flActMain);
    }

    private void showLevelSolvedFragment(int color, String clue, String fact, long left) {
        Bundle args = new Bundle();
        Fragment fragment = LevelSolvedFrag.newInstance();
        args.putInt("color", color);
        args.putString("clue", clue);
        args.putString("fact", fact);
        args.putLong("left", left);
        fragment.setArguments(args);
        ActivityUtils.replaceFragment(
                getSupportFragmentManager(),
                fragment,
                R.id.flActMain);
    }

    @Override
    public void onNextLevelSelected() {
        if (nextLevel != null) {
            levelId = nextLevel.getId();
            ActivityUtils.replaceFragment(
                    getSupportFragmentManager(),
                    GameFrag.newInstance(),
                    R.id.flActMain,
                    EXTRA_LEVEL_ID,
                    levelId);
        } else {
            showGameFinishedFragment();
        }
    }

    @Override
    public void onBackToLevelsSelected() {
        super.onBackPressed();
        startLevelsActivity();
    }
}
