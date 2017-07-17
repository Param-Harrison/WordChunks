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
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.widget.Toast
import com.appchamp.wordchunks.BuildConfig
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.realmdb.utils.RealmFactory
import com.appchamp.wordchunks.ui.tutorial.TutorialActivity
import com.appchamp.wordchunks.util.ActivityUtils
import com.appchamp.wordchunks.util.Constants.SUPPORTED_LOCALES
import com.franmontiel.localechanger.LocaleChanger
import com.franmontiel.localechanger.utils.ActivityRecreationHelper
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu
import dmax.dialog.SpotsDialog
import kotlinx.android.synthetic.main.frag_main.*
import kotlinx.android.synthetic.main.frag_sliding_menu.*
import org.jetbrains.anko.browse
import org.jetbrains.anko.email
import org.jetbrains.anko.startActivity
import java.util.*


class MainActivity : BaseMainActivity() {

    private val TAG: String = javaClass.simpleName
    private val RC_SIGN_IN = 9001
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var menu: SlidingMenu
    private lateinit var progressDialog: SpotsDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        // Make sure this is before calling super.onCreate
        setTheme(R.style.WordChunksAppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.act_main)

        firebaseAuth = FirebaseAuth.getInstance()

        if (savedInstanceState == null) {
            addMainFragment()
        }
        initLeftMenu()
        progressDialog = SpotsDialog(this, "Downloading levels…", R.style.CustomProgressDialog)
        progressDialog.setCancelable(false)

