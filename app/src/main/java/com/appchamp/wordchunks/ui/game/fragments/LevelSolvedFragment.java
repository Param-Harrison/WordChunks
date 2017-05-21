package com.appchamp.wordchunks.ui.game.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.ui.game.listeners.OnNextLevelListener;


public class LevelSolvedFragment extends Fragment {

    private OnNextLevelListener callback;

    public LevelSolvedFragment() {
        // Requires empty public constructor
    }

    public static LevelSolvedFragment newInstance() {
        return new LevelSolvedFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_level_solved, container, false);
        Button btnNextLevel = (Button) root.findViewById(R.id.btnNextLevel);
        btnNextLevel.setOnClickListener(
                v -> callback.onNextLevelSelected());
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            callback = (OnNextLevelListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnNextLevelListener");
        }
    }

}
