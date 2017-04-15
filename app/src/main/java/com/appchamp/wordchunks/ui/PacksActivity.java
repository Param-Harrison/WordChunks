package com.appchamp.wordchunks.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.models.Pack;
import com.appchamp.wordchunks.ui.adapters.PackAdapter;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class PacksActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView mListView;

    private PackAdapter mAdapter;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packs);

        if (mAdapter == null) {

            RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
            realm = Realm.getInstance(realmConfiguration);
            List<Pack> packs = realm.where(Pack.class).findAll();

            //This is the GridView adapter
            mAdapter = new PackAdapter(this);
            mAdapter.setData(packs);

            //This is the GridView which will display the list of cities
            mListView = (ListView) findViewById(R.id.packs_list);
            mListView.setAdapter(mAdapter);
            mListView.setOnItemClickListener(PacksActivity.this);
            mAdapter.notifyDataSetChanged();
            mListView.invalidate();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close(); // Remember to close Realm when done.
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Pack clickedPack = (Pack) mAdapter.getItem(position);
        Intent intent = new Intent(this, LevelsActivity.class);
        intent.putExtra("PACK_ID", clickedPack.getId());
        startActivity(intent);
    }

    public void onBackArrowClick(View v) {
        super.onBackPressed();
        Animation animFadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        v.startAnimation(animFadeIn);
    }
}