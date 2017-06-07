package com.appchamp.wordchunks.extensions

import android.content.Context
import android.content.res.Resources
import android.support.annotation.ColorRes
import android.support.annotation.DrawableRes
import android.support.v4.content.res.ResourcesCompat


fun Context.color(@ColorRes id: Int, theme: Resources.Theme? = null) = ResourcesCompat
        .getColor(resources, id, theme)

fun Context.drawable(@DrawableRes id: Int, theme: Resources.Theme? = null) = ResourcesCompat
        .getDrawable(resources, id, theme)