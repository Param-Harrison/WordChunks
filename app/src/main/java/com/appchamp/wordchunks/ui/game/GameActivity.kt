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

package com.appchamp.wordchunks.ui.game

import android.arch.lifecycle.Observer
import android.os.Bundle
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.models.realm.Level
import com.appchamp.wordchunks.ui.customviews.RoundedDialog
import com.appchamp.wordchunks.ui.levels.LevelsActivity
import com.appchamp.wordchunks.util.ActivityUtils
import com.appchamp.wordchunks.util.Constants.EXTRA_LEVEL_ID
import com.appchamp.wordchunks.util.Constants.EXTRA_PACK_ID
import kotlinx.android.synthetic.main.titlebar.*
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor


/**
 * GameViewModel shares the level ID between the GameActivity and GameFragment.
 */
class GameActivity : BaseGameActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_game)
        if (savedInstanceState == null) {
            ActivityUtils.addFragment(supportFragmentManager, GameFragment(),
                    R.id.fragment_container)
        }
        subscribeUi()
        showLevelSolvedDialog()
    }

    private fun subscribeUi() {
        // Getting level id through the Intent.
        levelId = requireNotNull(intent.getStringExtra(EXTRA_LEVEL_ID),
                { "Activity parameter 'EXTRA_LEVEL_ID' is missing" })

        // Observe updates to the LiveData level.
        viewModel.getLiveLevel().observe(this, Observer<Level> {
            // update UI titlebar with clue
            it?.let { tvTitle.text = it.clue }
        })
        viewModel.getLiveWords().observe(this, Observer {
            it?.let {
                if (viewModel.isLevelSolved()) {
                    showLevelSolvedDialog()
                }
            }
        })
    }

    private fun showLevelSolvedDialog() {
        val dialog = RoundedDialog.newInstance()
        dialog.show(supportFragmentManager, "fragment_level_solved")
    }

    override fun onStart() {
        super.onStart()
        // Setup click listeners
        btnBack.setOnClickListener { onBackPressed() }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        // If it's not a daily puzzle, back to the levels screen
        if (!viewModel.isDailyLevel()) {
            backToLevelsActivity()
        }
    }

    /**
     * Back navigation. Navigates from GameActivity to LevelsActivity passing Pack id in the Intent.
     */
    private fun backToLevelsActivity() {
        // Passing pack's id through the Intent.
        viewModel.getPackId()?.let {
            startActivity(intentFor<LevelsActivity>(EXTRA_PACK_ID to it).clearTop())
            finish()
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
        }
    }
}