package com.appchamp.wordchunks.ui.game

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
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
import com.appchamp.wordchunks.ui.levels.LevelsActivity
import com.appchamp.wordchunks.ui.tutorial.TutorialActivity
import com.appchamp.wordchunks.util.ActivityUtils
import com.appchamp.wordchunks.util.AnimUtils
import com.appchamp.wordchunks.util.Constants.*
import io.realm.Realm
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import kotlin.properties.Delegates


class GameActivity : AppCompatActivity(), OnLevelSolvedListener, OnNextLevelListener,
        OnBackToLevelsListener {

    private var levelId: String? = null
    private var nextLevel: Level? = null
    private var realm: Realm by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_game)
        realm = Realm.getDefaultInstance()

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
        startLevelsActivity()
    }

    override fun onDestroy() {
        super.onDestroy()
        realm.close() // Remember to close Realm when done.
    }

    fun onBackArrowClick(v: View) {
        super.onBackPressed()
        AnimUtils.startAnimationFadeIn(this@GameActivity, v)
        startLevelsActivity()
    }

    private fun showTutorial() {
        val intent = Intent(this, TutorialActivity::class.java)
        startActivity(intent)
    }

    private fun addGameFragment() {
        // Getting level id via Intents
        levelId = intent.getStringExtra(EXTRA_LEVEL_ID)

        ActivityUtils.addFragment(
                supportFragmentManager,
                GameFrag.newInstance(),
                R.id.flActMain,
                EXTRA_LEVEL_ID,
                levelId!!)
    }

    /**
     * Back navigation. Navigates from GameActivity to LevelsActivity passing Pack id in the Intent.
     */
    private fun startLevelsActivity() {
        val intent = Intent(this, LevelsActivity::class.java)

        // Passing levelId via Intent.
        val packId = LevelsRealmHelper.findLevelById(realm, levelId!!).packId
        intent.putExtra(EXTRA_PACK_ID, packId)

        startActivity(intent)
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

    /**
     * This callback method called from the GameFrag immediately when the level was solved.
     */
    override fun onLevelSolved() {
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
                level.state = LEVEL_STATE_SOLVED

                // Unlocking the next level to play in.

                // Getting the next level to play in.
                nextLevel = LevelsRealmHelper.findFirstLevelByState(bgRealm, LEVEL_STATE_LOCKED)
                //                Logger.d("Next Level id  = " + nextLevel.getId());


                var nextLevelClue = ""
                var nextPackColor = Color.parseColor("#f6aa4c")
                // If the next locked level exists
                if (nextLevel != null) {
                    // Unlock next level
                    nextLevel!!.state = LEVEL_STATE_CURRENT
                    nextLevelClue = nextLevel!!.clue!!
                    nextPackColor = Color.parseColor(
                            PacksRealmHelper.findFirstPackById(bgRealm, nextLevel!!.packId!!).color)
                }

                showLevelSolvedFragment(
                        nextPackColor,
                        nextLevelClue,
                        level.fact,
                        isPackSolved(bgRealm, level.packId))
            }
        }
    }

    private fun isLevelSolvedBefore(level: Level): Boolean {
        // Getting the state of the current level, if "current": return false, else true.
        return level.state != LEVEL_STATE_CURRENT
    }

    private fun isPackSolved(bgRealm: Realm, packId: String?): Long {
        // Count the number of current levels in pack, if 0 then we solved the whole pack

        if (LevelsRealmHelper
                .countLevelsByPackIdAndState(bgRealm, packId, LEVEL_STATE_CURRENT).toInt() == 0) {

            PacksRealmHelper.findFirstPackById(bgRealm, packId).state = PACK_STATE_SOLVED
            // Find the next locked pack and set it as "current"
            val nextLockedPack = PacksRealmHelper.findFirstPackByState(bgRealm, PACK_STATE_LOCKED)
            if (nextLockedPack != null) {
                nextLockedPack.state = PACK_STATE_CURRENT
            }
            return 0
        } else {
            val numberOfSolvedLevels = LevelsRealmHelper
                    .countLevelsByPackIdAndState(bgRealm, packId, LEVEL_STATE_SOLVED)
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

    private fun showLevelSolvedFragment(color: Int, clue: String, fact: String?, left: Long) {
        val args = Bundle()
        val fragment = LevelSolvedFrag.newInstance()
        args.putInt("color", color)
        args.putString("clue", clue)
        args.putString("fact", fact)
        args.putLong("left", left)
        fragment.arguments = args
        ActivityUtils.replaceFragment(
                supportFragmentManager,
                fragment,
                R.id.flActMain)
    }

    override fun onNextLevelSelected() {
        if (nextLevel != null) {
            levelId = nextLevel!!.id
            ActivityUtils.replaceFragment(
                    supportFragmentManager,
                    GameFrag.newInstance(),
                    R.id.flActMain,
                    EXTRA_LEVEL_ID,
                    levelId!!)
        } else {
            showGameFinishedFragment()
        }
    }

    override fun onBackToLevelsSelected() {
        super.onBackPressed()
        startLevelsActivity()
    }
}
