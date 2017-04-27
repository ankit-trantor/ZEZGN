package com.virtualdusk.zezgn.activity.PayInvoiceScreens;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.activity.ShoopingCart.ShoppingCartFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class PayInvoiceFragment extends Fragment {

    private ImageView ImgBack;
    private TextView TvAmount;
    private ViewPager viewPager;

    public PayInvoiceFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_pay_invoice, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImgBack = (ImageView)view.findViewById(R.id.ivBack);
        TvAmount = (TextView)view.findViewById(R.id.tvAmount);

        TvAmount.setText("AED " + String.format("%.2f", ShoppingCartFragment.TotalCartPrice));

//        ImgBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                PayInvoiceActivity.this.finish();
//            }
//        });
        TabLayout tabLayout = (TabLayout)view.findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("CREDIT CARD"));
        tabLayout.addTab(tabLayout.newTab().setText("DEBIT CARD"));
        tabLayout.addTab(tabLayout.newTab().setText("PAYPAL"));
        tabLayout.addTab(tabLayout.newTab().setText("COD"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.app_background_color));
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));

        viewPager = (ViewPager)view.findViewById(R.id.pager);

        PagerAdapterPayInvoice adapter = new PagerAdapterPayInvoice
                (getFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
//        viewPager.setOffscreenPageLimit(3);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
