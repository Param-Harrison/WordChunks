package com.appchamp.wordchunks.ui.hint

import android.arch.lifecycle.LifecycleFragment
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appchamp.wordchunks.R
import kotlinx.android.synthetic.main.frag_hint_second.*


class HintSecondFragment : LifecycleFragment() {

    private val viewModel by lazy {
        ViewModelProviders.of(activity).get(HintViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.frag_hint_second, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvHint.text = "..."
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel.getSelectedWord().observe(this, Observer {
            tvWordNumber.text = viewModel.getWordPosition()
            tvWordLetters.text = getString(R.string.number_of_letters, viewModel.getWordLength())
        })

        btnEntireSolution.setOnClickListener { tvHint.text = viewModel.getWord() }

        btnFirstLetter.setOnClickListener {
            tvHint.text = getString(R.string.first_and_dots, viewModel.getFirstLetter())
        }

        btnFirstChunk.setOnClickListener {
            tvHint.text = getString(R.string.first_and_dots, viewModel.getFirstChunk())
        }

    }
}