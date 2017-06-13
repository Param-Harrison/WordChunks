package com.appchamp.wordchunks.ui.game

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.realmdb.models.realm.Level
import com.appchamp.wordchunks.realmdb.models.realm.Word
import com.appchamp.wordchunks.ui.aftergame.AfterGameActivity
import com.appchamp.wordchunks.ui.hint.HintActivity
import com.appchamp.wordchunks.ui.tutorial.TutorialActivity
import com.appchamp.wordchunks.util.ActivityUtils
import com.appchamp.wordchunks.util.Constants.EXTRA_LEVEL_ID
import com.appchamp.wordchunks.util.Constants.PREFS_HOW_TO_PLAY
import com.appchamp.wordchunks.util.Constants.WORD_CHUNKS_PREFS
import io.realm.RealmResults
import kotlinx.android.synthetic.main.frag_game.*
import kotlinx.android.synthetic.main.titlebar.*
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


/**
 * GameActivity contains GameFragment, manages navigation to the previous and next activity,
 * handles intent extra data, shows tutorial activity on the first run.
 * GameViewModel shares level ID between this activity and its fragment.
 */
class GameActivity : LifecycleActivity() {

    private lateinit var levelId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_game)

        val sp = getSharedPreferences(WORD_CHUNKS_PREFS, Context.MODE_PRIVATE)
        val editor = sp.edit()
        // Shows tutorial on first game start
        if (sp.getBoolean(PREFS_HOW_TO_PLAY, true)) {
            // Show how to play tutorial
            startTutorialActivity()
            // Never show tutorial again
            editor.putBoolean(PREFS_HOW_TO_PLAY, false)
            editor.apply()
        }
        subscribeUi()

        // Add game fragment if this is first creation
        if (savedInstanceState == null) {
            ActivityUtils.addFragment(supportFragmentManager, GameFragment(), R.id.fragment_container)
        }
    }

    // Sets custom fonts.
    // (This is a temporary solution until Android O release).
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    private fun subscribeUi() {
        // Getting level id through the Intent.
        levelId = requireNotNull(intent.getStringExtra(EXTRA_LEVEL_ID),
                { "Activity parameter 'EXTRA_LEVEL_ID' is missing" })

        val factory = GameViewModel.Factory(application, levelId)

        val viewModel = ViewModelProviders.of(this, factory).get(GameViewModel::class.java)

        // Observe updates to the LiveData level.
        viewModel.getLevel()
                .observe(this, Observer<Level> {
                    // update UI titlebar
                    it?.let { tvTitle.text = it.clue }
                })
        viewModel.getLiveWords().observe(this, Observer<RealmResults<Word>> {
            it?.let {
                if (viewModel.isLevelSolved()) {
                    startAfterGameActivity()
                }
            }
        })
    }

    fun startAfterGameActivity() {
        startActivity(intentFor<AfterGameActivity>(
                EXTRA_LEVEL_ID to levelId).clearTop())
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        finish()  // to go back on the levels screen from after game activity
    }

    override fun onStart() {
        super.onStart()
        // Sets click listeners
        imgBackArrow.setOnClickListener { onBackPressed() }
        imgHint.setOnClickListener { startHintActivity() }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        backToLevelsActivity()
    }

    /**
     * Back navigation. Navigates from GameActivity to LevelsActivity passing Pack id in the Intent.
     */
    private fun backToLevelsActivity() {
        //val packId = Level().queryFirst { it.equalTo(REALM_FIELD_ID, levelId) }?.packId
        // Passing pack's id through the Intent.
        //startActivity(intentFor<LevelsActivity>(EXTRA_PACK_ID to packId))
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

    private fun startTutorialActivity() {
        startActivity<TutorialActivity>()
    }

    fun startHintActivity() {
        // Passing level's id through the Intent.
        startActivity(intentFor<HintActivity>(EXTRA_LEVEL_ID to levelId))
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }
}