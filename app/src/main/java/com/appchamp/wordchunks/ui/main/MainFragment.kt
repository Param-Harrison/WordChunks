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

//import dmax.dialog.SpotsDialog
import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.ui.customviews.StoreDialog
import com.appchamp.wordchunks.ui.game.GameActivity
import com.appchamp.wordchunks.ui.packs.PacksActivity
import com.appchamp.wordchunks.util.Constants
import kotlinx.android.synthetic.main.custom_button_subtitle.view.*
import kotlinx.android.synthetic.main.frag_main.*
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.share
import xyz.hanks.library.SmallBang


class MainFragment : LifecycleFragment() {
    private val TAG: String = javaClass.simpleName
    private val smallBang by lazy { SmallBang.attach2Window(activity) }
    private val viewModel by lazy { ViewModelProviders.of(activity).get(MainViewModel::class.java) }
//    private lateinit var progressDialog: SpotsDialog

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.frag_main, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        btnShare.setOnClickListener { onShareClick() }
        btnPlay.setOnClickListener { onPlayClick() }
        btnDaily.setOnClickListener { onDailyClick() }
        btnPacks.setOnClickListener { startPacksActivity() }
        btnStore.setOnClickListener { onStoreClick() }
        blur.setOnClickListener { smallBang.bang(it) }
        btnPlay.tvSubtitle.text = viewModel.getLevelTitle()
    }

    private fun onShareClick() {
        activity.share(getString(R.string.share_text), getString(R.string.share_subject))
    }

    private fun onPlayClick() {
        val levelId = viewModel.getLevelId()
        if (levelId != "") {
            startGameActivity(levelId)
        } else {
            // All levels and packs were solved, show dialog
        }
    }

    private fun onStoreClick() {
        // show dialog
        val dialog = StoreDialog.newInstance()
        dialog.show(activity.supportFragmentManager, "fragment_store")
    }

    private fun onDailyClick() {
//        progressDialog.show()
        viewModel.fetchDailyLevel()
        viewModel.isRealmLoaded().observe(this, Observer {
            if (it!!) {
//                progressDialog.dismiss()
                viewModel.getDailyPuzzleLevelId()?.let { startGameActivity(it) }
            }
        })
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
}