package com.appchamp.wordchunks.ui.game

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.data.LevelsRealmHelper
import com.appchamp.wordchunks.data.PacksRealmHelper
import com.appchamp.wordchunks.models.realm.Level
import com.appchamp.wordchunks.ui.game.fragments.GameFinishedFrag
import com.appchamp.wordchunks.ui.game.fragments.GameFrag
import com.appchamp.wordchunks.ui.game.fragments.LevelSolvedBeforeFrag
import com.appchamp.wordchunks.ui.game.fragments.LevelSolvedFrag
import com.appchamp.wordchunks.ui.game.listeners.OnBackToLevelsListener
import com.appchamp.wordchunks.ui.game.listeners.OnLevelSolvedListener
import com.appchamp.wordchunks.ui.game.listeners.OnNextLevelListener
import com.appchamp.wordchunks.ui.packslevels.LevelsActivity
import com.appchamp.wordchunks.ui.tutorial.TutorialActivity
import com.appchamp.wordchunks.util.ActivityUtils
import com.appchamp.wordchunks.util.Constants.EXTRA_PACK_ID
import com.appchamp.wordchunks.util.Constants.LEVEL_ID_KEY
import com.appchamp.wordchunks.util.Constants.PREFS_HOW_TO_PLAY
import com.appchamp.wordchunks.util.Constants.STATE_CURRENT
import com.appchamp.wordchunks.util.Constants.STATE_LOCKED
import com.appchamp.wordchunks.util.Constants.STATE_SOLVED
import com.appchamp.wordchunks.util.Constants.WORD_CHUNKS_PREFS
import io.realm.Realm
import org.jetbrains.anko.intentFor
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


class GameActivity : AppCompatActivity(), OnLevelSolvedListener, OnNextLevelListener,
        OnBackToLevelsListener {

    private var levelId: String? = null
    private var nextLevel: Level? = null
    private val handler = Handler()
    val realm: Realm = Realm.getDefaultInstance()

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
        addGameFragment()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        backToLevelsActivity()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close() // Remember to close Realm when done.
    }

    fun onBackArrowClick(v: View) {
        onBackPressed()
    }

    private fun showTutorial() {
        startActivity(intentFor<TutorialActivity>())
    }

    private fun addGameFragment() {
        // Getting level id via Intents
        levelId = intent.getStringExtra(LEVEL_ID_KEY)

        ActivityUtils.addFragment(
                supportFragmentManager,
                GameFrag.newInstance(levelId),
                R.id.flActMain)
    }

    /**
     * Back navigation. Navigates from GameActivity to LevelsActivity passing Pack id in the Intent.
     */
    private fun backToLevelsActivity() {
        val packId = LevelsRealmHelper.findLevelById(realm, levelId!!).packId
        // Passing levelId via Intent.
        startActivity(intentFor<LevelsActivity>(EXTRA_PACK_ID to packId))
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

    /**
     * This callback method called from the GameFrag immediately when the level was solved.
     */
    override fun onLevelSolved() {
        // Delay showing the fragment for more smoothness
//        handler.postDelayed({
        realm.executeTransaction { bgRealm ->

            val level = LevelsRealmHelper.findLevelById(bgRealm, levelId!!)

            // Reset all data of the solved level through its id.
            LevelsRealmHelper.resetLevelById(bgRealm, levelId!!)

            if (isLevelSolvedBefore(level)) {
                // Level WAS solved before
                showLevelSolvedBeforeFragment(level.fact)

            } else {
                // Level was NOT solved before
                // Change current level state to "solved", current level state is "current"
                level.state = STATE_SOLVED

                // Unlocking the next level to play in.

                // Getting the next level to play in.
                nextLevel = LevelsRealmHelper.findFirstLevelByState(bgRealm, STATE_LOCKED)

                var nextLevelClue = ""
                var nextPackColor = Color.parseColor("#f6aa4c")
                // If the next locked level exists
                if (nextLevel != null) {
                    // Unlock next level
                    nextLevel!!.state = STATE_CURRENT
                    nextLevelClue = nextLevel!!.clue!!
                    nextPackColor = Color.parseColor(
                            PacksRealmHelper.findFirstPackById(bgRealm, nextLevel!!.packId!!).color)
                }


                showLevelSolvedFragment(
                        nextPackColor,
                        nextLevelClue,
                        level.fact!!,
                        isPackSolved(bgRealm, level.packId))

            }
        }
//        }, 200)
    }

    private fun isLevelSolvedBefore(level: Level): Boolean {
        // Getting the state of the current level, if "current": return false, else true.
        return level.state != STATE_CURRENT
    }

    private fun isPackSolved(bgRealm: Realm, packId: String?): Long {
        // Count the number of current levels in pack, if 0 then we solved the whole pack

        if (LevelsRealmHelper
                .countLevelsByPackIdAndState(bgRealm, packId, STATE_CURRENT).toInt() == 0) {

            PacksRealmHelper.findFirstPackById(bgRealm, packId).state = STATE_SOLVED
            // Find the next locked pack and set it as "current"
            val nextLockedPack = PacksRealmHelper.findFirstPackByState(bgRealm, STATE_LOCKED)
            if (nextLockedPack != null) {
                nextLockedPack.state = STATE_CURRENT
            }
            return 0
        } else {
            val numberOfSolvedLevels = LevelsRealmHelper
                    .countLevelsByPackIdAndState(bgRealm, packId, STATE_SOLVED)
            val numberOfLevels = LevelsRealmHelper
                    .countLevelsByPackId(bgRealm, packId)
            return numberOfLevels - numberOfSolvedLevels
        }
    }

    private fun showLevelSolvedBeforeFragment(fact: String?) {
        val args = Bundle()
        val fragment = LevelSolvedBeforeFrag.newInstance()
        args.putString("fact", fact)
        fragment.arguments = args
        ActivityUtils.replaceFragment(
                supportFragmentManager,
                fragment,
                R.id.flActMain)
    }

    private fun showGameFinishedFragment() {
        ActivityUtils.replaceFragment(
                supportFragmentManager,
                GameFinishedFrag.newInstance(),
                R.id.flActMain)
    }

    private fun showLevelSolvedFragment(color: Int, clue: String, fact: String, left: Long) {
        ActivityUtils.replaceFragment(
                supportFragmentManager,
                LevelSolvedFrag.newInstance(color, clue, fact, left),
                R.id.flActMain)
    }

    override fun onNextLevelSelected() {
        if (nextLevel != null) {
            levelId = nextLevel!!.id
            ActivityUtils.replaceFragment(
                    supportFragmentManager,
                    GameFrag.newInstance(levelId),
                    R.id.flActMain)
        } else {
            showGameFinishedFragment()
        }
    }

    override fun onBackToLevelsSelected() {
        onBackPressed()
    }
}
