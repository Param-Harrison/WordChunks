package com.appchamp.wordchunks.ui.game.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.ui.game.listeners.OnBackToLevelsListener;


public class LevelSolvedBeforeFrag extends Fragment {

    private OnBackToLevelsListener callback;

    private TextView tvFunFact;

    public LevelSolvedBeforeFrag() {
        // Requires empty public constructor
    }

    public static LevelSolvedBeforeFrag newInstance() {
        return new LevelSolvedBeforeFrag();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(
                R.layout.frag_level_solved_before, container, false);
        RelativeLayout rlBackToLevels = (RelativeLayout) root.findViewById(R.id.rlBackToLevels);

        tvFunFact = (TextView) root.findViewById(R.id.tvFunFact);

        rlBackToLevels.setOnClickListener(v -> callback.onBackToLevelsSelected());

        setFunFact();

        return root;
    }

    private void setFunFact() {
        String fact = getArguments().getString("fact");
        tvFunFact.setText(fact);
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
