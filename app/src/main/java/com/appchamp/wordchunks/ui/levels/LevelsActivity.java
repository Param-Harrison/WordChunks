package com.appchamp.wordchunks.ui.levels;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.data.LevelsRealmHelper;
import com.appchamp.wordchunks.data.PacksRealmHelper;
import com.appchamp.wordchunks.models.realm.Level;
import com.appchamp.wordchunks.ui.game.GameActivity;
import com.appchamp.wordchunks.ui.packs.PacksActivity;

import java.util.List;

import io.realm.Realm;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.appchamp.wordchunks.util.Constants.EXTRA_LEVEL_ID;
import static com.appchamp.wordchunks.util.Constants.EXTRA_PACK_ID;


public class LevelsActivity extends AppCompatActivity {

    private Realm realm;
    private RecyclerView rvLevels;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_packs_levels);
        LinearLayout llPacksLevels = (LinearLayout) findViewById(R.id.llPacksLevels);
        llPacksLevels.setBackgroundResource(R.drawable.gradient_levels);
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(getString(R.string.title_select_level));
        rvLevels = (RecyclerView) findViewById(R.id.recyclerView);


        realm = Realm.getDefaultInstance();

        // Passing packs via ids
        String packId = getIntent().getStringExtra(EXTRA_PACK_ID);
        if (packId != null) {
            List<Level> levels = LevelsRealmHelper.INSTANCE.findLevelsByPackId(realm, packId);

            final int packColor = Color.parseColor(PacksRealmHelper.INSTANCE
                    .findFirstPackById(realm, levels.get(0).getPackId()).getColor());

            initLevelsAdapter(levels, packColor);
        }
        OverScrollDecoratorHelper.setUpOverScroll(
                rvLevels, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
    }

    private void initLevelsAdapter(List<Level> levels, int packColor) {
        LevelsAdapter adapter = new LevelsAdapter(levels, packColor);
        rvLevels.setAdapter(adapter);
        rvLevels.setLayoutManager(new LinearLayoutManager(LevelsActivity.this));
        rvLevels.setHasFixedSize(true);

        adapter.setOnItemClickListener((view, position) -> {
            Level clickedLevel = adapter.getItem(position);
            startGameActivity(clickedLevel.getId());
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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
        startPacksActivity();
    }

    /**
     * Up navigation. Navigates from LevelsActivity to GameActivity passing Level id by the Intent.
     *
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