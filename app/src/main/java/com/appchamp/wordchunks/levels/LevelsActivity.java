package com.appchamp.wordchunks.levels;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.data.DatabaseHelperRealm;
import com.appchamp.wordchunks.game.GameActivity;
import com.appchamp.wordchunks.models.realm.Level;
import com.appchamp.wordchunks.packs.PacksActivity;
import com.appchamp.wordchunks.util.AnimUtils;

import java.util.List;

import io.realm.Realm;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.appchamp.wordchunks.util.Constants.EXTRA_LEVEL_ID;
import static com.appchamp.wordchunks.util.Constants.EXTRA_PACK_ID;


public class LevelsActivity extends AppCompatActivity {

    private LevelsAdapter adapter;

    private Realm realm;

    private DatabaseHelperRealm db;

    private List<Level> levels;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);

        RecyclerView rvLevels = (RecyclerView) findViewById(R.id.rvLevels);

        db = new DatabaseHelperRealm();

        realm = Realm.getDefaultInstance();

        // Passing packs via ids
        if (getIntent() != null) {
            String packId = getIntent().getStringExtra(EXTRA_PACK_ID);
            if (packId != null) {
                levels = db.findLevelsByPackId(realm, packId);

                adapter = new LevelsAdapter(levels);
                rvLevels.setAdapter(adapter);
                rvLevels.setLayoutManager(new LinearLayoutManager(LevelsActivity.this));
                rvLevels.setHasFixedSize(true);

                adapter.setOnItemClickListener((view, position) -> {
                    Level clickedLevel = adapter.getItem(position);
                    startGameActivity(clickedLevel.getId());
                });
            }
        }
        OverScrollDecoratorHelper.setUpOverScroll(
                rvLevels, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startPacksActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close(); // Remember to close Realm when done.
    }

    public void onBackArrowClick(View v) {
        super.onBackPressed();
        AnimUtils.startAnimationFadeIn(LevelsActivity.this, v);
        startPacksActivity();
    }

    /**
     * Up navigation. Navigates from LevelsActivity to GameActivity passing Level id by the Intent.
     * @param levelId level id to be passed by the Intent.
     */
    private void startGameActivity(String levelId) {
        Intent intent = new Intent(LevelsActivity.this, GameActivity.class);
        intent.putExtra(EXTRA_LEVEL_ID, levelId);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    /**
     * Back navigation. Navigates from LevelsActivity to PacksActivity.
     */
    private void startPacksActivity() {
        Intent intent = new Intent(LevelsActivity.this, PacksActivity.class);
        realm.executeTransaction(bgRealm -> {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        });
    }
}