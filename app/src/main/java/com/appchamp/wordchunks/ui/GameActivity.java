package com.appchamp.wordchunks.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.models.Level;
import com.appchamp.wordchunks.models.Word;
import com.appchamp.wordchunks.ui.adapters.ChunkAdapter;
import com.appchamp.wordchunks.ui.adapters.WordAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class GameActivity extends AppCompatActivity {

    private ListView mWordsGridView;

    private GridView mChunksGridView;

    WordAdapter mAdapter;

    ChunkAdapter mChunkAdapter;

    private Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        realm = Realm.getInstance(realmConfiguration);

        Intent intent = getIntent();
        long levelId = intent.getLongExtra("LEVEL_ID", -1);

        if (levelId != -1) {
            if (mAdapter == null) {
                Level level = realm.where(Level.class).equalTo("id", levelId).findFirst();

                List<Word> words = level.getWords();

                //This is the GridView adapter
                mAdapter = new WordAdapter(this);
                mAdapter.setData(words);

                //This is the GridView which will display the list of cities
                mWordsGridView = (ListView) findViewById(R.id.words_grid);
                mWordsGridView.setAdapter(mAdapter);
                //mWordsGridView.setOnItemClickListener(PacksActivity.this);
                mAdapter.notifyDataSetChanged();
                mWordsGridView.invalidate();

                TextView packNameTextView = (TextView) findViewById(R.id.level_clue);
                packNameTextView.setText(level.getClue());

                mChunkAdapter = new ChunkAdapter(this);

                List<String> chunks = new ArrayList<>(40);
                for (int i = 0; i < words.size(); i++) {
                    Collections.addAll(chunks, words.get(i).getChunks().split(","));
                }
                mChunkAdapter.setData(chunks);
                //This is the GridView which will display the list of cities
                mChunksGridView = (GridView) findViewById(R.id.chunks_grid);
                mChunksGridView.setAdapter(mChunkAdapter);
                //mWordsGridView.setOnItemClickListener(PacksActivity.this);
                mChunkAdapter.notifyDataSetChanged();
                mChunksGridView.invalidate();

            }
        }
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
