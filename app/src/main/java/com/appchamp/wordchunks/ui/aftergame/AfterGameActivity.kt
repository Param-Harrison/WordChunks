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


//class AfterGameActivity : AppCompatActivity(), LifecycleRegistryOwner {
//
//    private lateinit var levelId: String
//    private val viewModel by lazy {
//        val factory = AfterGameViewModel.Factory(application, levelId)
//        ViewModelProviders.of(this, factory).get(AfterGameViewModel::class.java)
//    }
//    private val lifecycleRegistry: LifecycleRegistry by lazy { LifecycleRegistry(this) }
//
//    override fun getLifecycle(): LifecycleRegistry = lifecycleRegistry
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.act_after_game)
//
//        subscribeUi()
//    }
//
//    override fun attachBaseContext(newBase: Context) {
//        super.attachBaseContext(LocaleChanger.configureBaseContext(newBase))
//    }
//
//    override fun onBackPressed() {
//        super.onBackPressed()
//        if (viewModel.isDailyLevel()) {
//            backToMainActivity()
//        } else {
//            backToLevelsActivity(viewModel.getPackId())
//        }
//    }
//
//    private fun subscribeUi() {
//        // Getting solved level ID by the Intent.
//        levelId = requireNotNull(intent.getStringExtra(Constants.EXTRA_LEVEL_ID),
//                { "Activity parameter 'EXTRA_LEVEL_ID' is missing" })
//
//        // Get a state of the solved level, then show corresponding fragment
//        when (viewModel.getLevelState()) {
//        // Daily level is not marked as "in progress", it is locked state
//            LevelState.LOCKED.value -> addLevelSolvedFragment()
//        // If level "in progress"/"current" was solved
//            LevelState.IN_PROGRESS.value -> addLevelSolvedFragment()
//        // If level "solved before" was solved
//            LevelState.FINISHED.value -> addLevelSolvedBeforeFragment()
//        }
//    }
//
//    private fun addLevelSolvedFragment() = supportFragmentManager
//            .beginTransaction()
//            .add(R.id.fragment_container, LevelSolvedFragment())
//            .commit()
//
//    private fun addLevelSolvedBeforeFragment() = supportFragmentManager
//            .beginTransaction()
//            .add(R.id.fragment_container, LevelSolvedBeforeFragment())
//            .commit()
//
//    /**
//     * Back navigation. Navigates to MainActivity.
//     */
//    private fun backToMainActivity() {
//        startActivity(intentFor<MainActivity>().clearTop())
//        finish()
//        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
//    }
//
//    /**
//     * Back navigation. Navigates to LevelsActivity passing Pack id by the Intent.
//     */
//    private fun backToLevelsActivity(packId: String) {
//        // Passing pack's id by the Intent.
//        startActivity(intentFor<LevelsActivity>(Constants.EXTRA_PACK_ID to packId).clearTop())
//        finish()
//        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right)
//    }
//}