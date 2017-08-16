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
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Toast
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.models.realm.FINISHED
import com.appchamp.wordchunks.models.realm.IN_PROGRESS
import com.appchamp.wordchunks.models.realm.Level
import com.appchamp.wordchunks.ui.customviews.LevelSolvedDialog
import com.appchamp.wordchunks.ui.customviews.StoreDialog
import com.appchamp.wordchunks.ui.levels.LevelsActivity
import com.appchamp.wordchunks.util.ActivityUtils
import com.appchamp.wordchunks.util.Constants.EXTRA_LEVEL_ID
import com.appchamp.wordchunks.util.Constants.EXTRA_PACK_ID
import com.appchamp.wordchunks.util.Constants.PREFS_NAME
import com.appchamp.wordchunks.util.Constants.PREFS_TUTORIAL
import com.appchamp.wordchunks.util.Constants.USER_DAILY_LEVEL_SOLVED_REWARD
import com.appchamp.wordchunks.util.Constants.USER_LEVEL_SOLVED_REWARD
import kotlinx.android.synthetic.main.frag_game.*
import kotlinx.android.synthetic.main.titlebar.*
import me.toptas.fancyshowcase.FancyShowCaseQueue
import me.toptas.fancyshowcase.FancyShowCaseView
import me.toptas.fancyshowcase.FocusShape
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor


class GameActivity : BaseGameActivity(), LevelSolvedDialog.LevelSolvedDialogListener,
        StoreDialog.StoreDialogListener {

    private val TAG: String = javaClass.simpleName
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_game)
        if (savedInstanceState == null) {
            ActivityUtils.addFragment(supportFragmentManager, GameFragment(),
                    R.id.fragment_container)
        }
        subscribeUi()
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
    }

    override fun onStart() {
        super.onStart()
        // Setup click listeners
        btnBack.setOnClickListener { onBackPressed() }

        if (prefs.getBoolean(PREFS_TUTORIAL, true)) {
            prefs.edit().putBoolean(PREFS_TUTORIAL, false).apply()
            showIntroTutorial()
        }
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
        viewModel.makeLevelSolved()
        if (viewModel.isDailyLevel()) {
            onBackPressed()
        } else {
            // find next level to play
            //Toast.makeText(this, "LOOKING FOR THE NEXT LEVEL", Toast.LENGTH_SHORT).show()
            val nextLevel = viewModel.getNextLevel()
            if (nextLevel != null) {
                startGameActivity(nextLevel.id)
            } else {
                Toast.makeText(
                        this,
                        "Congratulations! You have solved all of the levels. New levels are coming!",
                        Toast.LENGTH_LONG).show()
                onBackPressed()
            }
        }
    }

    override fun onRewardUser() {
        viewModel.increaseHints(1)
        tvHintsCount.text = viewModel.getUser().value?.hints.toString()
    }

    private fun startGameActivity(nextLevelId: String) {
        startActivity(intentFor<GameActivity>(EXTRA_LEVEL_ID to nextLevelId).clearTop())
        finish()
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
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
                    viewModel.makeLevelSolved()
                    // restore level
                }
            }
        })
        viewModel.getUser().observe(this, Observer {
            tvHintsCount.text = it?.hints.toString()
        })
    }

    private fun showLevelSolvedDialog() {
        val dialog = LevelSolvedDialog.newInstance(
                Color.parseColor(viewModel.getNextLevelColor()),
                viewModel.isDailyLevel(),
                viewModel.getLiveLevel().value?.state == FINISHED)
        dialog.show(supportFragmentManager, "fragment_level_solved")
        if (viewModel.getLiveLevel().value?.state == IN_PROGRESS) {
            if (viewModel.isDailyLevel()) {
                // adds + 3 HINTS
                viewModel.increaseHints(USER_DAILY_LEVEL_SOLVED_REWARD)
            } else {
                // adds + 2 HINTS
                viewModel.increaseHints(USER_LEVEL_SOLVED_REWARD)
            }
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
                .title("You have to use all these chunks.\nCorrect answers accepted automatically.\nThe words order doesn't matter.")
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
}