package com.appchamp.wordchunks.ui.packs

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.realmdb.models.realm.Pack
import com.appchamp.wordchunks.ui.BaseLifecycleActivity
import com.appchamp.wordchunks.ui.levels.LevelsActivity
import com.appchamp.wordchunks.util.Constants.EXTRA_PACK_ID
import io.realm.RealmResults
import kotlinx.android.synthetic.main.act_packs_levels.*
import kotlinx.android.synthetic.main.titlebar.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor


class PacksActivity : BaseLifecycleActivity<PacksViewModel>() {

    override val viewModelClass = PacksViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_packs_levels)
        llPacksLevels.setBackgroundResource(R.drawable.gradient_packs)
        tvTitle.text = getString(R.string.title_select_pack)
        rvList.layoutManager = LinearLayoutManager(this)
        rvList.setHasFixedSize(true)

        // Sets smooth scroll effect.
        OverScrollDecoratorHelper.setUpOverScroll(
                rvList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)

        imgBackArrow.setOnClickListener { onBackPressed() }

        subscribeToModel()
    }

    private fun subscribeToModel() {
        // Observe updates to our LiveData packs.
        viewModel
                .getPacks()
                .observe(this, Observer<RealmResults<Pack>> {
                    // update UI
                    val adapter = PacksLevelsAdapter<Pack> {
                        // Navigates to LevelsActivity passing packId in the Intent.
                        startLevelsActivity(it.id)
                    }
                    it?.let { adapter.updateItems(it) }
                })

        // Scrolling RecyclerView to the last "current", or "solved" level item.
        // indexOfLast gets last index, or -1 if the list does not contain that item.
        rvList.smoothScrollToPosition(viewModel.getLastPackPos())
    }

    /**
     * Navigates back to MainActivity.
     */
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }

    /**
     * Navigates back to MainActivity.
     */
    private fun startLevelsActivity(packId: String) {
        startActivity(intentFor<LevelsActivity>(EXTRA_PACK_ID to packId).clearTop())
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
        finish()
    }
}