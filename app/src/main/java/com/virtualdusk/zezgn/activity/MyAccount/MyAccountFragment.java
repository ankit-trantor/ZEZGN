package com.virtualdusk.zezgn.activity.MyAccount;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.internal.Utility;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.squareup.picasso.Picasso;

import com.virtualdusk.zezgn.Profile.UserProfile;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Constant;
import com.virtualdusk.zezgn.activity.CustomDesignActivity;
import com.virtualdusk.zezgn.activity.Home;
import com.virtualdusk.zezgn.activity.UpdateFragments;
import com.virtualdusk.zezgn.activity.notification.PagerAdapterNotifications;
import com.virtualdusk.zezgn.utils.helper.AppUtils;

import static com.virtualdusk.zezgn.Utilities.Constant.IMAGE_BASE_URL;
import static com.virtualdusk.zezgn.activity.Home.strKey;


/**
 * Created by Amit Sharma on 10/17/2016.
 */
public class MyAccountFragment extends Fragment {

    private static final String TAG = "MyAccountFragment";
    private TextView TV_Name, TV_Email, TV_Phone;
    private SharedPreferences sharedPreferences;
    private ImageView IvProfilePic;
    private RelativeLayout rlUserInfo;
    private String strUserName,strUserEmail,strUserPhone,strProfilePicUrl;
    private ViewPager viewPager;
    private PagerAdapterMyAccount adapter;
    private DisplayImageOptions options;
    private ImageLoader imageLoader =null;// ImageLoader.getInstance();
    private String strMove = "";
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle mBundle = this.getArguments();

        if(mBundle != null){
            strMove = mBundle.getString("key","");
        }
        sharedPreferences = getActivity().getSharedPreferences(AppUtils.PREF_NAME, Context.MODE_PRIVATE);
        strUserName = sharedPreferences.getString(getString(R.string.key_name), "");
        strUserEmail = sharedPreferences.getString(getString(R.string.key_email), "");
        strUserPhone = sharedPreferences.getString(getString(R.string.key_mobile), "");
        strProfilePicUrl = sharedPreferences.getString(getString(R.string.key_profile_image), "");
        Log.d(TAG,"strProfilePicUrl " + strProfilePicUrl);

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.user_placeholder)
                .showImageForEmptyUri(R.mipmap.user_placeholder)
                .showImageOnFail(R.mipmap.user_placeholder)
                .resetViewBeforeLoading()
                .delayBeforeLoading(1000)
                .cacheInMemory()
                .cacheOnDisc()
                .build();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v=inflater.inflate(R.layout.my_account_screen,container,false);

        rlUserInfo=(RelativeLayout)v.findViewById(R.id.rlUserInfo);
        IvProfilePic = (ImageView)v.findViewById(R.id.ivProfilePic);

        TV_Name = (TextView)v.findViewById(R.id.lblName);
        TV_Email = (TextView)v.findViewById(R.id.lblEmail);
        TV_Phone = (TextView)v.findViewById(R.id.lblPhone);

        TV_Name.setText(strUserName);
        TV_Email.setText(strUserEmail);
        TV_Phone.setText(strUserPhone);

        TabLayout tabLayout = (TabLayout)v.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("MY CREATIONS"));
        tabLayout.addTab(tabLayout.newTab().setText("MY FAVORITES"));
        tabLayout.addTab(tabLayout.newTab().setText("MY ORDERS"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.app_background_color));
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.white));

        viewPager = (ViewPager)v.findViewById(R.id.pager);
        adapter = new PagerAdapterMyAccount
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

        if(!strProfilePicUrl.isEmpty()){

//            try{
//                Picasso.with(getActivity())
//                        .load(Constant.IMAGE_BASE_URL+strProfilePicUrl)
//                        .error(R.mipmap.user_placeholder)
//                        .into(IvProfilePic);
//            }catch (Exception e){
//                e.printStackTrace();
//            }

            imageLoader.displayImage(Constant.IMAGE_BASE_URL+strProfilePicUrl, IvProfilePic,
                    options, new ImageLoadingListener() {

                        public void onLoadingCancelled(String imageUri,
                                                       View view) {
                        }

                        public void onLoadingComplete(String arg0,
                                                      View arg1, Bitmap arg2) {


                        }

                        public void onLoadingStarted(String arg0, View arg1) {
                            // TODO Auto-generated method stub
                            //if (post_image.getVisibility() == View.VISIBLE) {
                            //   load_progressbar.setVisibility(View.VISIBLE);
                            //}
                        }

                        @Override
                        public void onLoadingFailed(String arg0, View arg1,
                                                    FailReason arg2) {
                            // TODO Auto-generated method stub
                            //load_progressbar.setVisibility(View.GONE);
                        }
                    });


        }else{
           // IvProfilePic.setImageResource(R.mipmap.user_placeholder);

        }


        rlUserInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strKey = "my_acc";

                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame, new UserProfile()).addToBackStack(null).commit();
                //UpdateFragments.setFragment(getActivity().getSupportFragmentManager().beginTransaction(),new UserProfile(), R.id.frame);
            }
        });

        if(Home.strMove.equalsIgnoreCase("order")){
            viewPager.setCurrentItem(2);
            Home.strMove = "";
        }

        viewPager.setOffscreenPageLimit(3);

        if(strMove.equalsIgnoreCase("account")){
            strKey = "my_acc";
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame, new UserProfile()).addToBackStack(null).commit();
        }

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        Home.ivSetting.setVisibility(View.VISIBLE);
        Home.tvTitle.setText("");

    }
}
