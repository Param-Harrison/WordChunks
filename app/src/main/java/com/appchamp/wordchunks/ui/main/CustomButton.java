package com.appchamp.wordchunks.ui.main;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.appchamp.wordchunks.R;


public class CustomButton extends RelativeLayout {

    private TextView txtTitle;
    private ImageView imgIcon;
    private ImageView imgRectBg;

    public CustomButton(Context context) {
        super(context);
        init();
    }

    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init() {
        inflate(getContext(), R.layout.custom_button, this);
        this.txtTitle = (TextView) findViewById(R.id.txtTitle);
        this.imgIcon = (ImageView) findViewById(R.id.icon);
        this.imgRectBg = (ImageView) findViewById(R.id.imgRectBg);
    }

    private void init(Context context, AttributeSet attrs) {

        init();
        // Getting the attributes.
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CustomButton,
                0, 0);
        String title;
        Drawable drawableIcon;
        Drawable drawableBg;
        ColorStateList colorTxt;
        try {
            title = a.getString(R.styleable.CustomButton_cb_title);
            drawableIcon = a.getDrawable(R.styleable.CustomButton_cb_icon);
            drawableBg = a.getDrawable(R.styleable.CustomButton_cb_bg);
            colorTxt = a.getColorStateList(R.styleable.CustomButton_cb_color_title);
        } finally {
            a.recycle();
        }
        // Setting the attributes to view.
        this.txtTitle.setText(title);
        this.imgIcon.setImageDrawable(drawableIcon);
        this.imgRectBg.setImageDrawable(drawableBg);
        this.txtTitle.setTextColor(colorTxt);
    }
}
