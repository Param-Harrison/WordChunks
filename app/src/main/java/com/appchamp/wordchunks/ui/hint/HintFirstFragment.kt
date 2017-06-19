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

package com.appchamp.wordchunks.ui.hint

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.ui.game.CustomGridLayoutManager
import kotlinx.android.synthetic.main.frag_hint_first.*


class HintFirstFragment : LifecycleFragment() {

    private lateinit var wordsHintAdapter: WordsHintAdapter

    private val viewModel by lazy {
        ViewModelProviders.of(activity).get(HintViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.frag_hint_first, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupWordsHintAdapter()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getLevel().observe(this, Observer {
            it?.let {
                wordsHintAdapter.updateItems(it.words)
                wordsHintAdapter.setPackColor(it.color)
            }
        })
    }

    private fun setupWordsHintAdapter() {
        wordsHintAdapter = WordsHintAdapter {
            viewModel.setSelectedWord(it.id)
            onWordClick()
        }
        rvWordsHints.adapter = wordsHintAdapter
        rvWordsHints.layoutManager = CustomGridLayoutManager(activity, 1)
        rvWordsHints.setHasFixedSize(true)
    }

    private fun onWordClick() {
        activity.supportFragmentManager.beginTransaction()
                .replace(R.id.flActHint, HintSecondFragment())
                .commit()
    }

}