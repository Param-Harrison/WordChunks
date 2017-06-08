package com.appchamp.wordchunks.ui.packslevels

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.extensions.queryFirst
import com.appchamp.wordchunks.models.realm.Level
import com.appchamp.wordchunks.models.realm.Pack
import com.appchamp.wordchunks.ui.game.GameActivity
import com.appchamp.wordchunks.util.Constants.EXTRA_PACK_ID
import com.appchamp.wordchunks.util.Constants.LEVEL_ID_KEY
import com.appchamp.wordchunks.util.Constants.REALM_FIELD_ID
import kotlinx.android.synthetic.main.act_packs_levels.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


class LevelsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_packs_levels)

        llPacksLevels.setBackgroundResource(R.drawable.gradient_levels)
        tvTitle.text = getString(R.string.title_select_level)
        imgBackArrow.setOnClickListener { onBackPressed() }
        rvList.layoutManager = LinearLayoutManager(this)
        rvList.setHasFixedSize(true)

        OverScrollDecoratorHelper
                .setUpOverScroll(rvList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)
    }

    override fun onResume() {
        super.onResume()
        // Getting selected pack through its id to load levels
        val packId = requireNotNull(intent.getStringExtra(EXTRA_PACK_ID),
                { "Activity parameter 'EXTRA_PACK_ID' is missing" })

        packId.let {
            val levels = Pack().queryFirst { it.equalTo(REALM_FIELD_ID, packId) }?.levels
            levels?.let(this::initLevelsAdapter)
        }
    }

    private fun initLevelsAdapter(levels: List<Level>) {
        val adapter = PacksLevelsAdapter(levels) { startGameActivity(it.id) }
        rvList.adapter = adapter
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        backToPacksActivity()
    }

    /**
     * Navigates from LevelsActivity to GameActivity passing Level id by the Intent.
     *
     * @param levelId level id to be passed by the Intent.
     */
    private fun startGameActivity(levelId: String?) {
        startActivity(intentFor<GameActivity>(LEVEL_ID_KEY to levelId))
        finish()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    /**
     * Navigates from LevelsActivity to PacksActivity.
     */
    private fun backToPacksActivity() {
        startActivity(intentFor<PacksActivity>().clearTop())
        finish()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }
}