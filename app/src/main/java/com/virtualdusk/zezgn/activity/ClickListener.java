package com.virtualdusk.zezgn.activity;

import android.view.View;

/**
 * Created by Amit Sharma on 10/13/2016.
 */
public interface ClickListener {
    void onClick(View view, int position);

    void onLongClick(View view, int position);
}
