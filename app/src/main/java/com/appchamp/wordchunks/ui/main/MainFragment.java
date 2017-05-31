package com.appchamp.wordchunks.ui.main;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.data.LevelsRealmHelper;

import io.realm.Realm;

import static com.appchamp.wordchunks.util.Constants.LEVEL_STATE_CURRENT;


public class MainFragment extends Fragment {

    //private SmallBang smallBang;

    private OnMainFragmentClickListener callback;

    static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.frag_main, container, false);

        // smallBang = SmallBang.attach2Window(getActivity());

        //ImageView kittyView = (ImageView) rootView.findViewById(R.id.imgKitty);
        //kittyView.setOnClickListener(v -> smallBang.bang(v));
        // kittyView.callOnClick();

        rootView.findViewById(R.id.imgSettingsIcon).setOnClickListener(
                (View v) -> callback.showSlidingMenu());
        rootView.findViewById(R.id.imgShareIcon).setOnClickListener(this::onShareClick);

        rootView.findViewById(R.id.btnPlay).setOnClickListener(this::onPlayClick);
        rootView.findViewById(R.id.btnDaily).setOnClickListener(this::onDailyClick);
        rootView.findViewById(R.id.btnPacks).setOnClickListener(this::onPacksClick);
        rootView.findViewById(R.id.btnStore).setOnClickListener(this::onStoreClick);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            callback = (OnMainFragmentClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnMainFragmentClickListener");
        }
    }

    private void onPlayClick(View v) {
        Realm realm = Realm.getDefaultInstance();
        String levelId = getLastCurrentLevelId(realm);
        realm.close();
        if (levelId != null) {
            callback.startGameActivity(levelId);
        } else {
            // If all levels and packs were solved, showing the fragment
            callback.showGameFinishedFragment();
        }
    }

    private void onPacksClick(View v) {
        callback.showPacksActivity();
    }

    private void onStoreClick(View v) {
    }

    private void onDailyClick(View v) {
    }

    private void onShareClick(View v) {
    }

    private String getLastCurrentLevelId(Realm realm) {

        long countLevelsByCurrentState =
                LevelsRealmHelper.countLevelsByState(realm, LEVEL_STATE_CURRENT);

        // If not all levels were solved
        if (countLevelsByCurrentState != 0) {
            // Getting the last "current" level id
            return LevelsRealmHelper.findLastLevelByState(realm, LEVEL_STATE_CURRENT).getId();
        } else {
            // If all levels and packs were solved, showing the fragment
            return null;
        }
    }

}
