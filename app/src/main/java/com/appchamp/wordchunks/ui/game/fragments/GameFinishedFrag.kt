package com.appchamp.wordchunks.ui.game.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.appchamp.wordchunks.R
import kotlinx.android.synthetic.main.frag_game_finished.*
import org.jetbrains.anko.browse


class GameFinishedFrag : Fragment() {

    companion object {
        fun newInstance() = GameFinishedFrag()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.frag_game_finished, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rlRateUs.setOnClickListener {
            context.browse("market://details?id=${activity.packageName}")
        }
    }
}
