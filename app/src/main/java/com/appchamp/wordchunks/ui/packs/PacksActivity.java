package com.appchamp.wordchunks.ui.packs;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.data.PacksRealmHelper;
import com.appchamp.wordchunks.models.realm.Pack;
import com.appchamp.wordchunks.ui.levels.LevelsActivity;

import java.util.List;

import io.realm.Realm;
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static com.appchamp.wordchunks.util.Constants.EXTRA_PACK_ID;


public class PacksActivity extends AppCompatActivity {

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_packs_levels);

        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(getString(R.string.title_select_pack));
        RecyclerView rvPacks = (RecyclerView) findViewById(R.id.recyclerView);

        realm = Realm.getDefaultInstance();

        // This is the RecyclerView adapter which will display the list of packs
        List<Pack> packs = PacksRealmHelper.findAllPacks(realm);

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
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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