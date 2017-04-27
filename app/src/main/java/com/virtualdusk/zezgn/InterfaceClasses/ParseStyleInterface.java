package com.virtualdusk.zezgn.InterfaceClasses;

import java.util.ArrayList;

import com.virtualdusk.zezgn.Model.Style;

/**
 * Created by bhart.gupta on 03-Nov-16.
 */

public interface ParseStyleInterface {

    public void onStyleResponseError(String msg);
    public void onStyleResponseSuccess(ArrayList<Style> StyleList, String msg);
}
