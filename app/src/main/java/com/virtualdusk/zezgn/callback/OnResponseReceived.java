package com.virtualdusk.zezgn.callback;

/**
 * Created by hitesh.mathur on 8/8/2016.
 */
public interface OnResponseReceived {

    void onErrorReceived(String msg);

    void onResultReceived(String result);
}
