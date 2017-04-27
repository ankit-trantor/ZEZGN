package com.virtualdusk.zezgn.utils;


import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TextViewGothmMonoster extends TextView {
    public TextViewGothmMonoster(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TextViewGothmMonoster(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextViewGothmMonoster(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
               "montserratboldbold.TTF");
        setTypeface(tf);
    }

}
