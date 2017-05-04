package com.appchamp.wordchunks.util;


import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.appchamp.wordchunks.R;

public class AnimUtils {


    public static void startAnimationFadeIn(Context context, View v) {
        Animation animFadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        v.startAnimation(animFadeIn);
    }
}
