package com.appchamp.wordchunks.ui.finish

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.appchamp.wordchunks.R
import kotlinx.android.synthetic.main.act_finish.*
import org.jetbrains.anko.browse
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


class FinishActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_finish)
    }

    override fun onResume() {
        super.onResume()

        rlRateUs.setOnClickListener { browse("market://details?id=$packageName") }
    }

    // Sets custom fonts.
    // (This is a temporary solution until Android O release).
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}
