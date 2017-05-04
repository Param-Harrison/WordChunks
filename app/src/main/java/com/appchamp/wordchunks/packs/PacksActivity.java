package com.appchamp.wordchunks.packs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.levels.LevelsActivity;
import com.appchamp.wordchunks.models.realm.Pack;
import com.appchamp.wordchunks.util.AnimUtils;

import java.util.List;

import io.realm.Realm;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.appchamp.wordchunks.util.Constants.EXTRA_PACK_ID;


public class PacksActivity extends AppCompatActivity {

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packs);

        RecyclerView rvPacks = (RecyclerView) findViewById(R.id.rvPacks);

        realm = Realm.getDefaultInstance();

        // This is the RecyclerView adapter which will display the list of packs
        List<Pack> packs = realm.where(Pack.class).findAll();

        PacksAdapter adapter = new PacksAdapter(packs);

        rvPacks.setAdapter(adapter);
        rvPacks.setLayoutManager(new LinearLayoutManager(this));

        adapter.setOnItemClickListener((view, position) -> {
            Pack clickedPack = adapter.getItem(position);
            startLevelsActivity(clickedPack);
        });

        OverScrollDecoratorHelper.setUpOverScroll(
                rvPacks, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
    }

    private void startLevelsActivity(Pack pack) {
        Intent intent = new Intent(this, LevelsActivity.class);
        intent.putExtra(EXTRA_PACK_ID, pack.getId());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    public void onBackArrowClick(View v) {
        super.onBackPressed();
        AnimUtils.startAnimationFadeIn(PacksActivity.this, v);
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
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

}