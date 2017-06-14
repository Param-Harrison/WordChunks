package com.appchamp.wordchunks.ui.aftergame

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.realmdb.models.realm.LevelState
import com.appchamp.wordchunks.util.ActivityUtils
import com.appchamp.wordchunks.util.Constants
import org.jetbrains.anko.AnkoLogger
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


class AfterGameActivity : LifecycleActivity(), AnkoLogger {

    private lateinit var levelId: String

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

        val factory = AfterGameViewModel.Factory(application, levelId)

        val viewModel = ViewModelProviders.of(this, factory).get(AfterGameViewModel::class.java)

        // Get a state of the solved level, then show corresponding fragment
        when (viewModel.getLevelState()) {
        // If level in progress was solved
            LevelState.IN_PROGRESS.value -> addLevelSolvedFragment()
        // If level solved before was solved
            LevelState.FINISHED.value -> addLevelSolvedBeforeFragment()
        }
//        when (viewModel.findNextLevel()) {
//            0 -> startActivity(intentFor<GameActivity>())
//            -1 -> startFinishActivity()
//        }
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
                LevelSolvedBeforeFragment.newInstance(),
                R.id.fragment_container)
    }
}
//                if (nextLevel != null) {
//
//                    addLevelSolvedFragment(
//                            Color.parseColor(nextLevel!!.color),
//                            nextLevel!!.clue,
//                            level.fact,
//                            countLeftLevels(level.packId))
//
//                    it.executeTransaction { nextLevel!!.state = STATE_IN_PROGRESS }
//
//                } else {
//                    addLevelSolvedFragment(
//                            color(R.color.btn_rect_game_finish),
//                            resources.getString(R.string.congratulations),
//                            level.fact,
//                            -1)
//                }
//                isPackSolved(it, level.packId)
//            }
//        }

//    private fun countLeftLevels(packId: String): Int = Pack()
//            .queryFirst { it.equalTo(Constants.REALM_FIELD_ID, packId) }?.levels
//            ?.count { it.state == Constants.STATE_LOCKED }!!
//
//    override fun onNextLevelSelected() {
//        if (nextLevel != null) {
//            levelId = nextLevel!!.id
//            replaceGameFragment()
//        } else {
//            showGameFinishedFragment()
//        }