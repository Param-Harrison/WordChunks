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

package com.appchamp.wordchunks.ui.aftergame

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.annotation.ColorInt
import android.support.annotation.Nullable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.ui.finish.FinishActivity
import com.appchamp.wordchunks.ui.game.GameActivity
import com.appchamp.wordchunks.util.Constants
import kotlinx.android.synthetic.main.frag_level_solved.*
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.startActivity


class LevelSolvedFragment : LifecycleFragment() {

    private val viewModel by lazy {
        ViewModelProviders.of(activity).get(AfterGameViewModel::class.java)
    }

    @Nullable
    override fun onCreateView(inflater: LayoutInflater?, @Nullable container: ViewGroup?,
                              @Nullable savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.frag_level_solved, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rlNextLevel.setOnClickListener {
            val nextLevelId = viewModel.getNextLevelId()
            if (nextLevelId != null) {
                startGameActivity(nextLevelId)
            } else {
                startFinishActivity()
            }
        }

        setPackColor(Color.parseColor(viewModel.getPackColor()))
        setClue(viewModel.getLevelClue())
        setFunFact(viewModel.getFunFact())
        setLevelsLeft(viewModel.getLevelsLeft())
        setExcellent()
    }

    private fun startGameActivity(levelId: String) {
        startActivity(activity.intentFor<GameActivity>(
                Constants.EXTRA_LEVEL_ID to levelId).clearTop())
        activity.finish()
        activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    private fun startFinishActivity() {
        activity.startActivity<FinishActivity>()
        activity.finish()
        activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    private fun setPackColor(@ColorInt color: Int) {
        val drawable = imgRectBg.drawable as GradientDrawable
        drawable.setColor(color)
        tvNextLevelTitle.setTextColor(color)
    }

    private fun setClue(clue: String?) {
        tvNextLevelClue.text = clue ?: resources.getString(R.string.congratulations)
    }

    private fun setFunFact(fact: String?) {
        tvFunFact.text = fact ?: resources.getString(R.string.congratulations)
    }

    private fun setLevelsLeft(levelsLeft: Int) {
        when (levelsLeft) {
            0 -> tvLevelsLeft.text = getString(R.string.you_finished_the_whole_pack)
            1 -> tvLevelsLeft.text = getString(R.string.only_one_level_left)
            -1 -> tvLevelsLeft.text = getString(R.string.you_finished_all_packs)
            else -> tvLevelsLeft.text = getString(R.string.num_levels_left, levelsLeft)
        }
    }

    private fun setExcellent() {
        val congrats = resources.getStringArray(R.array.congrats)
        tvExcellent.text = congrats[viewModel.getRand()]
    }
}
