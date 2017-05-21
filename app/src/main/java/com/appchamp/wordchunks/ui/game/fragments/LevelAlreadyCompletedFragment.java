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
import com.appchamp.wordchunks.ui.game.listeners.OnBackToLevelsListener;


public class LevelAlreadyCompletedFragment extends Fragment {

    private OnBackToLevelsListener callback;

    public LevelAlreadyCompletedFragment() {
        // Requires empty public constructor
    }

    public static LevelAlreadyCompletedFragment newInstance() {
        return new LevelAlreadyCompletedFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(
                R.layout.frag_level_already_solved, container, false);
        Button btnBackToLevels = (Button) root.findViewById(R.id.btnBackToLevels);
        btnBackToLevels.setOnClickListener(
                v -> callback.onBackToLevelsSelected());
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            callback = (OnBackToLevelsListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnBackToLevelsListener");
        }
    }

}
