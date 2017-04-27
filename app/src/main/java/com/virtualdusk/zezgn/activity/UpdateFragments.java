package com.virtualdusk.zezgn.activity;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;



import java.util.Stack;

import com.virtualdusk.zezgn.R;


public class UpdateFragments {

    public static Stack<Fragment> fragmentStack;
    static Context context;
    static ActionBar actionBar;
    static LayoutInflater inflaters;

    public UpdateFragments(Context context) {
        this.context = context;
        this.inflaters = LayoutInflater.from(context);

        fragmentStack = new Stack<Fragment>();
    }
    // set Fragment method
    public static void setFragment(FragmentTransaction fragmentManager, Fragment fragment, int containerViewId) {

        fragmentManager.replace(containerViewId, fragment);
        fragmentManager.commit();
        fragmentManager.setCustomAnimations(R.anim.enter_from_right,R.anim.exit_to_left);
        fragmentStack.push(fragment);

    }

    public static Fragment getFragment(){
        return fragmentStack.lastElement();
    }

    // set previous fragment on back button
    public static void backSetFragment(FragmentTransaction fragmentManager, int containerViewId) {
        fragmentStack.remove(fragmentStack.pop());
        fragmentManager.replace(containerViewId, fragmentStack.lastElement());
        fragmentManager.commit();
        fragmentManager.setCustomAnimations(R.anim.enter_from_left,
                R.anim.exit_to_right);
    }


    // set toolbar on fragment change
    public static void setActionBar(int layout) {
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        inflaters = LayoutInflater.from(context);

        View view = inflaters.inflate(layout,
                null);

        ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(view, layoutParams);
        Toolbar parent = (Toolbar) view.getParent();
        parent.setContentInsetsAbsolute(0, 0);

    }


    // for clear all fragments
    public static void removeAllFragments() {
        fragmentStack.removeAllElements();
    }


}
