package com.appchamp.wordchunks.ui.game;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appchamp.wordchunks.R;

public class LevelSolvedFragment extends Fragment {

    public LevelSolvedFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_level_solved, container, false);
        return root;
    }

    public LevelSolvedFragment newInstance() {
        return new LevelSolvedFragment();
    }
}
