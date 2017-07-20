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
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import berlin.volders.badger.BadgeShape
import berlin.volders.badger.Badger
import berlin.volders.badger.CountBadge
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.ui.finish.FinishActivity
import com.appchamp.wordchunks.ui.game.GameActivity
import com.appchamp.wordchunks.ui.packs.PacksActivity
import com.appchamp.wordchunks.ui.store.StoreActivity
import com.appchamp.wordchunks.util.Constants
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.frag_main.*
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.share
import org.jetbrains.anko.startActivity
import xyz.hanks.library.SmallBang


class MainFragment : LifecycleFragment() {

    private val smallBang by lazy { SmallBang.attach2Window(activity) }

    private val viewModel by lazy { ViewModelProviders.of(activity).get(MainViewModel::class.java) }

    private lateinit var sharedPref: SharedPreferences

    private lateinit var progressDialog: SpotsDialog

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
        circularProgressBar.setOnClickListener { smallBang.bang(it) }
        circularProgressBar.setValue(9F)
        changeLog.setOnClickListener { showChangeLogDialog() }

        sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
        val isChangelogUpdated = sharedPref.getBoolean(getString(R.string.saved_changelog_state), true)
        if (isChangelogUpdated) {
            val circleFactory: CountBadge.Factory = CountBadge.Factory(
                    BadgeShape.circle(1f, Gravity.BOTTOM),
                    Color.parseColor("#d50000"),
                    Color.parseColor("#ffffff")
            )
            Badger.sett<CountBadge>(imgChangeLog, circleFactory).count = 1
        }
        progressDialog = SpotsDialog(context, "Downloading today's daily puzzle…", R.style.CustomProgressDialog)
        progressDialog.setCancelable(false)
    }

    private fun showChangeLogDialog() {
        // Show Warning dialog
        val builder = AlertDialog.Builder(context)
        // Add the buttons
        builder.setPositiveButton(R.string.ok, { dialog, _ ->
            // User clicked OK button
            dialog.dismiss()
        })
        builder.setTitle(R.string.changelog)
        builder.setIcon(R.drawable.ic_changelog)
        // Create the AlertDialog
        val dialog = builder.create()
        dialog.setCanceledOnTouchOutside(true)
        dialog.setMessage(resources.getString(R.string.changelog_message))
        dialog.show()
        val editor = sharedPref.edit()
        editor.putBoolean(getString(R.string.saved_changelog_state), false)
        editor.apply()
        imgChangeLog.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        grav.stop()
    }

    private fun onShareClick() {
        activity.share(getString(R.string.share_text), getString(R.string.share_subject))
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
        startActivity(activity.intentFor<StoreActivity>().clearTop())
    }

    private fun onDailyClick() {
        progressDialog.show()
        viewModel.fetchDailyLevel()
        viewModel.isRealmLoaded().observe(this, Observer {
            if (it!!) {
                progressDialog.dismiss()
                viewModel.getDailyPuzzleLevelId()?.let { startGameActivity(it) }
            }
        })
//        if (dailyLevelId != null) {
//            startGameActivity(dailyLevelId)
//        }
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