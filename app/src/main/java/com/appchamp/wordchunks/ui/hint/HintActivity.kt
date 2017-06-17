package com.appchamp.wordchunks.ui.hint

import android.arch.lifecycle.LifecycleRegistry
import android.arch.lifecycle.LifecycleRegistryOwner
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.util.Constants.EXTRA_LEVEL_ID
import kotlinx.android.synthetic.main.titlebar.*


class HintActivity :  AppCompatActivity(), LifecycleRegistryOwner {

    private lateinit var levelId: String

    private val lifecycleRegistry: LifecycleRegistry by lazy { LifecycleRegistry(this) }

    override fun getLifecycle(): LifecycleRegistry = lifecycleRegistry

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_hint)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .add(R.id.flActHint, HintFirstFragment())
                    .commit()
        }
        // Getting level id through Intents
        levelId = requireNotNull(intent.getStringExtra(EXTRA_LEVEL_ID),
                { "Activity parameter 'EXTRA_LEVEL_ID' is missing" })

        val factory = HintViewModel.Factory(application, levelId)
        ViewModelProviders.of(this, factory).get(HintViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()

        imgBackArrow?.setOnClickListener { onBackPressed() }
        tvTitle?.text = getString(R.string.show_a_hint_for)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }
}
