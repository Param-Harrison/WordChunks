package com.appchamp.wordchunks.game;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.data.DatabaseHelperRealm;
import com.appchamp.wordchunks.levels.LevelsActivity;
import com.appchamp.wordchunks.util.ActivityUtils;
import com.appchamp.wordchunks.util.AnimUtils;

import io.realm.Realm;

import static com.appchamp.wordchunks.util.Constants.EXTRA_LEVEL_ID;
import static com.appchamp.wordchunks.util.Constants.EXTRA_PACK_ID;
import static com.appchamp.wordchunks.util.Constants.PACK_STATE_CURRENT;


public class GameActivity extends AppCompatActivity {

//    private GamePresenter gamePresenter;

    private DatabaseHelperRealm db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        db = new DatabaseHelperRealm();

        // Create the presenter
//        gamePresenter = new GamePresenter(initGameFragment());
        initGameFragment();
    }

    private void initGameFragment() {
        GameFragment gameFragment =
                (GameFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (gameFragment == null) {
            // Create the fragment
            gameFragment = GameFragment.newInstance();

            // Getting levels via ids
            if (getIntent() != null) {
                String levelId = getIntent().getStringExtra(EXTRA_LEVEL_ID);
                if (levelId != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString(EXTRA_LEVEL_ID, levelId);
                    gameFragment.setArguments(bundle);
                    ActivityUtils.addFragmentToActivity(
                            getSupportFragmentManager(), gameFragment, R.id.contentFrame);
                }
            }
        }
    }

    public void onBackArrowClick(View v) {
        super.onBackPressed();
        AnimUtils.startAnimationFadeIn(GameActivity.this, v);
        startLevelsActivity();
    }

    /**
     * Back navigation. Navigates from GameActivity to LevelsActivity passing Pack id by the Intent.
     */
    private void startLevelsActivity() {
        Intent intent = new Intent(GameActivity.this, LevelsActivity.class);
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransaction(bgRealm -> {

            String packId = db.findPackIdByState(bgRealm, PACK_STATE_CURRENT);

            intent.putExtra(EXTRA_PACK_ID, packId);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        });
        realm.close();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startLevelsActivity();
    }
}
