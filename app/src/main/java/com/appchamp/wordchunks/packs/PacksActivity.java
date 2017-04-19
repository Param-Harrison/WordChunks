package com.appchamp.wordchunks.packs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.levels.LevelsActivity;
import com.appchamp.wordchunks.models.Pack;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;


public class PacksActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private ListView listView;

    private PacksAdapter adapter;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packs);

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder().build();
        realm = Realm.getInstance(realmConfiguration);

        adapter = new PacksAdapter(new ArrayList<>(0));

        List<Pack> packs = realm.where(Pack.class).findAll();

        adapter.replacePacks(packs);

        listView = (ListView) findViewById(R.id.packs_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(PacksActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close(); // Remember to close Realm when done.
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Pack clickedPack = (Pack) adapter.getItem(position);
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