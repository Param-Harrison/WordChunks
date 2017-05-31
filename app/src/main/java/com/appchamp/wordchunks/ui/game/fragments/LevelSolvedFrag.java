package com.appchamp.wordchunks.ui.game.fragments;

import android.content.Context;
import android.content.res.Resources;
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

import java.util.Random;


public class LevelSolvedFrag extends Fragment {

    private OnNextLevelListener callback;

    private LinearLayout llPackBg;
    private TextView tvExcellent;
    private TextView tvFunFact;
    private TextView tvFunFactTitle;
    private TextView tvNextLevelClue;
    private TextView tvLevelsLeft;

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
        CardView cvNextLevel = (CardView) root.findViewById(R.id.cvRateUs);
        llPackBg = (LinearLayout) root.findViewById(R.id.llPackBg);
        tvExcellent = (TextView) root.findViewById(R.id.tvExcellent);
        tvFunFact = (TextView) root.findViewById(R.id.tvFunFact);
        tvFunFactTitle = (TextView) root.findViewById(R.id.tvFunFactTitle);
        tvNextLevelClue = (TextView) root.findViewById(R.id.tvSubtitle);
        tvLevelsLeft = (TextView) root.findViewById(R.id.tvLevelsLeft);

        cvNextLevel.setOnClickListener(v -> callback.onNextLevelSelected());

        setPackColor();
        setClue();
        setFunFact();
        setLevelsLeft();
        setExcellent();

        return root;
    }

    private void setPackColor() {
        int color = getArguments().getInt("color");
        llPackBg.setBackgroundColor(color);
        tvFunFactTitle.setTextColor(color);

    }

    private void setClue() {
        String clue = getArguments().getString("clue");
        if (clue == "") {
            tvNextLevelClue.setText("GO GET YOUR PRIZE!");
        } else {
            tvNextLevelClue.setText(clue);
        }
    }

    private void setFunFact() {
        String fact = getArguments().getString("fact");
        tvFunFact.setText(fact);
    }

    private void setLevelsLeft() {
        long left = getArguments().getLong("left");
        if (left == 0) {
            tvLevelsLeft.setText("YOU'VE FINISHED THE PACK!");
        } else if (left == 1) {
            tvLevelsLeft.setText("JUST ONE LEVEL LEFT IN PACK");
        } else {
            tvLevelsLeft.setText(left + " LEVELS LEFT IN PACK");
        }

    }

    private void setExcellent() {
        Resources res = getContext().getResources();
        String[] congrats = res.getStringArray(R.array.congrats);
        int i = new Random().nextInt(congrats.length - 1);
        tvExcellent.setText(congrats[i]);
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
