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
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.appchamp.wordchunks.BuildConfig
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.realmdb.utils.RealmFactory
import com.appchamp.wordchunks.ui.game.GameActivity
import com.appchamp.wordchunks.util.Constants
import com.appchamp.wordchunks.util.Constants.SUPPORTED_LOCALES
import com.badoo.mobile.util.WeakHandler
import com.franmontiel.localechanger.LocaleChanger
import com.franmontiel.localechanger.utils.ActivityRecreationHelper
import com.google.firebase.auth.FirebaseAuth
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu
import dmax.dialog.SpotsDialog
import io.ghyeok.stickyswitch.widget.StickySwitch
import kotlinx.android.synthetic.main.act_main.*
import kotlinx.android.synthetic.main.frag_main.*
import kotlinx.android.synthetic.main.frag_sliding_menu.*
import org.jetbrains.anko.browse
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.email
import org.jetbrains.anko.intentFor
import org.jetbrains.annotations.NotNull
import java.util.*


class MainActivity : BaseMainActivity() {
    private val TAG: String = javaClass.simpleName
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var menu: SlidingMenu
    private var progressDialog: SpotsDialog? = null
    private lateinit var mHandler: WeakHandler
    private lateinit var prefs: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        // Make sure setTheme is before calling super.onCreate
        setTheme(R.style.WordChunksAppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_main)

        firebaseAuth = FirebaseAuth.getInstance()
        mHandler = WeakHandler()

        if (savedInstanceState == null) {
            addMainFragment()
        }
        setupSettingsMenu()
        setLanguageButton()
        if (!isUserAuthenticated()) {
            setupDownloadDialog()
            startAnonymousSignIn({ subscribeUiFirstRun() })
        } else {
            // User authenticated, fetch data and observe it
            viewModel.fetchDataFromFirebase()
        }
        prefs = getSharedPreferences("com.appchamp.wordchunks", MODE_PRIVATE)
    }

    override fun onStart() {
        super.onStart()
        btnSettings.setOnClickListener { onSettingsClick() }
        btnTutorial.setOnClickListener { onTutorialClick() }
        btnRate.setOnClickListener { onRateClick() }
        btnFeedback.setOnClickListener { onFeedbackClick() }
        tvVersion.text = resources.getString(R.string.version, BuildConfig.VERSION_NAME)
        setupGameProgressBar()
        setupLanguageChangeListener()
    }

    private fun setupLanguageChangeListener() {
        // Set Selected Change Listener
        stickySwitch.onSelectedChangeListener = object : StickySwitch.OnSelectedChangeListener {
            override fun onSelectedChange(@NotNull direction: StickySwitch.Direction, @NotNull text: String) {
                mHandler.postDelayed({
                    when (direction) {
                        StickySwitch.Direction.RIGHT -> changeLocaleAndRecreate(SUPPORTED_LOCALES[1])
                        StickySwitch.Direction.LEFT -> changeLocaleAndRecreate(SUPPORTED_LOCALES[0])
                    }
                }, 600)
            }
        }
    }

    private fun setLanguageButton() {
        if (Locale.getDefault() == SUPPORTED_LOCALES[1]) {
            if (stickySwitch.getDirection() == StickySwitch.Direction.LEFT) {
                stickySwitch.setDirection(StickySwitch.Direction.RIGHT)
            }
        } else {
            if (stickySwitch.getDirection() == StickySwitch.Direction.RIGHT) {
                stickySwitch.setDirection(StickySwitch.Direction.LEFT)
            }
        }
    }

    private fun isUserAuthenticated(): Boolean {
        val currentUser = firebaseAuth.currentUser
        return currentUser != null
    }

    private fun setupDownloadDialog() {
        progressDialog = SpotsDialog(this, getString(R.string.downloading_levels),
                R.style.CustomProgressDialog)
        progressDialog?.setCancelable(false)
    }

    private fun subscribeUiFirstRun() {
        viewModel.fetchDataFromFirebase()
        viewModel.isRealmLoaded().observe(this, android.arch.lifecycle.Observer {
            if (it == true) {
                progressDialog?.dismiss()
            }
        })
    }

    private fun onTutorialClick() {
        prefs.edit().putBoolean("TUTORIAL", true).apply()
        val levelId = viewModel.getLevelId()
        if (levelId != "") {
            startGameActivity(levelId)
        }
    }

    private fun startGameActivity(levelId: String) {
        startActivity(intentFor<GameActivity>(
                Constants.EXTRA_LEVEL_ID to levelId).clearTop())
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
    }

    private fun setupGameProgressBar() {
        val value = viewModel.getCircularProgressValue()
        circularProgressBar.invalidate()
        circularProgressBar.setValue(value)
        circularProgressBar.invalidate()

        if (value.isNaN()) {
            tvPercentComplete.text = resources.getString(R.string.percent_complete, 0F)
        } else {
            tvPercentComplete.text = resources.getString(R.string.percent_complete, value)
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleChanger.configureBaseContext(newBase))
    }

    override fun onResume() {
        super.onResume()
        ActivityRecreationHelper.onResume(this)
    }

    override fun onDestroy() {
        ActivityRecreationHelper.onDestroy(this)
        super.onDestroy()
        gravMain.stop()
    }

    override fun onBackPressed() = when {
        menu.isMenuShowing -> menu.toggle() // Collapse the sliding menu on back pressed
        else -> super.onBackPressed()
    }

    private fun startAnonymousSignIn(success: () -> Unit) {
        progressDialog?.show()
        firebaseAuth.signInAnonymously().addOnCompleteListener(this) {
            when {
                it.isSuccessful -> success()
                else -> {
                    // If sign in fails, display a message to the user.
                    it.exception?.message?.let {
                        showSnackbar("Authentication failed. " + it)
                    }
                    Log.w(TAG, "signInAnonymously:failure", it.exception)
                }
            }
        }
    }

    private fun changeLocaleAndRecreate(locale: Locale) {
        LocaleChanger.setLocale(locale)
        reconfigureRealm(locale.displayLanguage)
        ActivityRecreationHelper.recreate(this, true)
    }

    private fun reconfigureRealm(dbName: String) = RealmFactory().setRealmConfiguration(dbName)

    private fun addMainFragment() {
        supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, MainFragment())
                .commit()
    }

    private fun onSettingsClick() = menu.toggle()

    private fun setupSettingsMenu() {
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

    private fun onFeedbackClick() = email(getString(R.string.feedback_email),
            getString(R.string.feedback_subject),
            getString(R.string.feedback_text, BuildConfig.APPLICATION_ID,
                    BuildConfig.VERSION_CODE, Build.MODEL, Build.VERSION.RELEASE))

    private fun onRateClick() = browse("market://details?id=$packageName")

    private fun showSnackbar(msg: String) = Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}