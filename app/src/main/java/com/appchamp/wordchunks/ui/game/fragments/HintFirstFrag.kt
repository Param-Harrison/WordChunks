package com.appchamp.wordchunks.ui.game.fragments

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import com.appchamp.wordchunks.R
import com.appchamp.wordchunks.models.realm.Level
import com.appchamp.wordchunks.ui.game.CustomGridLayoutManager
import com.appchamp.wordchunks.ui.game.adapters.WordsHintAdapter
import io.realm.Realm
import kotlinx.android.synthetic.main.frag_hint_first.*
import org.jetbrains.anko.AnkoLogger


class HintFirstFrag: Fragment(), AnkoLogger {

    private lateinit var wordsHintAdapter: WordsHintAdapter

    companion object {
        fun newInstance() = HintFirstFrag()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.frag_hint_first, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val words = Realm.getDefaultInstance().where(Level::class.java)
                .findFirst().words


        wordsHintAdapter = WordsHintAdapter(words, Color.parseColor("#cccccc"))
        rvWordsHints.adapter = wordsHintAdapter
        rvWordsHints.layoutManager = CustomGridLayoutManager(activity, 1)
        rvWordsHints.setHasFixedSize(true)
        rvWordsHints.translationY = 0.5F
        rvWordsHints.alpha = 0f
        rvWordsHints.animate()
                .translationY(0F)
                .setDuration(800)
                .alpha(1f)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
    }


}