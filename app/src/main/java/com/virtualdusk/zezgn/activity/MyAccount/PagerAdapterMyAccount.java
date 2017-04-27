package com.virtualdusk.zezgn.activity.MyAccount;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.virtualdusk.zezgn.activity.notification.NewDesignFragment;
import com.virtualdusk.zezgn.activity.notification.OrderStatusFragment;
import com.virtualdusk.zezgn.activity.notification.PromosFragment;

/**
 * Created by Amit Sharma on 10/14/2016.
 */
public class PagerAdapterMyAccount extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapterMyAccount(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                MyCreationsListFragment tab1 = new MyCreationsListFragment();
                return tab1;
            case 1:
                MyFavoritesListsFragment tab2 = new MyFavoritesListsFragment();
                return tab2;
            case 2:
                MyOrderListsFragment tab3 = new MyOrderListsFragment();
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