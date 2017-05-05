package com.appchamp.wordchunks.main;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.appchamp.wordchunks.R;

import xyz.hanks.library.SmallBang;


public class MainFragment extends Fragment {

    private SmallBang smallBang;

    static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        smallBang = SmallBang.attach2Window(getActivity());

        ImageView kittyView = (ImageView) rootView.findViewById(R.id.kitty_img);
        kittyView.setOnClickListener(v -> smallBang.bang(v));
        kittyView.callOnClick();

        return rootView;
    }

}
