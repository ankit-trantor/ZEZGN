package com.virtualdusk.zezgn.activity.PayInvoiceScreens;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.activity.AbstractActivity;
import com.virtualdusk.zezgn.activity.ShoopingCart.ShoppingCartActivity;

public class PayInvoiceActivity extends AbstractActivity {


    private ImageView ImgBack;
    private TextView TvAmount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pay_invoice);

        ImgBack = (ImageView)findViewById(R.id.ivBack);
        TvAmount = (TextView)findViewById(R.id.tvAmount);

        TvAmount.setText("AED " + ShoppingCartActivity.TotalCartPrice);

        ImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PayInvoiceActivity.this.finish();
            }
        });
        TabLayout tabLayout = (TabLayout)findViewById(R.id.tab_layout);

        tabLayout.addTab(tabLayout.newTab().setText("CREDIT CARD"));
        tabLayout.addTab(tabLayout.newTab().setText("DEBIT CARD"));
        tabLayout.addTab(tabLayout.newTab().setText("PAYPAL"));
        tabLayout.addTab(tabLayout.newTab().setText("COD"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.app_background_color));
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));

        final ViewPager viewPager = (ViewPager)findViewById(R.id.pager);

        final PagerAdapterPayInvoice adapter = new PagerAdapterPayInvoice
                (getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(4);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        PayInvoiceActivity.this.finish();
    }
}
