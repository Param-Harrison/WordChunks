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
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.models.realm.Level
import com.appchamp.wordchunks.ui.customviews.LevelSolvedDialog
import com.appchamp.wordchunks.ui.levels.LevelsActivity
import com.appchamp.wordchunks.util.ActivityUtils
import com.appchamp.wordchunks.util.Constants.EXTRA_LEVEL_ID
import com.appchamp.wordchunks.util.Constants.EXTRA_PACK_ID
import kotlinx.android.synthetic.main.frag_game.*
import kotlinx.android.synthetic.main.titlebar.*
import me.toptas.fancyshowcase.FancyShowCaseQueue
import me.toptas.fancyshowcase.FancyShowCaseView
import me.toptas.fancyshowcase.FocusShape
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor


class GameActivity : BaseGameActivity(), LevelSolvedDialog.LevelSolvedDialogListener {

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_game)
        if (savedInstanceState == null) {
            ActivityUtils.addFragment(supportFragmentManager, GameFragment(),
                    R.id.fragment_container)
        }
        subscribeUi()
        prefs = getSharedPreferences("com.appchamp.wordchunks", MODE_PRIVATE)
//        showLevelSolvedDialog()
    }

    override fun onStart() {
        super.onStart()
        // Setup click listeners
        btnBack.setOnClickListener { onBackPressed() }

        if (prefs.getBoolean("TUTORIAL", true)) {
            prefs.edit().putBoolean("TUTORIAL", false).apply()
            showIntroTutorial()
        }
    }

    private fun showIntroTutorial() {
        // Show Tutorial
        val fancyShowCaseView1 = FancyShowCaseView.Builder(this)
                .title("In WordChunks, your challenge is to reconstruct hidden words using a grid of chunks.")
                .fitSystemWindows(true)
                .build()

        val fancyShowCaseView2 = FancyShowCaseView.Builder(this)
                .focusOn(tvTitle)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .title("Each level has a specific theme that all words are associated with!")
                .fitSystemWindows(true)
                .build()

        val fancyShowCaseView3 = FancyShowCaseView.Builder(this)
                .focusOn(rvWords)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .title("Each word has the length and optionally the first letter.\nIn order to solve the puzzle, you need to find specific hidden words for each level.")
                .titleStyle(0, Gravity.BOTTOM or Gravity.CENTER)
                .fitSystemWindows(true)
                .build()

        val fancyShowCaseView4 = FancyShowCaseView.Builder(this)
                .focusOn(rvChunks)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .title("You have to use all these chunks.\n\nCorrect answers accepted automatically.\n\nThe words order doesn't matter.")
                .titleStyle(0, Gravity.CENTER or Gravity.TOP)
                .fitSystemWindows(true)
                .build()

        val fancyShowCaseView5 = FancyShowCaseView.Builder(this)
                .focusOn(btnHint)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .title("The game will get trickier.\n\nIf you get stuck, you can use hints!")
                .titleStyle(0, Gravity.CENTER or Gravity.TOP)
                .fitSystemWindows(true)
                .build()

        val fancyShowCaseView6 = FancyShowCaseView.Builder(this)
                .focusOn(btnShuffle)
                .focusShape(FocusShape.ROUNDED_RECTANGLE)
                .title("Or use shuffle!\n\nGood luck!")
                .fitSystemWindows(true)
                .build()

        FancyShowCaseQueue()
                .add(fancyShowCaseView1)
                .add(fancyShowCaseView2)
                .add(fancyShowCaseView3)
                .add(fancyShowCaseView4)
                .add(fancyShowCaseView5)
                .add(fancyShowCaseView6)
                .show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        // If it's not a daily puzzle, back to the levels screen
        if (!viewModel.isDailyLevel()) {
            backToLevelsActivity()
        }
    }

    // This method is invoked in the activity when the listener is triggered
    // Access the data result passed to the activity here
    override fun onNextBtnClickedDialog() {
        Toast.makeText(this, "Hi, ", Toast.LENGTH_SHORT).show()
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
        val dialog = LevelSolvedDialog.newInstance()
        dialog.show(supportFragmentManager, "fragment_level_solved")
        if (viewModel.isDailyLevel()) {
            // add + 3 HINTS
        } else {
            // add + 2 HINTS
        }
        // pass levels left
        // color
        // number of hints added
        // excellent words
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