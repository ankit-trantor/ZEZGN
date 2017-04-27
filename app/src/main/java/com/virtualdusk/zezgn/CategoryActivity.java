package com.virtualdusk.zezgn;


import android.os.Bundle;

import com.virtualdusk.zezgn.activity.AbstractActivity;
import com.virtualdusk.zezgn.activity.CategoryScreen.CategoryFragment;
import com.virtualdusk.zezgn.activity.UpdateFragments;

public class CategoryActivity extends AbstractActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.virtualdusk.zezgn.R.layout.activity_category);
        UpdateFragments.setFragment(getSupportFragmentManager().beginTransaction(), new CategoryFragment(), com.virtualdusk.zezgn.R.id.category_frame);

    }
}
