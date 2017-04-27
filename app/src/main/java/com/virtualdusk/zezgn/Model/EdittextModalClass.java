package com.virtualdusk.zezgn.Model;

import android.widget.EditText;

/**
 * Created by bhart.gupta on 24-Oct-16.
 */

public class EdittextModalClass {

    private EditText et;
    private String textColor;
    private String backgroundColor;
    private float textsize;
    private String font;
    private int edittextNumber;

    public EditText getEt() {
        return et;
    }

    public void setEt(EditText et) {
        this.et = et;
    }

    public int getEdittextNumber() {
        return edittextNumber;
    }

    public void setEdittextNumber(int edittextNumber) {
        this.edittextNumber = edittextNumber;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public float getTextsize() {
        return textsize;
    }

    public void setTextsize(float textsize) {
        this.textsize = textsize;
    }

    public String getFont() {
        return font;
    }

    public void setFont(String font) {
        this.font = font;
    }
}
