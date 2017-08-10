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

package com.appchamp.wordchunks.ui.levels

import android.arch.lifecycle.Observer
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.models.realm.Level
import com.appchamp.wordchunks.ui.BaseActivity
import com.appchamp.wordchunks.ui.game.GameActivity
import com.appchamp.wordchunks.ui.packs.PacksActivity
import com.appchamp.wordchunks.ui.packs.PacksLevelsAdapter
import com.appchamp.wordchunks.util.Constants.EXTRA_LEVEL_ID
import com.appchamp.wordchunks.util.Constants.EXTRA_PACK_ID
import com.franmontiel.localechanger.LocaleChanger
import kotlinx.android.synthetic.main.act_packs_levels.*
import kotlinx.android.synthetic.main.titlebar.*
import me.everything.android.ui.overscroll.OverScrollDecoratorHelper
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor


class LevelsActivity : BaseActivity<LevelsViewModel>() {

    override val viewModelClass = LevelsViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_packs_levels)

//        llPacksLevels.setBackgroundResource(R.drawable.gradient_levels)
        tvTitle.text = getString(R.string.title_select_level)
        rvList.layoutManager = LinearLayoutManager(this)
        rvList.setHasFixedSize(true)

        // Sets smooth scroll effect.
        OverScrollDecoratorHelper.setUpOverScroll(
                rvList, OverScrollDecoratorHelper.ORIENTATION_VERTICAL)

        btnBack.setOnClickListener { onBackPressed() }

        subscribeUi()
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleChanger.configureBaseContext(newBase))
    }

    /**
     * Navigates back to PacksActivity.
     */
    override fun onBackPressed() {
        super.onBackPressed()
        backToPacksActivity()
    }

    private fun subscribeUi() {
        // Getting pack id by the Intent.
        val packId = requireNotNull(intent.getStringExtra(EXTRA_PACK_ID),
                { "Activity parameter 'EXTRA_PACK_ID' is missing" })
        // Observe updates to the LiveData levels.
        viewModel
                .getLiveLevels(packId)
                .observe(this, Observer {
                    // update UI
                    val adapter = PacksLevelsAdapter<Level> {
                        // Navigates up to GameActivity passing levelId in the Intent.
                        startGameActivity(it.id)
                    }
                    it?.toList()?.let { levels -> adapter.updateItems(levels) }
                    rvList.adapter = adapter
                })

        val packColor = viewModel.getPackColor() ?: "#7bda7a"
        rlTitlebar.setBackgroundColor(Color.parseColor(packColor))  // get pack color
        // Scrolling RecyclerView to the last "current", or "solved" level item.
        // indexOfLast gets last index, or -1 if the list does not contain that item.
        rvList.smoothScrollToPosition(viewModel.getLastLevelPos())
    }

    private fun startGameActivity(levelId: String) {
        startActivity(intentFor<GameActivity>(EXTRA_LEVEL_ID to levelId).clearTop())
        finish()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
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