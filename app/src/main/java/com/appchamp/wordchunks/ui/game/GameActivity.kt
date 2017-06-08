package com.appchamp.wordchunks.ui.game

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.extensions.color
import com.appchamp.wordchunks.extensions.queryFirst
import com.appchamp.wordchunks.models.realm.Level
import com.appchamp.wordchunks.models.realm.Pack
import com.appchamp.wordchunks.ui.game.fragments.GameFinishedFrag
import com.appchamp.wordchunks.ui.game.fragments.GameFrag
import com.appchamp.wordchunks.ui.game.fragments.LevelSolvedBeforeFrag
import com.appchamp.wordchunks.ui.game.fragments.LevelSolvedFrag
import com.appchamp.wordchunks.ui.game.hint.HintActivity
import com.appchamp.wordchunks.ui.game.listeners.OnGameFragClickListener
import com.appchamp.wordchunks.ui.game.listeners.OnNextLevelListener
import com.appchamp.wordchunks.ui.packslevels.LevelsActivity
import com.appchamp.wordchunks.ui.tutorial.TutorialActivity
import com.appchamp.wordchunks.util.ActivityUtils
import com.appchamp.wordchunks.util.Constants
import com.appchamp.wordchunks.util.Constants.EXTRA_LEVEL_ID
import com.appchamp.wordchunks.util.Constants.EXTRA_PACK_ID
import com.appchamp.wordchunks.util.Constants.LEVEL_ID_KEY
import com.appchamp.wordchunks.util.Constants.PREFS_HOW_TO_PLAY
import com.appchamp.wordchunks.util.Constants.REALM_FIELD_ID
import com.appchamp.wordchunks.util.Constants.REALM_FIELD_STATE
import com.appchamp.wordchunks.util.Constants.STATE_CURRENT
import com.appchamp.wordchunks.util.Constants.STATE_LOCKED
import com.appchamp.wordchunks.util.Constants.STATE_SOLVED
import com.appchamp.wordchunks.util.Constants.WORD_CHUNKS_PREFS
import io.realm.Realm
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


class GameActivity : AppCompatActivity(), OnGameFragClickListener, OnNextLevelListener {

    private lateinit var levelId: String
    private var nextLevel: Level? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_game)

        val sp = getSharedPreferences(WORD_CHUNKS_PREFS, Context.MODE_PRIVATE)
        val editor = sp.edit()

        if (sp.getBoolean(PREFS_HOW_TO_PLAY, true)) {
            // show how to play
            showTutorial()
            editor.putBoolean(PREFS_HOW_TO_PLAY, false)
            editor.apply()
        }
        // Getting level id through Intents
        levelId = requireNotNull(intent.getStringExtra(LEVEL_ID_KEY),
                { "Activity parameter 'LEVEL_ID_KEY' is missing" })
        addGameFragment()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        backToLevelsActivity()
    }

    private fun showTutorial() {
        startActivity<TutorialActivity>()
    }

    private fun addGameFragment() {
        ActivityUtils.addFragment(
                supportFragmentManager,
                GameFrag.newInstance(levelId),
                R.id.flActMain)
    }

    private fun replaceGameFragment() {
        ActivityUtils.replaceFragment(
                supportFragmentManager,
                GameFrag.newInstance(levelId),
                R.id.flActMain)
    }

    /**
     * Back navigation. Navigates from GameActivity to LevelsActivity passing Pack id in the Intent.
     */
    private fun backToLevelsActivity() {
        val packId = Level().queryFirst { it.equalTo(REALM_FIELD_ID, levelId) }?.packId
        // Passing pack's id through the Intent.
        startActivity(intentFor<LevelsActivity>(EXTRA_PACK_ID to packId))
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

    /**
     * This callback method called from the GameFrag when the level was solved.
     */
    override fun onLevelSolved() {
        Realm.getDefaultInstance().let {
            val level = it.where(Level::class.java)
                    .equalTo(REALM_FIELD_ID, levelId)
                    .findFirst()

            if (level.state == STATE_SOLVED) {

                showLevelSolvedBeforeFragment(level.fact)

            } else if (level.state == STATE_CURRENT) {

                it.executeTransaction { level.state = Constants.STATE_SOLVED }

                // Is next level exists?
                nextLevel = it.where(Level::class.java)
                        .equalTo(REALM_FIELD_STATE, STATE_LOCKED)
                        .findFirst()
                // if so
                if (nextLevel != null) {

                    showLevelSolvedFragment(
                            Color.parseColor(nextLevel!!.color),
                            nextLevel!!.clue,
                            level.fact,
                            countLeftLevels(level.packId))

                    it.executeTransaction { nextLevel!!.state = STATE_CURRENT }

                } else {
                    showLevelSolvedFragment(
                            color(R.color.btn_rect_game_finish),
                            resources.getString(R.string.congratulations),
                            level.fact,
                            -1)
                }
                isPackSolved(it, level.packId)
            }
        }
    }

    override fun onHintClick() {
        // Passing level's id through the Intent.
        startActivity(intentFor<HintActivity>(EXTRA_LEVEL_ID to levelId))
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    private fun countLeftLevels(packId: String): Int = Pack()
            .queryFirst { it.equalTo(REALM_FIELD_ID, packId) }?.levels
            ?.count { it.state == STATE_LOCKED }!!

    private fun isPackSolved(realm: Realm, packId: String) {
        // If we solved the whole pack
        val pack = realm.where(Pack::class.java)
                .equalTo(REALM_FIELD_ID, packId)
                .findFirst()
        if (!pack?.levels?.any { it.state == STATE_CURRENT }!!) {
            // Change its state to "solved"
            realm.executeTransaction { pack.state = STATE_SOLVED }

            // Find next locked pack to play in
            val nextPack = realm.where(Pack::class.java)
                    .equalTo(REALM_FIELD_STATE, STATE_LOCKED)
                    .findFirst()
            if (nextPack != null) {
                realm.executeTransaction { nextPack.state = STATE_CURRENT }
            }
        }
    }

    private fun showLevelSolvedFragment(color: Int, clue: String, fact: String, left: Int) {
        ActivityUtils.replaceFragment(
                supportFragmentManager,
                LevelSolvedFrag.newInstance(color, clue, fact, left),
                R.id.flActMain)
    }

    private fun showLevelSolvedBeforeFragment(fact: String?) {
        ActivityUtils.replaceFragment(
                supportFragmentManager,
                LevelSolvedBeforeFrag.newInstance(fact),
                R.id.flActMain)
    }

    private fun showGameFinishedFragment() {
        ActivityUtils.replaceFragment(
                supportFragmentManager,
                GameFinishedFrag.newInstance(),
                R.id.flActMain)
    }

    override fun onNextLevelSelected() {
        if (nextLevel != null) {
            levelId = nextLevel!!.id
            replaceGameFragment()
        } else {
            showGameFinishedFragment()
        }
    }

    override fun onBackToLevelsClick() = onBackPressed()
}