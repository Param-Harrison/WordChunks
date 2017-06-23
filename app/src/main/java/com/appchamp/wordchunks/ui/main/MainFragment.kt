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

package com.appchamp.wordchunks.ui.main

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.ui.finish.FinishActivity
import com.appchamp.wordchunks.ui.game.GameActivity
import com.appchamp.wordchunks.ui.packs.PacksActivity
import com.appchamp.wordchunks.util.Constants
import kotlinx.android.synthetic.main.frag_main.*
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.share
import org.jetbrains.anko.startActivity


class MainFragment : LifecycleFragment() {

    private val viewModel by lazy {
        ViewModelProviders.of(activity).get(MainViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.frag_main, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgShareIcon.setOnClickListener { onShareClick() }
        btnPlay.setOnClickListener { onPlayClick() }
        btnDaily.setOnClickListener { onDailyClick() }
        btnPacks.setOnClickListener { startPacksActivity() }
        btnStore.setOnClickListener { onStoreClick() }
    }

    private fun onShareClick() {
        activity.share(
                // todo localize
                "WordChunks is AWESOME and I think you'll love it. Get it! -- [link]",
                "My current puzzle")
    }

    private fun onPlayClick() {
        val levelId = viewModel.getLevelId()
        if (levelId != null) {
            startGameActivity(levelId)
        } else {
            // All levels and packs were solved
            startFinishActivity()
        }
    }

    private fun onStoreClick() {
        Toast.makeText(context, "COMING SOON", Toast.LENGTH_SHORT).show()
    }

    private fun onDailyClick() {
        Toast.makeText(context, "COMING SOON", Toast.LENGTH_SHORT).show()
    }

    private fun startGameActivity(levelId: String) {
        startActivity(activity.intentFor<GameActivity>(
                Constants.EXTRA_LEVEL_ID to levelId).clearTop())
        activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    private fun startPacksActivity() {
        startActivity(activity.intentFor<PacksActivity>().clearTop())
        activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    private fun startFinishActivity() {
        activity.startActivity<FinishActivity>()
        activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

}
