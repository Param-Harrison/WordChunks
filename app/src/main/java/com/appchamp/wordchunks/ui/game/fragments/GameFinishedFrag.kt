package com.appchamp.wordchunks.ui.game.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.appchamp.wordchunks.R


class GameFinishedFrag : Fragment() {

    companion object {
        fun newInstance() = GameFinishedFrag()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val root = inflater?.inflate(R.layout.frag_game_finished, container, false)
        //        CardView cvRateUs = (CardView) root.findViewById(R.id.rlNextLevel);
        //        cvRateUs.setOnClickListener(v ->
        //                getContext().startActivity(
        //                        new Intent(
        //                                Intent.ACTION_VIEW,
        //                                Uri.parse("market://details?id=" + getActivity().getPackageName())))
        //        );
        return root
    }

}
