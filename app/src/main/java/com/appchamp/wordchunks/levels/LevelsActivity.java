package com.appchamp.wordchunks.levels;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.game.GameActivity;
import com.appchamp.wordchunks.models.Level;
import com.appchamp.wordchunks.models.Pack;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class LevelsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listView;

    private LevelsAdapter adapter;

    private Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_levels);

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        realm = Realm.getInstance(realmConfiguration);

        Intent intent = getIntent();
        long packId = intent.getLongExtra("PACK_ID", -1);

        if (packId != -1) {

            adapter = new LevelsAdapter(new ArrayList<>(0));

            Pack pack = realm.where(Pack.class).equalTo("id", packId).findFirst();

            List<Level> levels = pack.getLevels();

            adapter.replaceLevels(levels);

            listView = (ListView) findViewById(R.id.levels_list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(LevelsActivity.this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close(); // Remember to close Realm when done.
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Level clickedLevel = (Level) adapter.getItem(position);
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("LEVEL_ID", clickedLevel.getId());
        startActivity(intent);
    }

    public void onBackArrowClick(View v) {
        super.onBackPressed();
        Animation animFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        v.startAnimation(animFadeIn);
    }
}