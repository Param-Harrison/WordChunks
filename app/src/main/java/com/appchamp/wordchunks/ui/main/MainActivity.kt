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
import android.util.Log
import com.appchamp.wordchunks.BuildConfig
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.realmdb.models.pojo.packsFromJSONFile
import com.appchamp.wordchunks.ui.BaseActivity
import com.appchamp.wordchunks.ui.tutorial.TutorialActivity
import com.appchamp.wordchunks.util.ActivityUtils
import com.appchamp.wordchunks.util.Constants
import com.appchamp.wordchunks.util.Constants.SUPPORTED_LOCALES
import com.franmontiel.localechanger.LocaleChanger
import com.franmontiel.localechanger.utils.ActivityRecreationHelper
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu
import io.realm.Realm
import kotlinx.android.synthetic.main.frag_main.*
import kotlinx.android.synthetic.main.frag_sliding_menu.*
import org.jetbrains.anko.act
import org.jetbrains.anko.browse
import org.jetbrains.anko.email
import org.jetbrains.anko.startActivity
import java.io.File
import java.util.*


class MainActivity : BaseActivity<MainViewModel>() {

    private lateinit var menu: SlidingMenu

    override val viewModelClass = MainViewModel::class.java

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_main)

//        if (isRealmExists) { // always false while debugging
//            // update an existing realm objects here
//            // updateRealmDb();
//        } else {
        // Creates realm objects from json file if this is first launch
//        }
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
        tvVersion.text = resources.getString(R.string.version, BuildConfig.VERSION_NAME)

        radioButtonEn.setOnClickListener {
            if (LocaleChanger.getLocale() != SUPPORTED_LOCALES[0]) {
                LocaleChanger.setLocale(SUPPORTED_LOCALES[0])
                ActivityRecreationHelper.recreate(this, true)
            }
        }
        radioButtonRu.setOnClickListener {
            if (LocaleChanger.getLocale() != SUPPORTED_LOCALES[1]) {
                LocaleChanger.setLocale(SUPPORTED_LOCALES[1])
                ActivityRecreationHelper.recreate(this, true)
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleChanger.configureBaseContext(newBase))
    }

    override fun onResume() {
        super.onResume()
        ActivityRecreationHelper.onResume(this)
        Log.d("SSSSSSSSSSS", ""+File(Realm.getDefaultInstance().configuration.path).exists())
        Log.d("SSSSSSSSSSS", ""+File(Realm.getDefaultInstance().configuration.path))
        Log.d("SSSSSSSSSSS", ""+Realm.getDefaultInstance().configuration.path)
        if (!File(Realm.getDefaultInstance().configuration.path).exists()) {
            if (Locale.getDefault() == SUPPORTED_LOCALES[1]) {
                viewModel.initGame(packsFromJSONFile(act, Constants.FILE_NAME_DATA_RU_JSON))
            } else {
                viewModel.initGame(packsFromJSONFile(act, Constants.FILE_NAME_DATA_JSON))
            }
            // Initialize game state for the first time in the beginning.
            viewModel.initFirstGameState()
        }

        if (Locale.getDefault() == SUPPORTED_LOCALES[1]) {
            radioButtonRu.isChecked = true
        } else {
            radioButtonEn.isChecked = true
        }
    }

    override fun onDestroy() {
        ActivityRecreationHelper.onDestroy(this)
        super.onDestroy()
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