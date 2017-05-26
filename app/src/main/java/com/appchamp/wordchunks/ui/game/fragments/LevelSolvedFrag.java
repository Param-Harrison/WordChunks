package com.appchamp.wordchunks.ui.game.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.ui.game.listeners.OnNextLevelListener;


public class LevelSolvedFrag extends Fragment {

    private OnNextLevelListener callback;

    public LevelSolvedFrag() {
        // Requires empty public constructor
    }

    public static LevelSolvedFrag newInstance() {
            //int color, String clue, String fact, long levelsLeft) {
        return new LevelSolvedFrag();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_level_solved, container, false);
        CardView cvNextLevel = (CardView) root.findViewById(R.id.cvNextLevel);
        LinearLayout llPackBg = (LinearLayout) root.findViewById(R.id.llPackBg);
        TextView tvExcellent = (TextView) root.findViewById(R.id.tvExcellent);
        TextView tvFunFact = (TextView) root.findViewById(R.id.tvFunFact);
        TextView tvFunFactTitle = (TextView) root.findViewById(R.id.tvFunFactTitle);
        TextView tvNextLevelClue = (TextView) root.findViewById(R.id.tvNextLevelClue);
        TextView tvLevelsLeft = (TextView) root.findViewById(R.id.tvLevelsLeft);

        cvNextLevel.setOnClickListener(v -> callback.onNextLevelSelected());

        int color = getArguments().getInt("color");
        llPackBg.setBackgroundColor(color);
        tvFunFactTitle.setTextColor(color);

        String clue = getArguments().getString("clue");
        if (clue == "") {
            tvNextLevelClue.setText("GO GET YOUR PRIZE!");
        } else {
            tvNextLevelClue.setText(clue);
        }


        String fact = getArguments().getString("fact");
        tvFunFact.setText(fact);

        long left = getArguments().getLong("left");
        if (left == 0) {
            tvLevelsLeft.setText("YOU FINISHED THE PACK!");
        } else if (left == 1) {
            tvLevelsLeft.setText(left + " LEVEL LEFT IN PACK");
        } else {
            tvLevelsLeft.setText(left + " LEVELS LEFT IN PACK");
        }


        tvExcellent.setText("AWESOME!");

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
