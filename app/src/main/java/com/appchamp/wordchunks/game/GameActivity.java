package com.appchamp.wordchunks.game;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.util.ActivityUtils;


public class GameActivity extends AppCompatActivity {

    private GamePresenter mGamePresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        GameFragment gameFragment =
                (GameFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (gameFragment == null) {
            // Create the fragment
            gameFragment = GameFragment.newInstance();

            Intent intent = getIntent();
            long levelId = intent.getLongExtra("LEVEL_ID", -1);

            Bundle bundle = new Bundle();
            bundle.putLong("LEVEL_ID", levelId);

            gameFragment.setArguments(bundle);
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), gameFragment, R.id.contentFrame);
        }

        // Create the presenter
        mGamePresenter = new GamePresenter(gameFragment);

    }

    public void onBackArrowClick(View v) {
        super.onBackPressed();
        Animation animFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        v.startAnimation(animFadeIn);
    }

}
