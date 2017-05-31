package com.appchamp.wordchunks.ui.tutorial;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.appchamp.wordchunks.R;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class TutorialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_tutorial);

        ImageView imgClose = (ImageView) findViewById(R.id.imgClose);
        imgClose.setOnClickListener(v -> {
            onBackPressed();
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}