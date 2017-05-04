package com.appchamp.wordchunks.levels;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.game.GameActivity;
import com.appchamp.wordchunks.models.realm.Level;
import com.appchamp.wordchunks.models.realm.Pack;
import com.appchamp.wordchunks.packs.PacksActivity;
import com.appchamp.wordchunks.util.AnimUtils;

import java.util.List;

import io.realm.Realm;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.appchamp.wordchunks.util.Constants.EXTRA_LEVEL_ID;
import static com.appchamp.wordchunks.util.Constants.EXTRA_PACK_ID;
import static com.appchamp.wordchunks.util.Constants.REALM_FIELD_NAME_ID;


public class LevelsActivity extends AppCompatActivity {

    private LevelsAdapter adapter;

    private Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);

        RecyclerView rvLevels = (RecyclerView) findViewById(R.id.rvLevels);

        // Passing packs via ids
        if (getIntent() != null) {
            String packId = getIntent().getStringExtra(EXTRA_PACK_ID);
            if (packId != null) {

                realm = Realm.getDefaultInstance();
                Pack pack = realm.where(Pack.class)
                        .equalTo(REALM_FIELD_NAME_ID, packId)
                        .findFirst();

                List<Level> levels = pack.getLevels();
                adapter = new LevelsAdapter(levels);
                rvLevels.setAdapter(adapter);
                rvLevels.setLayoutManager(new LinearLayoutManager(LevelsActivity.this));

                adapter.setOnItemClickListener((view, position) -> {
                    Level clickedLevel = adapter.getItem(position);
                    startGameActivity(clickedLevel);
                });
            }
        }
        OverScrollDecoratorHelper.setUpOverScroll(
                rvLevels, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

    }


    private void startGameActivity(Level level) {
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(EXTRA_LEVEL_ID, level.getId());
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
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

    private void startPacksActivity() {
        Intent intent = new Intent(LevelsActivity.this, PacksActivity.class);
        realm.executeTransaction(bgRealm -> {
            Pack pack = bgRealm.where(Pack.class)
                    .equalTo("state", 1)
                    .findFirst();
            String packId = pack.getId();
            intent.putExtra(EXTRA_PACK_ID, packId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startPacksActivity();
    }
}