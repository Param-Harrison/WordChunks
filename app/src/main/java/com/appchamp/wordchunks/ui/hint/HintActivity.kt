package com.appchamp.wordchunks.ui.hint

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.util.ActivityUtils
import com.appchamp.wordchunks.util.Constants.EXTRA_LEVEL_ID


class HintActivity : AppCompatActivity(), OnHintFirstFragClickListener,
        OnHintSecondFragClickListener {

    lateinit var levelId: String
    var isHintSecondFragShowing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_hint)

        // Getting level id through Intents
        levelId = requireNotNull(intent.getStringExtra(EXTRA_LEVEL_ID),
                { "Activity parameter 'EXTRA_LEVEL_ID' is missing" })
        addHintFirstFragment(levelId)
    }

    private fun addHintFirstFragment(levelId: String) {
        ActivityUtils.addFragment(
                supportFragmentManager,
                HintFirstFragment.newInstance(levelId),
                R.id.flActHint)
    }

    private fun replaceHintFirstFragment(levelId: String) {
        ActivityUtils.replaceFragment(
                supportFragmentManager,
                HintFirstFragment.newInstance(levelId),
                R.id.flActHint)
    }

    override fun onBackPressed() {
        if (isHintSecondFragShowing) {
            replaceHintFirstFragment(levelId)
        } else {
            super.onBackPressed()
        }
    }

    override fun onHintWordSelected(wordId: String) {
        isHintSecondFragShowing = true
        ActivityUtils.replaceFragment(
                supportFragmentManager,
                HintSecondFragment.newInstance(wordId),
                R.id.flActHint)
    }

    override fun onBackArrowFromHintSecondClick() {
        replaceHintFirstFragment(levelId)
    }

    override fun onCloseFromHintSecondClick() {
        super.onBackPressed()
    }

    override fun onBackArrowFromHintFirstClick() {
        onBackPressed()
    }
}
