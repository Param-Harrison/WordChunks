package com.appchamp.wordchunks.ui.aftergame

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.extensions.queryFirst
import com.appchamp.wordchunks.realmdb.models.realm.Level
import com.appchamp.wordchunks.realmdb.models.realm.LevelState
import com.appchamp.wordchunks.realmdb.models.realm.Pack
import com.appchamp.wordchunks.util.ActivityUtils
import com.appchamp.wordchunks.util.Constants
import io.realm.Realm
import org.jetbrains.anko.AnkoLogger
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


class AfterGameActivity : LifecycleActivity(), AnkoLogger {

    private lateinit var levelId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_after_game)

        subscribeUi()
    }

    // Sets custom fonts.
    // (This is a temporary solution until Android O release).
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    private fun subscribeUi() {
        // Getting solved level ID by the Intent.
        levelId = requireNotNull(intent.getStringExtra(Constants.EXTRA_LEVEL_ID),
                { "Activity parameter 'EXTRA_LEVEL_ID' is missing" })

        val factory = AfterGameViewModel.Factory(application, levelId)

        val viewModel = ViewModelProviders.of(this, factory).get(AfterGameViewModel::class.java)

        // Observe updates to the LiveData level.
        viewModel.getLevel().observe(this, Observer<Level> {
            // Update UI, inject corresponding fragment
            it?.let {
                // Get a state of the solved level, then show corresponding fragment
                when(viewModel.getLevelState()) {
                    // If level in progress was solved
                    LevelState.IN_PROGRESS.value -> addLevelSolvedFragment()
                    // If level solved before was solved
                    LevelState.FINISHED.value -> addLevelSolvedBeforeFragment()
                }
            }
            viewModel.resetLevel()
        })
    }

    private fun addLevelSolvedFragment() {
        ActivityUtils.addFragment(
                supportFragmentManager,
                LevelSolvedFragment.newInstance(),
                R.id.fragment_container)
    }

    private fun addLevelSolvedBeforeFragment() {
        ActivityUtils.addFragment(
                supportFragmentManager,
                LevelSolvedBeforeFragment.newInstance(),
                R.id.fragment_container)
    }


    // level solved
    //        Realm.getDefaultInstance().let {
//            val level = it.where(Level::class.java)
//                    .equalTo(REALM_FIELD_ID, levelId)
//                    .findFirst()
//
//            if (level.state == STATE_SOLVED) {
//
//                addLevelSolvedBeforeFragment(level.fact)
//
//            } else if (level.state == STATE_CURRENT) {
//
//                it.executeTransaction { level.state = Constants.STATE_SOLVED }
//
//                // Is next level exists?
//                nextLevel = it.where(Level::class.java)
//                        .equalTo(REALM_FIELD_STATE, STATE_LOCKED)
//                        .findFirst()
//                // if so
//                if (nextLevel != null) {
//
//                    addLevelSolvedFragment(
//                            Color.parseColor(nextLevel!!.color),
//                            nextLevel!!.clue,
//                            level.fact,
//                            countLeftLevels(level.packId))
//
//                    it.executeTransaction { nextLevel!!.state = STATE_CURRENT }
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

    private fun countLeftLevels(packId: String): Int = Pack()
            .queryFirst { it.equalTo(Constants.REALM_FIELD_ID, packId) }?.levels
            ?.count { it.state == Constants.STATE_LOCKED }!!

    private fun isPackSolved(realm: Realm, packId: String) {
        // If we solved the whole pack
        val pack = realm.where(Pack::class.java)
                .equalTo(Constants.REALM_FIELD_ID, packId)
                .findFirst()
        if (!pack?.levels?.any { it.state == Constants.STATE_CURRENT }!!) {
            // Change its state to "solved"
            realm.executeTransaction { pack.state = Constants.STATE_SOLVED }

            // Find next locked pack to play in
            val nextPack = realm.where(Pack::class.java)
                    .equalTo(Constants.REALM_FIELD_STATE, Constants.STATE_LOCKED)
                    .findFirst()
            if (nextPack != null) {
                realm.executeTransaction { nextPack.state = Constants.STATE_CURRENT }
            }
        }
    }


//    override fun onNextLevelSelected() {
//        if (nextLevel != null) {
//            levelId = nextLevel!!.id
//            replaceGameFragment()
//        } else {
//            showGameFinishedFragment()
//        }
//    }

    private fun showGameFinishedFragment() {
        ActivityUtils.replaceFragment(
                supportFragmentManager,
                GameFinishedFragment.newInstance(),
                R.id.fragment_container)
    }
}
