package com.appchamp.wordchunks.ui.finish

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.appchamp.wordchunks.R
import kotlinx.android.synthetic.main.act_finish.*
import org.jetbrains.anko.browse


class FinishActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_finish)
    }

    override fun onResume() {
        super.onResume()

        rlRateUs.setOnClickListener { browse("market://details?id=$packageName") }
    }
}
