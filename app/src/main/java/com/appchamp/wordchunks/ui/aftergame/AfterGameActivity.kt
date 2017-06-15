package com.appchamp.wordchunks.ui.aftergame

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.realmdb.models.realm.LevelState
import com.appchamp.wordchunks.ui.levels.LevelsActivity
import com.appchamp.wordchunks.util.ActivityUtils
import com.appchamp.wordchunks.util.Constants
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


class AfterGameActivity : LifecycleActivity(), AnkoLogger {

    private lateinit var levelId: String

    private val viewModel by lazy {
        val factory = AfterGameViewModel.Factory(application, levelId)
        ViewModelProviders.of(this, factory).get(AfterGameViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_after_game)

        subscribeUi()
    }

    // Sets custom fonts. (This is a temporary solution until Android O release).
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    private fun subscribeUi() {
        // Getting solved level ID by the Intent.
        levelId = requireNotNull(intent.getStringExtra(Constants.EXTRA_LEVEL_ID),
                { "Activity parameter 'EXTRA_LEVEL_ID' is missing" })

        // Get a state of the solved level, then show corresponding fragment
        when (viewModel.getLevelState()) {
        // If level in progress was solved
            LevelState.IN_PROGRESS.value -> addLevelSolvedFragment()
        // If level solved before was solved
            LevelState.FINISHED.value -> addLevelSolvedBeforeFragment()
        }
    }

    private fun addLevelSolvedFragment() {
        ActivityUtils.addFragment(
                supportFragmentManager,
                LevelSolvedFragment(),
                R.id.fragment_container)
    }

    private fun addLevelSolvedBeforeFragment() {
        ActivityUtils.addFragment(
                supportFragmentManager,
                LevelSolvedBeforeFragment(),
                R.id.fragment_container)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        backToLevelsActivity(viewModel.getPackId())
    }

    /**
     * Back navigation. Navigates to LevelsActivity passing Pack id by the Intent.
     */
    private fun backToLevelsActivity(packId: String) {
        // Passing pack's id by the Intent.
        startActivity(intentFor<LevelsActivity>(Constants.EXTRA_PACK_ID to packId).clearTop())
        finish()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }
}