package com.virtualdusk.zezgn.activity.notification;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Amit Sharma on 10/14/2016.
 */
public class PagerAdapterNotifications extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapterNotifications(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                NewDesignFragment tab1 = new NewDesignFragment();
                return tab1;
            case 1:
                OrderStatusFragment tab2 = new OrderStatusFragment();
                return tab2;
            case 2:
                PromosFragment tab3 = new PromosFragment();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}