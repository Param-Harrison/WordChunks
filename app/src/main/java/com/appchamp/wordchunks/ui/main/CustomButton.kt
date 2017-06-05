package com.appchamp.wordchunks.ui.main

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout
import com.appchamp.wordchunks.R
import kotlinx.android.synthetic.main.custom_button.view.*


class CustomButton : RelativeLayout {

    constructor(context: Context?) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        init(context, attrs)
    }

    private fun init() {
        View.inflate(context, R.layout.custom_button, this)
    }

    private fun init(context: Context?, attrs: AttributeSet?) {
        init()
        // Getting the attributes.
        val a = context?.theme?.obtainStyledAttributes(attrs, R.styleable.CustomButton, 0, 0)
        val title: String?
        val drawableIcon: Drawable?
        val drawableBg: Drawable?
        val colorTxt: ColorStateList?
        try {
            title = a?.getString(R.styleable.CustomButton_cb_title)
            drawableIcon = a?.getDrawable(R.styleable.CustomButton_cb_icon)
            drawableBg = a?.getDrawable(R.styleable.CustomButton_cb_bg)
            colorTxt = a?.getColorStateList(R.styleable.CustomButton_cb_color_title)
        } finally {
            a?.recycle()
        }
        // Setting the attributes to view.
        txtTitle.text = title
        icon.setImageDrawable(drawableIcon)
        imgRectBg.setImageDrawable(drawableBg)
        txtTitle.setTextColor(colorTxt)
    }
}
