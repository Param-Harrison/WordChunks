package com.appchamp.wordchunks.ui.game.fragments;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.ui.game.listeners.OnNextLevelListener;

import java.util.Random;


public class LevelSolvedFrag extends Fragment {

    private OnNextLevelListener callback;

    private RelativeLayout rlNextLevel;
    private ImageView imgRectBg;
    private TextView tvExcellent;
    private TextView tvFunFact;
    private TextView tvNextLevelTitle;
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
        rlNextLevel = (RelativeLayout) root.findViewById(R.id.rlNextLevel);
        imgRectBg = (ImageView) root.findViewById(R.id.imgRectBg);
        tvExcellent = (TextView) root.findViewById(R.id.tvExcellent);
        tvFunFact = (TextView) root.findViewById(R.id.tvFunFact);
        tvNextLevelTitle = (TextView) root.findViewById(R.id.tvNextLevelTitle);
        tvNextLevelClue = (TextView) root.findViewById(R.id.tvNextLevelClue);
        tvLevelsLeft = (TextView) root.findViewById(R.id.tvLevelsLeft);

        rlNextLevel.setOnClickListener(v -> callback.onNextLevelSelected());

        setPackColor();
        setClue();
        setFunFact();
        setLevelsLeft();
        setExcellent();

        return root;
    }

    private void setPackColor() {
        int color = getArguments().getInt("color");
        GradientDrawable drawable = (GradientDrawable) imgRectBg.getDrawable();
        drawable.setColor(color);
        tvNextLevelTitle.setTextColor(color);
    }

    private void setClue() {
        String clue = getArguments().getString("clue");
        if (clue == "") {
            tvNextLevelClue.setText("CONGRATULATIONS!");
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
            tvLevelsLeft.setText("YOU'VE FINISHED THE WHOLE PACK!");
        } else if (left == 1) {
            tvLevelsLeft.setText("ONLY ONE LEVEL LEFT IN PACK");
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
