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

import android.content.Context
import android.os.Build
import android.os.Bundle
import com.appchamp.wordchunks.BuildConfig
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.realmdb.models.pojo.packsFromJSONFile
import com.appchamp.wordchunks.ui.BaseActivity
import com.appchamp.wordchunks.ui.tutorial.TutorialActivity
import com.appchamp.wordchunks.util.ActivityUtils
import com.appchamp.wordchunks.util.Constants.FILE_NAME_DATA_JSON
import com.appchamp.wordchunks.util.Constants.PREFS_IS_DB_EXISTS
import com.appchamp.wordchunks.util.Constants.WORD_CHUNKS_PREFS
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu
import kotlinx.android.synthetic.main.frag_main.*
import kotlinx.android.synthetic.main.frag_sliding_menu.*
import org.jetbrains.anko.act
import org.jetbrains.anko.browse
import org.jetbrains.anko.email
import org.jetbrains.anko.startActivity


class MainActivity : BaseActivity<MainViewModel>() {

    private lateinit var menu: SlidingMenu

    override val viewModelClass = MainViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_main)

        val sp = getSharedPreferences(WORD_CHUNKS_PREFS, Context.MODE_PRIVATE)
        val isRealmExists = sp.getBoolean(PREFS_IS_DB_EXISTS, true)  // false for debugging

        if (isRealmExists) { // always false while debugging
            // update an existing realm objects here
            // updateRealmDb();


        } else {
            // Creates realm objects from json file if this is first launch
            viewModel.initGame(packsFromJSONFile(act, FILE_NAME_DATA_JSON))

        }
        if (savedInstanceState == null) {
            addMainFragment()
        }
        initLeftMenu()
    }

    override fun onStart() {
        super.onStart()

        imgSettingsIcon.setOnClickListener { onSettingsClick() }

        // Sliding menu listeners
        tvHowToPlay.setOnClickListener { showTutorial() }
        tvRateUs.setOnClickListener { browse("market://details?id=$packageName") }
        tvFeedback.setOnClickListener {
            email(
                    "jkozhukhovskaya@gmail.com", // todo
                    "Feedback for WordChunks",
                    """
                        -----------------
                        ID: ${BuildConfig.APPLICATION_ID}
                        App version: ${BuildConfig.VERSION_CODE}
                        Device: ${Build.MODEL}
                        System version: ${Build.VERSION.RELEASE}
                        -----------------

                        """
            )
        }
    }

    private fun addMainFragment() {
        ActivityUtils.addFragment(
                supportFragmentManager,
                MainFragment(),
                R.id.fragment_container)
    }

    override fun onBackPressed() {
        when {
            menu.isMenuShowing -> menu.toggle()  // Collapse the sliding menu
            else -> super.onBackPressed()
        }
    }

    private fun showTutorial() = startActivity<TutorialActivity>()

    private fun onSettingsClick() = menu.toggle()

    private fun initLeftMenu() {
        menu = SlidingMenu(this)
        // Configure the SlidingMenu
        menu.mode = SlidingMenu.LEFT
        menu.touchModeAbove = SlidingMenu.TOUCHMODE_FULLSCREEN
        menu.setShadowWidthRes(R.dimen.shadow_width)
        menu.setShadowDrawable(R.drawable.shadow)
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset)
        menu.setFadeDegree(0.35f)
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT)
        menu.setMenu(R.layout.frag_sliding_menu)
    }
}