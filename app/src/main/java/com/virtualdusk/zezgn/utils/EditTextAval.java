package com.virtualdusk.zezgn.utils;


import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

public class EditTextAval extends EditText {
    public EditTextAval(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public EditTextAval(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public EditTextAval(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(),
               "AVALONN.TTF");
        setTypeface(tf);
    }

}
