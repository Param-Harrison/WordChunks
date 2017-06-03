package com.appchamp.wordchunks.ui.main


import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.data.GameRealmHelper
import com.appchamp.wordchunks.data.PacksRealmHelper
import com.appchamp.wordchunks.models.pojo.PackJson
import com.appchamp.wordchunks.models.pojo.packsFromJSONFile
import com.appchamp.wordchunks.ui.game.GameActivity
import com.appchamp.wordchunks.ui.packslevels.PacksActivity
import com.appchamp.wordchunks.ui.tutorial.TutorialActivity
import com.appchamp.wordchunks.util.ActivityUtils
import com.appchamp.wordchunks.util.Constants.JSON_FILE_NAME
import com.appchamp.wordchunks.util.Constants.LEVEL_ID_KEY
import com.appchamp.wordchunks.util.Constants.PREFS_REALM_CREATE_OBJECTS
import com.appchamp.wordchunks.util.Constants.WORD_CHUNKS_PREFS
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.frag_sliding_menu.*
import org.jetbrains.anko.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


class MainActivity : AppCompatActivity(), AnkoLogger, OnMainFragmentClickListener {

    private var menu: SlidingMenu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_main)

        val sp = getSharedPreferences(WORD_CHUNKS_PREFS, Context.MODE_PRIVATE)
        val isRealmFileExists = sp.getBoolean(PREFS_REALM_CREATE_OBJECTS, true)
        if (isRealmFileExists) {
            // update an existing realm objects here
            // updateRealmDb();
            showMainFragment()
        } else {
            // create realm objects for the first time
            startImport()
        }
        initLeftMenu()

        btnHowToPlay.setOnClickListener { showTutorial() }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun startGameActivity(levelId: String?) {
        startActivity(intentFor<GameActivity>(LEVEL_ID_KEY to levelId).clearTop())
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    override fun showGameFinishedFragment() {
        TODO()
    }

    override fun showPacksActivity() {
        startActivity(intentFor<PacksActivity>().clearTop())
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    override fun onBackPressed() {
        if (menu!!.isMenuShowing) {
            menu!!.toggle()  // close the sliding menu
        } else {
            super.onBackPressed()
        }
    }

    override fun showSlidingMenu() {
        menu!!.toggle()
    }

    private fun showTutorial() {
        startActivity<TutorialActivity>()
    }

    private fun initLeftMenu() {
        // Configure the SlidingMenu
        menu = SlidingMenu(this)
        menu!!.mode = SlidingMenu.LEFT
        menu!!.touchModeAbove = SlidingMenu.TOUCHMODE_FULLSCREEN
        menu!!.setShadowWidthRes(R.dimen.shadow_width)
        menu!!.setShadowDrawable(R.drawable.shadow)
        menu!!.setBehindOffsetRes(R.dimen.slidingmenu_offset)
        menu!!.setFadeDegree(0.35f)
        menu!!.attachToActivity(this, SlidingMenu.SLIDING_CONTENT)
        menu!!.setMenu(R.layout.frag_sliding_menu)
    }

    private fun startImport() {
        // Delete realm db before creating new objects.
        Realm.deleteRealm(RealmConfiguration.Builder().build())

        // More complex operations can be executed on another thread, for example using
        // Anko's doAsync extension method.
        doAsync {
            info("Starting import")
            processData(packsFromJSONFile(act, JSON_FILE_NAME))

            activityUiThread {
                // done
                showMainFragment()
            }
        }
    }

    private fun processData(packs: List<PackJson>) {
        if (packs.isEmpty()) return
        // Open the default realm. All threads must use its own reference to the realm.
        // Those can not be transferred across threads.
        Realm.getDefaultInstance().use { realm ->
            // Add packs in one transaction
            realm.executeTransaction {
                // Create "packs" <- "levels" <- "words" <- "chunks" realm objects.
                PacksRealmHelper.createPacks(realm, packs)
                // Initialize game state for the first time in the beginning.
                GameRealmHelper.initFirstGameState(realm)
            }
        }
    }

    private fun showMainFragment() {
        ActivityUtils.addFragment(
                supportFragmentManager,
                MainFragment.newInstance(),
                R.id.flActMain)
    }
}