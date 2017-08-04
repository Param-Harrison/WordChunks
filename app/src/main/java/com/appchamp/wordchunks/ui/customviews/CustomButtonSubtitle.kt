/*
 * Copyright 2017 Julia Kozhukhovskaya
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.appchamp.wordchunks.ui.customviews

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.appchamp.wordchunks.R
import kotlinx.android.synthetic.main.custom_button_subtitle.view.*


class CustomButtonSubtitle : RelativeLayout {

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    private fun init() {

        View.inflate(context, R.layout.custom_button_subtitle, this)
    }

    private fun init(context: Context?, attrs: AttributeSet?) {
        init()
        // Getting the attributes.
        val a = context?.theme?.obtainStyledAttributes(attrs, R.styleable.CustomButtonSubtitle, 0, 0)
        val title: String?
        val subtitle: String?
        val drawableIcon: Drawable?
        val drawableBg: Drawable?
        val colorTitleText: ColorStateList?
        val colorSubtitleText: ColorStateList?
        try {
            title = a?.getString(R.styleable.CustomButtonSubtitle_cbs_title)
            subtitle = a?.getString(R.styleable.CustomButtonSubtitle_cbs_subtitle)
            drawableIcon = a?.getDrawable(R.styleable.CustomButtonSubtitle_cbs_icon)
            drawableBg = a?.getDrawable(R.styleable.CustomButtonSubtitle_cbs_bg)
            colorTitleText = a?.getColorStateList(R.styleable.CustomButtonSubtitle_cbs_color_title)
            colorSubtitleText = a?.getColorStateList(R.styleable.CustomButtonSubtitle_cbs_color_subtitle)
        } finally {
            a?.recycle()
        }
        // Setting the attributes to view.
        tvTitle?.text = title
        tvSubtitle?.text = subtitle
        imgIcon?.setImageDrawable(drawableIcon)
        imgShapeBg?.setImageDrawable(drawableBg)
        tvTitle?.setTextColor(colorTitleText)
        tvSubtitle?.setTextColor(colorSubtitleText)
    }
}
