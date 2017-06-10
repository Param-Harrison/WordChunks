package com.appchamp.wordchunks.ui.main


import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.appchamp.wordchunks.BuildConfig
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.data.GameDao
import com.appchamp.wordchunks.data.PacksDao
import com.appchamp.wordchunks.realmdb.models.pojo.PackJson
import com.appchamp.wordchunks.realmdb.models.pojo.packsFromJSONFile
import com.appchamp.wordchunks.ui.game.GameActivity
import com.appchamp.wordchunks.ui.game.fragments.GameFinishedFrag
import com.appchamp.wordchunks.ui.packs.PacksActivity
import com.appchamp.wordchunks.ui.tutorial.TutorialActivity
import com.appchamp.wordchunks.util.ActivityUtils
import com.appchamp.wordchunks.util.Constants.FILE_NAME_DATA_JSON
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

    private lateinit var menu: SlidingMenu

    private var isGameFinished = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.act_main)

        val sp = getSharedPreferences(WORD_CHUNKS_PREFS, Context.MODE_PRIVATE)
        val isRealmFileExists = sp.getBoolean(PREFS_REALM_CREATE_OBJECTS, true)
        if (isRealmFileExists) {
            // update an existing realm objects here
            // updateRealmDb();
            addMainFragment()
        } else {
            // create realm objects for the first time
            startImport()
        }
        initLeftMenu()
    }

    override fun onStart() {
        super.onStart()

        btnHowToPlay.setOnClickListener { showTutorial() }
        btnRateUs.setOnClickListener { browse("market://details?id=$packageName") }
        btnFeedback.setOnClickListener {
            email(
                    "jkozhukhovskaya@gmail.com",
                    "Feedback for WordChunks",
                    """
                        -----------------
                        ID: ${BuildConfig.APPLICATION_ID}
                        App version: ${BuildConfig.VERSION_CODE}
                        Device: ${Build.MODEL}
                        System version: ${Build.VERSION.RELEASE}
                        -----------------

                        """
            )
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun startGameActivity(levelId: String?) {
        startActivity(intentFor<GameActivity>(LEVEL_ID_KEY to levelId).clearTop())
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    override fun showGameFinishedFragment() {
        isGameFinished = true
        ActivityUtils.replaceFragment(
                supportFragmentManager,
                GameFinishedFrag.newInstance(),
                R.id.flActMain)
    }

    override fun showPacksActivity() {
        startActivity(intentFor<PacksActivity>().clearTop())
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    override fun onBackPressed() {
        when {
            isGameFinished -> replaceMainFragment()
            menu.isMenuShowing -> menu.toggle()  // Collapse the sliding menu
            else -> super.onBackPressed()
        }
    }

    private fun showTutorial() = startActivity<TutorialActivity>()

    private fun initLeftMenu() {
        menu = SlidingMenu(this)
        // Configure the SlidingMenu
        menu.mode = SlidingMenu.LEFT
        menu.touchModeAbove = SlidingMenu.TOUCHMODE_FULLSCREEN
        menu.setShadowWidthRes(R.dimen.shadow_width)
        menu.setShadowDrawable(R.drawable.shadow)
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset)
        menu.setFadeDegree(0.35f)
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT)
        menu.setMenu(R.layout.frag_sliding_menu)

    }

    override fun showSlidingMenu() = menu.toggle()

    private fun startImport() {
        // Delete realm db before creating new objects.
        Realm.deleteRealm(RealmConfiguration.Builder().build())

        // More complex operations can be executed on another thread, for example using
        // Anko's doAsync extension method.
        doAsync {
            processData(packsFromJSONFile(act, FILE_NAME_DATA_JSON))

            activityUiThread {
                // when done
                addMainFragment()
            }
        }
    }

    private fun processData(packs: List<PackJson>) {
        if (packs.isEmpty()) return
        // Open the default realm. All threads must use its own reference to the realm.
        // Those can not be transferred across threads.
        Realm.getDefaultInstance().use {
            // Add packs in one transaction
            it.executeTransaction {
                // Create "packs" <- "levels" <- "words" <- "chunks" realm objects.
                PacksDao.createPacks(it, packs)
                // Initialize game state for the first time in the beginning.
                GameDao.initFirstGameState(it)
            }
        }
    }

    private fun addMainFragment() {
        ActivityUtils.addFragment(
                supportFragmentManager,
                MainFragment.newInstance(),
                R.id.flActMain)
    }

    private fun replaceMainFragment() {
        ActivityUtils.replaceFragment(
                supportFragmentManager,
                MainFragment.newInstance(),
                R.id.flActMain)
    }
}