package com.appchamp.wordchunks.packs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.data.DatabaseHelperRealm;
import com.appchamp.wordchunks.levels.LevelsActivity;
import com.appchamp.wordchunks.models.realm.Pack;
import com.appchamp.wordchunks.util.AnimUtils;

import java.util.List;

import io.realm.Realm;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.appchamp.wordchunks.util.Constants.EXTRA_PACK_ID;


public class PacksActivity extends AppCompatActivity {

    private Realm realm;

    private DatabaseHelperRealm db;

    private List<Pack> packs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packs);

        RecyclerView rvPacks = (RecyclerView) findViewById(R.id.rvPacks);

        db = new DatabaseHelperRealm();

        realm = Realm.getDefaultInstance();

        // This is the RecyclerView adapter which will display the list of packs
        packs = db.findAllPacks(realm);

        PacksAdapter adapter = new PacksAdapter(packs);
        rvPacks.setAdapter(adapter);
        rvPacks.setLayoutManager(new LinearLayoutManager(PacksActivity.this));
        rvPacks.setHasFixedSize(true);

        adapter.setOnItemClickListener((view, position) -> {
            Pack clickedPack = adapter.getItem(position);
            startLevelsActivity(clickedPack.getId());
        });

        OverScrollDecoratorHelper.setUpOverScroll(
                rvPacks, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close(); // Remember to close Realm when done.
    }

    public void onBackArrowClick(View v) {
        super.onBackPressed();
        AnimUtils.startAnimationFadeIn(PacksActivity.this, v);
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
    }

    /**
     * Up navigation. Navigates from PacksActivity to LevelsActivity passing packId by the Intent.
     * @param packId pack id to be passed by the Intent.
     */
    private void startLevelsActivity(String packId) {
        Intent intent = new Intent(PacksActivity.this, LevelsActivity.class);
        intent.putExtra(EXTRA_PACK_ID, packId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }
}