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
import com.appchamp.wordchunks.ui.game.adapters.WordsHintAdapter
import kotlinx.android.synthetic.main.frag_hint_first.*
import org.jetbrains.anko.AnkoLogger


class HintFirstFragment : LifecycleFragment(), AnkoLogger {

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