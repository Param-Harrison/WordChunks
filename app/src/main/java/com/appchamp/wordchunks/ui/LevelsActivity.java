package com.appchamp.wordchunks.ui;

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
import com.appchamp.wordchunks.models.Level;
import com.appchamp.wordchunks.models.Pack;
import com.appchamp.wordchunks.ui.adapters.LevelAdapter;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class LevelsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView mListView;

    private LevelAdapter mAdapter;

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
            if (mAdapter == null) {
                Pack pack = realm.where(Pack.class).equalTo("id", packId).findFirst();

                List<Level> levels = pack.getLevels();

                //This is the GridView adapter
                mAdapter = new LevelAdapter(this);
                mAdapter.setData(levels);

                //This is the GridView which will display the list of cities
                mListView = (ListView) findViewById(R.id.levels_list);
                mListView.setAdapter(mAdapter);
                mListView.setOnItemClickListener(LevelsActivity.this);
                mAdapter.notifyDataSetChanged();
                mListView.invalidate();
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Level clickedLevel = (Level) mAdapter.getItem(position);
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra("LEVEL_ID", clickedLevel.getId());
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close(); // Remember to close Realm when done.
    }

    public void onBackArrowClick(View v) {
        super.onBackPressed();
        Animation animFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        v.startAnimation(animFadeIn);
    }
}