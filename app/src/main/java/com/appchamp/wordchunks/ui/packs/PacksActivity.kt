/*
 * Copyright 2017 Julia Kozhukhovskaya
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appchamp.wordchunks.ui.packs

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.realmdb.models.realm.Pack
import com.appchamp.wordchunks.ui.BaseActivity
import com.appchamp.wordchunks.ui.levels.LevelsActivity
import com.appchamp.wordchunks.util.Constants.EXTRA_PACK_ID
import com.franmontiel.localechanger.LocaleChanger
import io.realm.RealmResults
import kotlinx.android.synthetic.main.act_packs_levels.*
import kotlinx.android.synthetic.main.titlebar.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor


class PacksActivity : BaseActivity<PacksViewModel>() {

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

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleChanger.configureBaseContext(newBase))
    }

    private fun subscribeToModel() {
        // Observe updates to our LiveData packs.
        viewModel
                .getLivePacks()
                .observe(this, Observer<RealmResults<Pack>> {
                    // update UI
                    val adapter = PacksLevelsAdapter<Pack> {
                        // Navigates to the LevelsActivity passing pack id in the Intent.
                        startLevelsActivity(it.id)
                    }

                    it?.toList()?.let { packs -> adapter.updateItems(packs) }
                    rvList.adapter = adapter
                })

        // Scroll RecyclerView to the last "current", or "solved" level item.
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
        finish()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }
}