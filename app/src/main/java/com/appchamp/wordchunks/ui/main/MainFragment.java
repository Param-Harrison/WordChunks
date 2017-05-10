package com.appchamp.wordchunks.ui.main;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.appchamp.wordchunks.R;
import com.appchamp.wordchunks.data.DatabaseHelperRealm;
import com.appchamp.wordchunks.ui.game.GameActivity;
import com.appchamp.wordchunks.util.AnimUtils;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import io.realm.Realm;

import static com.appchamp.wordchunks.util.Constants.EXTRA_LEVEL_ID;
import static com.appchamp.wordchunks.util.Constants.LEVEL_STATE_CURRENT;


public class MainFragment extends Fragment {

    //private SmallBang smallBang;

    private SlidingMenu menu;

    private DatabaseHelperRealm db;


    static MainFragment newInstance() {
        return new MainFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

       // smallBang = SmallBang.attach2Window(getActivity());

        //ImageView kittyView = (ImageView) rootView.findViewById(R.id.imgKitty);
        //kittyView.setOnClickListener(v -> smallBang.bang(v));
       // kittyView.callOnClick();

        initLeftMenu();

        rootView.findViewById(R.id.imgSettingsIcon).setOnClickListener(v -> {
            AnimUtils.startAnimationFadeIn(getActivity(), v);
            menu.toggle();
        });

        db = new DatabaseHelperRealm();
        rootView.findViewById(R.id.btnPlay).setOnClickListener(v -> startGameActivity());

        rootView.findViewById(R.id.btnDaily).setOnClickListener(v -> {
            // todo daily puzzle
        });

        rootView.findViewById(R.id.btnShop).setOnClickListener(v -> {
            // todo shop
        });
        return rootView;
    }

    private void startGameActivity() {
        Intent intent = new Intent(getActivity(), GameActivity.class);

        Realm realm = Realm.getDefaultInstance();
        // Finds _last_ current level id to pass by the Intent into GamesActivity.
        String levelId = db.findLastLevelIdByState(realm, LEVEL_STATE_CURRENT);
        realm.close();
        intent.putExtra(EXTRA_LEVEL_ID, levelId);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    private void initLeftMenu() {
        // Configure the SlidingMenu
        menu = new SlidingMenu(getActivity());
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(getActivity(), SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.settings_menu);
    }
}
