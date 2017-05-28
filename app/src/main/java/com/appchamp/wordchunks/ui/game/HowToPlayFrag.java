package com.appchamp.wordchunks.ui.game;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.ui.game.listeners.OnCloseBtnListener;

public class HowToPlayFrag extends Fragment {

    private OnCloseBtnListener callback;

    public HowToPlayFrag() {
        // Requires empty public constructor
    }

    public static HowToPlayFrag newInstance() {
        return new HowToPlayFrag();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.frag_how_to_play, container, false);
        //root.findViewById(R.id.btnClose).setOnClickListener(v -> callback.startGameFromHowToPlay());
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            callback = (OnCloseBtnListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnCloseBtnListener");
        }
    }
}
