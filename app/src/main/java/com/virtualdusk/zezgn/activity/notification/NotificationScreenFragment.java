package com.virtualdusk.zezgn.activity.notification;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.activity.AbstractActivity;
import com.virtualdusk.zezgn.utils.ConnectionDetector;

/**
 * Created by Amit Sharma on 10/14/2016.
 */
public class NotificationScreenFragment extends Fragment {

    private String strMoveTab = "";
    private String TAG = "Notification";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = this.getArguments();
        if (b != null) {
            strMoveTab =b.getString("tab","");
            Log.d(TAG, "onCreate: strMoveTab " + strMoveTab);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View  v = inflater.inflate(R.layout.notification, container, false);


        TabLayout tabLayout = (TabLayout)v.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("NEW DESIGNS"));
        tabLayout.addTab(tabLayout.newTab().setText("ORDER STATUS"));
        tabLayout.addTab(tabLayout.newTab().setText("PROMOS"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.app_background_color));
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));

        if (new ConnectionDetector(getActivity()).isConnectingToInternet()) {

        } else {
            Toast.makeText(getActivity(),getString(R.string.no_internet),Toast.LENGTH_LONG).show();
        }


        final ViewPager viewPager = (ViewPager)v.findViewById(R.id.pager);
        final PagerAdapterNotifications adapter = new PagerAdapterNotifications
                (getActivity().getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
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

        viewPager.setOffscreenPageLimit(3);

        Log.d(TAG, "onCreateView: " + strMoveTab);
        if(!strMoveTab.isEmpty()){
            if(strMoveTab.equalsIgnoreCase("0")){
                viewPager.setCurrentItem(0);
            }else if(strMoveTab.equalsIgnoreCase("1")){
                viewPager.setCurrentItem(1);
            }else{
                viewPager.setCurrentItem(2);
            }
        }

        return v;
    }
}
