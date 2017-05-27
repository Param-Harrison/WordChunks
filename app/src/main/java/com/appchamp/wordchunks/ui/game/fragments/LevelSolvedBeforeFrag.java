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
import com.appchamp.wordchunks.ui.game.listeners.OnBackToLevelsListener;

import java.util.Random;


public class LevelSolvedBeforeFrag extends Fragment {

    private OnBackToLevelsListener callback;

    private LinearLayout llPackBg;
    private TextView tvExcellent;
    private TextView tvFunFact;
    private TextView tvFunFactTitle;

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
        CardView btnBackToLevels = (CardView) root.findViewById(R.id.cvBackToLevels);
        llPackBg = (LinearLayout) root.findViewById(R.id.llPackBg);
        tvExcellent = (TextView) root.findViewById(R.id.tvExcellent);
        tvFunFact = (TextView) root.findViewById(R.id.tvFunFact);
        tvFunFactTitle = (TextView) root.findViewById(R.id.tvFunFactTitle);

        btnBackToLevels.setOnClickListener(v -> callback.onBackToLevelsSelected());

        setPackColor();
        setFunFact();
        setExcellent();

        return root;
    }

    private void setPackColor() {
        int color = getArguments().getInt("color");
        llPackBg.setBackgroundColor(color);
        tvFunFactTitle.setTextColor(color);

    }

    private void setFunFact() {
        String fact = getArguments().getString("fact");
        tvFunFact.setText(fact);
    }

    private void setExcellent() {
        Resources res = getActivity().getResources();
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
            callback = (OnBackToLevelsListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnBackToLevelsListener");
        }
    }

}
