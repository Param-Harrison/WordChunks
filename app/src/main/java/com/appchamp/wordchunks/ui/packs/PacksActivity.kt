package com.appchamp.wordchunks.ui.packs

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.realmdb.models.realm.Pack
import com.appchamp.wordchunks.ui.BaseLifecycleActivity
import com.appchamp.wordchunks.util.Constants.STATE_CURRENT
import com.appchamp.wordchunks.util.Constants.STATE_SOLVED
import io.realm.RealmResults
import kotlinx.android.synthetic.main.act_packs_levels.*
import kotlinx.android.synthetic.main.titlebar.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper


class PacksActivity : BaseLifecycleActivity<PacksViewModel>() {

    // Android will instantiate my ViewModel for me, and the best part is
    // the viewModel will survive configurationChanges!
    override val viewModelClass = PacksViewModel::class.java

    private val adapter by lazy {
        viewModel.getAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUi()

        subscribe()
    }

    /**
     *  Sets titlebar text, background drawable, adapter, click listeners, and smooth scroll effect.
     */
    private fun setUi() {
        setContentView(R.layout.act_packs_levels)
        llPacksLevels.setBackgroundResource(R.drawable.gradient_packs)
        tvTitle.text = getString(R.string.title_select_pack)
        rvList.layoutManager = LinearLayoutManager(this)
        rvList.setHasFixedSize(true)
        rvList.adapter = adapter
        OverScrollDecoratorHelper.setUpOverScroll(
                rvList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)
        imgBackArrow.setOnClickListener { onBackPressed() }
    }

    private fun subscribe() {
        // Observe updates to our LiveData packs.

        viewModel.getPacks().observe(this, Observer<RealmResults<Pack>> {
            it?.let {
                adapter.updateItems(it)
                // Scrolling RecyclerView to the last "current", or "solved" pack item in the list.
                // indexOfLast returns last index, or -1 if the collection does not contain matched item.
                // Probably, I should have separated it as an independent observer, but..
                it.indexOfLast { it.state == STATE_CURRENT || it.state == STATE_SOLVED }.let {
                    it1 ->
                    rvList.smoothScrollToPosition(it1)
                }
            }
        })

        viewModel.getSelectedPackId().observe(this, Observer<String> {
            it?.let {
                // Navigates to LevelsActivity passing packId in the Intent.
                startActivity(PacksViewModel.createIntent(this, it))
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
                finish()
            }
        })
    }

    /**
     * Navigates back to MainActivity.
     */
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
    }
}