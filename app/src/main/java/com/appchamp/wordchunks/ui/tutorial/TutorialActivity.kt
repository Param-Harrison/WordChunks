package com.appchamp.wordchunks.ui.tutorial

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.appchamp.wordchunks.R
import kotlinx.android.synthetic.main.act_tutorial.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


class TutorialActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_tutorial)

        imgClose.setOnClickListener { onBackPressed() }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}