        val currentUser = firebaseAuth.currentUser
        if (currentUser == null) {
            progressDialog.show()
            Log.d(TAG, "SIGN IN GUEST STARTED")
            signInGuest({ subscribeUi() })
        } else {
            if (currentUser.isAnonymous) {

            } else {
                viewModel.fetchFirebaseUser(currentUser.uid)
            }
        }

    }

    private fun subscribeUi() {
        viewModel.isRealmLoaded().observe(this, android.arch.lifecycle.Observer {
            if (it == true) {
                progressDialog.dismiss()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        imgSettingsIcon.setOnClickListener { onSettingsClick() }
        imgHowToPlay.setOnClickListener { showTutorial() }
        imgRateUs.setOnClickListener { onRateUsClick() }
        imgFeedback.setOnClickListener { onFeedbackClick() }
        radioButtonEn.setOnClickListener { onLangChangeClick(it) }
        radioButtonRu.setOnClickListener { onLangChangeClick(it) }
        tvSignOut.setOnClickListener { signOut() }
        imgGoogleSettings.setOnClickListener { onGoogleClick() }
        imgFacebookSettings.setOnClickListener { showSnackbar(getString(R.string.facebook_coming)) }
        imgTwitterSettings.setOnClickListener { showSnackbar(getString(R.string.twitter_coming)) }
        changeLog.setOnClickListener {
            //viewModel.changeData()
        }
        tvVersion.text = resources.getString(R.string.version, BuildConfig.VERSION_NAME)
        updateAuthInfo()
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if (result.isSuccess) {
                // Google Sign In was successful, authenticate with Firebase
                val account = result.signInAccount
                if (account != null) {
                    firebaseAuthLinkWithGoogle(account)
                }
            } else {
                // Google Sign In failed, update UI appropriately
                showSnackbar("Google Sign In failed")
            }
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleChanger.configureBaseContext(newBase))
    }

    override fun onResume() {
        super.onResume()
        ActivityRecreationHelper.onResume(this)

        when (Locale.getDefault()) {
            SUPPORTED_LOCALES[1] -> radioButtonRu.isChecked = true
            else -> radioButtonEn.isChecked = true
        }
    }

    override fun onDestroy() {
        ActivityRecreationHelper.onDestroy(this)
        super.onDestroy()
    }

    override fun onBackPressed() = when {
        menu.isMenuShowing -> menu.toggle() // Collapse the sliding menu on back pressed
        else -> super.onBackPressed()
    }

    private fun signInGuest(call: () -> Unit) {
        firebaseAuth.signInAnonymously().addOnCompleteListener(this) {
            when {
                it.isSuccessful -> {
                    // Sign in success, update UI with the signed-in user's information
                    val user = firebaseAuth.currentUser
                    Log.w(TAG, "user=" + user?.uid)
                    updateAuthInfo()
                    call()
                }
                else -> {
                    // If sign in fails, display a message to the user.
                    it.exception?.message?.let {
                        showSnackbar("Guest authentication failed. " + it)
                    }
                    Log.w(TAG, "signInAnonymously:failure", it.exception)
                }
            }
        }
    }

    private fun firebaseAuthLinkWithGoogle(acct: GoogleSignInAccount) {
        imgGoogleSettings.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.currentUser?.linkWithCredential(credential)?.addOnCompleteListener(this) {
            when {
                it.isSuccessful -> {
                    val user = it.result.user
                    Log.d(TAG, "USER = " + user.toString())
                    updateAuthInfo()
                    viewModel.writeNewUser(user)
                    imgGoogleSettings.visibility = View.GONE
                }
                else -> {

                    Log.d(TAG, "AUTH FAILED = " + it.exception)
                    /*
                    The call to linkWithCredential will fail if the credentials are already linked
                    to another user account. In this situation, you must handle merging the accounts
                    and associated data as appropriate for your app:
                     */
                    firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
                        when {
                            it.isSuccessful -> {
                                val user = it.result.user
                                showSnackbar("Authenticated.")
                                Log.d(TAG, "USER = " + user?.uid)
                                updateAuthInfo()
                                viewModel.fetchFirebaseUser(user.uid)
                            }
                            else -> {
                                showSnackbar("Authentication Failed. " + it.exception?.message)
                                Log.d(TAG, "AUTH FAILED = " + it.exception)
                            }
                        }
                    }
                    imgGoogleSettings.visibility = View.VISIBLE
                    //                val prevUser = firebaseAuth.currentUser
                    //                firebaseAuth.use = firebaseAuth.signInWithCredential(credential).await().getUser()
                    // Merge prevUser and currentUser accounts and data
                }
            }
            progressBar.visibility = View.GONE
        }
    }

    private fun onGoogleClick() {
        val signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient)
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun updateAuthInfo() {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            if (currentUser.isAnonymous) {
                tvProvider.text = getString(R.string.guest)
                btns_group_social.visibility = View.VISIBLE
            } else {
                btns_group_social.visibility = View.GONE
                currentUser.providerData.map {
                    // Id of the provider (ex: google.com)
                    when {
                        it.providerId == "google.com" -> tvProvider.text = getString(R.string.google)
                    }
                }
            }
            tvUserId.text = userId.take(12)
            tvUserId.append(getString(R.string.dots))
        }
    }

    private fun signOut() {
        // Firebase sign out
        firebaseAuth.signOut()
        // Google sign out
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback { }
        updateAuthInfo()
    }

    // Rethink!
    private fun onLangChangeClick(btnLang: View) {
        when (btnLang) {
            radioButtonEn -> if (LocaleChanger.getLocale() == SUPPORTED_LOCALES[1]) {
                showLangChangeDialog(SUPPORTED_LOCALES[0])
            }
            radioButtonRu -> if (LocaleChanger.getLocale() == SUPPORTED_LOCALES[0]) {
                showLangChangeDialog(SUPPORTED_LOCALES[1])
            }
        }
    }

    private fun showLangChangeDialog(locale: Locale) {
        val currentUser = firebaseAuth.currentUser
        if (currentUser!!.isAnonymous) {

            // Show Warning dialog
            val builder = AlertDialog.Builder(this)
            // Add the buttons
            builder.setPositiveButton(R.string.change, { _, _ ->
                // User clicked OK button
                changeLocaleAndRecreate(locale)
            })
            builder.setNegativeButton(R.string.cancel, { _, _ ->
                // User cancelled the dialog
                when (locale) {
                    SUPPORTED_LOCALES[1] -> radioButtonEn.isChecked = true
                    SUPPORTED_LOCALES[0] -> radioButtonRu.isChecked = true
                }
            })
            builder.setTitle(R.string.warning)
            // Create the AlertDialog
            val dialog = builder.create()
            dialog.setCanceledOnTouchOutside(false)
            dialog.setMessage(getString(R.string.dialog_message_lang_change))
            dialog.show()
        } else {
            changeLocaleAndRecreate(locale)
        }
    }

    private fun changeLocaleAndRecreate(locale: Locale) {
        LocaleChanger.setLocale(locale)
        configureRealmOnLangChanged(locale.displayLanguage)
        ActivityRecreationHelper.recreate(this, true)
    }


    // Rethink that!
    private fun configureRealmOnLangChanged(dbName: String) {
        RealmFactory().setRealmConfiguration(dbName)
    }

    private fun addMainFragment() {
        ActivityUtils.addFragment(
                supportFragmentManager,
                MainFragment(),
                R.id.fragment_container)
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

    private fun onFeedbackClick() {
//        Instabug.invoke(InstabugInvocationMode.NEW_FEEDBACK)
        email(
                "jkozhukhovskaya@gmail.com", // todo
                "Feedback for WordChunks",
                """
                        -----------------
                        ID: ${BuildConfig.APPLICATION_ID}
                        DebugApp version: ${BuildConfig.VERSION_CODE}
                        Device: ${Build.MODEL}
                        System version: ${Build.VERSION.RELEASE}
                        -----------------

                        """
        )
    }

    private fun onRateUsClick() {
        browse("market://details?id=$packageName")
    }

    private fun showSnackbar(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}
