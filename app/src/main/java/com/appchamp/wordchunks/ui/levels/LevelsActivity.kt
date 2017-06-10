package com.appchamp.wordchunks.ui.levels

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.realmdb.models.realm.Level
import com.appchamp.wordchunks.ui.BaseLifecycleActivity
import com.appchamp.wordchunks.ui.game.GameActivity
import com.appchamp.wordchunks.ui.packs.PacksActivity
import com.appchamp.wordchunks.ui.packs.PacksLevelsAdapter
import com.appchamp.wordchunks.util.Constants.EXTRA_LEVEL_ID
import com.appchamp.wordchunks.util.Constants.EXTRA_PACK_ID
import io.realm.RealmResults
import kotlinx.android.synthetic.main.act_packs_levels.*
import kotlinx.android.synthetic.main.titlebar.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor


class LevelsActivity : BaseLifecycleActivity<LevelsViewModel>() {

    override val viewModelClass = LevelsViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_packs_levels)
        llPacksLevels.setBackgroundResource(R.drawable.gradient_levels)
        tvTitle.text = getString(R.string.title_select_level)
        rvList.layoutManager = LinearLayoutManager(this)
        rvList.setHasFixedSize(true)

        // Sets smooth scroll effect.
        OverScrollDecoratorHelper.setUpOverScroll(
                rvList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)

        imgBackArrow.setOnClickListener { onBackPressed() }

        subscribeToModel()
    }

    private fun subscribeToModel() {
        val packId = intent.getStringExtra(EXTRA_PACK_ID)
        // Observe updates to the LiveData levels.
        viewModel
                .getLevels(packId)
                .observe(this, Observer<RealmResults<Level>> {
                    // update UI
                    val adapter = PacksLevelsAdapter<Level> {
                        // Navigates up to GameActivity passing levelId in the Intent.
                        startGameActivity(it.id)
                    }
                    it?.let {
                        adapter.updateItems(it)
                    }
                    rvList.adapter = adapter
                })

        // Scrolling RecyclerView to the last "current", or "solved" level item.
        // indexOfLast gets last index, or -1 if the list does not contain that item.
        rvList.smoothScrollToPosition(viewModel.getLastLevelPos())
    }

    private fun startGameActivity(levelId: String) {
        startActivity(intentFor<GameActivity>(EXTRA_LEVEL_ID to levelId).clearTop())
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        finish()
    }

    /**
     * Navigates back to PacksActivity.
     */
    override fun onBackPressed() {
        super.onBackPressed()
        backToPacksActivity()
    }

    /**
     * Navigates back to PacksActivity.
     */
    private fun backToPacksActivity() {
        startActivity(intentFor<PacksActivity>().clearTop())
        finish()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }
}