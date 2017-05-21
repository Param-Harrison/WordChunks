package com.appchamp.wordchunks.util;


import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.appchamp.wordchunks.R;

public final class AnimUtils {

    private AnimUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static void startAnimationFadeIn(Context context, View v) {
        Animation animFadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in);
        v.startAnimation(animFadeIn);
    }
}
