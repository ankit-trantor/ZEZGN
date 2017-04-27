package com.virtualdusk.zezgn.activity.PayInvoiceScreens;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.virtualdusk.zezgn.activity.MyAccount.MyCreationsListFragment;
import com.virtualdusk.zezgn.activity.MyAccount.MyFavoritesListsFragment;
import com.virtualdusk.zezgn.activity.MyAccount.MyOrderListsFragment;

/**
 * Created by Amit Sharma on 10/14/2016.
 */
public class PagerAdapterPayInvoice extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapterPayInvoice(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                CreditCard tab1 = new CreditCard();
                return tab1;
            case 1:
                DebitCardFragment tab2 = new DebitCardFragment();
                return tab2;
            case 2:
                PaypalFragment tab3 = new PaypalFragment();
                return tab3;
            case 3:
                CodFragment tab4 = new CodFragment();
                return tab4;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}