package com.appchamp.wordchunks.ui.game;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.appchamp.wordchunks.R;


public class LevelSolvedFragment extends Fragment {

    OnNextLevelListener callback;

    public LevelSolvedFragment() {
    }

    public static LevelSolvedFragment newInstance() {
        return new LevelSolvedFragment();
    }

    public interface OnNextLevelListener {
        void onNextLevelSelected();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_level_solved, container, false);
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
                    + " must implement OnHeadlineSelectedListener");
        }
    }

}
