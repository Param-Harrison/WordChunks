package com.appchamp.wordchunks.ui.game.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appchamp.wordchunks.R;


public class GameFinishedFrag extends Fragment {

    public GameFinishedFrag() {
        // Requires empty public constructor
    }

    public static GameFinishedFrag newInstance() {
        return new GameFinishedFrag();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_game_finished, container, false);
//        CardView cvRateUs = (CardView) root.findViewById(R.id.rlNextLevel);
//        cvRateUs.setOnClickListener(v ->
//                getContext().startActivity(
//                        new Intent(
//                                Intent.ACTION_VIEW,
//                                Uri.parse("market://details?id=" + getActivity().getPackageName())))
//        );
        return root;
    }
}
