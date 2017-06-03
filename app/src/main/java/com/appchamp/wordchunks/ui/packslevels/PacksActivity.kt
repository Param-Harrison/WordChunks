package com.appchamp.wordchunks.ui.packslevels

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.models.realm.Pack
import com.appchamp.wordchunks.util.Constants.EXTRA_PACK_ID
import com.appchamp.wordchunks.util.queryAll
import kotlinx.android.synthetic.main.act_packs_levels.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


class PacksActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_packs_levels)

        llPacksLevels.setBackgroundResource(R.drawable.gradient_packs)
        tvTitle.text = getString(R.string.title_select_pack)
        imgBackArrow.setOnClickListener { onBackPressed() }
        rvList.layoutManager = LinearLayoutManager(this)
        rvList.setHasFixedSize(true)

        OverScrollDecoratorHelper
                .setUpOverScroll(rvList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)
    }

    override fun onResume() {
        super.onResume()
        loadPacks()
    }

    private fun loadPacks() {
        val packs = Pack().queryAll()
        val adapter = PacksLevelsAdapter(packs) { startLevelsActivity(it.id) }
        rvList.adapter = adapter
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

    /**
     * Navigates from PacksActivity to LevelsActivity passing packId by the Intent.
     * @param packId pack id to be passed by the Intent.
     */
    private fun startLevelsActivity(packId: String?) {
        startActivity(intentFor<LevelsActivity>(EXTRA_PACK_ID to packId).clearTop())
        finish()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }
}