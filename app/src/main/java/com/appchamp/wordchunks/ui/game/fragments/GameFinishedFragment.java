package com.appchamp.wordchunks.ui.game.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appchamp.wordchunks.R;


public class GameFinishedFragment extends Fragment {

    public GameFinishedFragment() {
        // Requires empty public constructor
    }


    public static GameFinishedFragment newInstance() {
        return new GameFinishedFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_game_finished, container, false);

        return root;
    }
}
