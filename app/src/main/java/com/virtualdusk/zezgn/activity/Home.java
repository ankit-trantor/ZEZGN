package com.virtualdusk.zezgn.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.virtualdusk.zezgn.ChangePasswordFragment;
import com.virtualdusk.zezgn.Product.Product;
import com.virtualdusk.zezgn.Profile.EditUserProfileFragment;
import com.virtualdusk.zezgn.Profile.UserProfile;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Constant;
import com.virtualdusk.zezgn.Utilities.SaveRecords;
import com.virtualdusk.zezgn.Utilities.Utilities;
import com.virtualdusk.zezgn.activity.CategoryScreen.DesignDetailScreen;
import com.virtualdusk.zezgn.activity.HomeScreen.AllDesignsFragment;
import com.virtualdusk.zezgn.activity.MyAccount.MyAccountFragment;
import com.virtualdusk.zezgn.activity.MyAccount.OrderSummaryFragment;
import com.virtualdusk.zezgn.activity.PayInvoiceScreens.ThankyouFragment;
import com.virtualdusk.zezgn.activity.Products.ProductFragment;
import com.virtualdusk.zezgn.activity.Setting.SettingFragment;
import com.virtualdusk.zezgn.activity.Setting.Settings;
import com.virtualdusk.zezgn.activity.CategoryScreen.CategoryFragment;
import com.virtualdusk.zezgn.activity.Shipping.AddAddressFragment;
import com.virtualdusk.zezgn.activity.ShoopingCart.ShoppingCartActivity;
import com.virtualdusk.zezgn.activity.ShoopingCart.ShoppingCartFragment;
import com.virtualdusk.zezgn.activity.notification.NotificationScreenFragment;
import com.virtualdusk.zezgn.api.CartCounter;

/**
 * Created by Amit Sharma on 10/4/2016.
 */
public class Home extends AbstractActivity implements View.OnClickListener {

    private String TAG = Home.class.getSimpleName().toString();
    // private SearchView searchView;
    public static ImageView ivSetting;
    private ImageView ivHome, ivCategory, ivMain, ivNotification, ivProfile, ivCart;
    // public static RelativeLayout rlSearch;
    private RelativeLayout rlHome, rlNotification, rlProfile, rlCategory, rlProduct;
    private LinearLayout llBottom;
    public static SharedPreferences sharedPreferences;
    public static TextView tvTitle, tvCart;
    public int setting_back = 0;

    public static FloatingActionButton fab;
    FloatingActionButton fab1;
    FloatingActionButton fab2;
    FloatingActionButton fab3;
    CoordinatorLayout rootLayout;

    //Save the FAB's active status
    //false -> fab = close
    //true -> fab = open
    private boolean FAB_Status = false;

    //Animations
    Animation show_fab_1;
    Animation hide_fab_1;
    Animation show_fab_2;
    Animation hide_fab_2;
    Animation show_fab_3;
    Animation hide_fab_3;
    public static int CartCount;
    public static String strKey = "";
    public static String strMove = "";
    private boolean doubleBackToExitPressedOnce = false;
    private String mUserId;
    private Utilities mUtilities;
    private boolean is_Notification = false;
    private String strNotificationType = "";
    public static RelativeLayout RL_TopBar;
    public static boolean recreateActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN|WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.main_screen_);

        try {
            Intent i = getIntent();
            if (i != null) {
                // from thankyou page when user press "My Order" button. it will move to my orders page
                if (i.getExtras() != null) {
                    strMove = i.getExtras().getString("move", "");
                    is_Notification = i.getExtras().getBoolean(Constant.IS_NOTIFICATION, false);
                    Log.d(TAG, "onCreate: is_Notification " + is_Notification);
                    if (is_Notification) {
                        strNotificationType = i.getExtras().getString(Constant.NOTIFICATION_TYPE, "");
                        Log.d(TAG, "onCreate: strNotificationType " + strNotificationType);
                    }
                }


            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        rootLayout = (CoordinatorLayout) findViewById(R.id.cordinatorLayout);

        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvCart = (TextView) findViewById(R.id.tvCartCount);

        sharedPreferences = getAppSharedPreferences();
        mUserId = sharedPreferences.getString(getString(R.string.key_user_id), null);
        mUtilities = new Utilities();

        getNotificationList(mUserId);

        findRes();
        ivSetting.setVisibility(View.GONE);


        new UpdateFragments(Home.this);
        fabFunctionlity();
        tvTitle.setText("All DESIGNS");

        UpdateFragments.setFragment(getSupportFragmentManager().beginTransaction(), new HomeFragment(), R.id.frame);

        if (strMove.equalsIgnoreCase("order")) {

            setprofileTab("");
        } else if (strMove.equalsIgnoreCase("cart")) {

            setShoppingCartFragment();
        }else if(strMove.equalsIgnoreCase("home")){
            setHomeTab();
        }else if(strMove.equalsIgnoreCase("account")){
            ivSetting.setVisibility(View.GONE);
            setprofileTab("account");
        }

        if (is_Notification) {
            setNotifcationTab();
        }

    }

    private void getNotificationList(String user_id)
    {
        //mUtilities.showProgressDialog(Home.this, "Adding Design...");
        Retrofit restAdapter = CartCounter.retrofit;
        CartCounter registerApi = restAdapter.create(CartCounter.class);
        Call<JsonElement> call = registerApi.getCartCounter(user_id);
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                if(response.body()!=null){
                  //  mUtilities.hideProgressDialog();
                    parseResponse(response.body().toString());
                }
                else
                {}
            }

            @Override
            public void onFailure(Call<JsonElement> result, Throwable t) {
              //  mUtilities.hideProgressDialog();
                Log.e("onFailure: " ,""+result.toString());
            }
        });
    }

    private void parseResponse(String s) {
        try {
            JSONObject js  = new JSONObject(s);
            JSONObject coun = js.getJSONObject("response");
            String counter = coun.getString("data");
            int asd = Integer.parseInt(counter);
            setCartCount(asd);
            SaveRecords.saveIntegerToPreference(getString(R.string.key_cartcount),asd,Home.this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void fabFunctionlity() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (FAB_Status == false) {
                    //Display FAB menu
                    expandFAB();
                    FAB_Status = true;
                } else {
                    hideFAB();
                    FAB_Status = false;
                }
            }
        });


        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.hide();
                fab1.hide();
                fab2.hide();
                fab3.hide();
                hideFAB();
                FAB_Status = false;

                startActivity(new Intent(Home.this, UploadPicStyleActivity.class));
            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.hide();
                fab1.hide();
                fab2.hide();
                fab3.hide();
                hideFAB();
                FAB_Status = false;
                startActivity(new Intent(Home.this, UploadPicStyleActivity.class));

                Toast.makeText(getApplication(), "Floating Action Button 2", Toast.LENGTH_SHORT).show();
            }
        });

        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fab.hide();
                fab1.hide();
                fab2.hide();
                fab3.hide();
                hideFAB();
                FAB_Status = false;
                startActivity(new Intent(Home.this, UploadPicStyleActivity.class));

                Toast.makeText(getApplication(), "Floating Action Button 3", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void expandFAB() {

        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        layoutParams.rightMargin += (int) (fab1.getWidth() * 1.7);
        layoutParams.bottomMargin += (int) (fab1.getHeight() * 0.25);
        fab1.setLayoutParams(layoutParams);
        fab1.startAnimation(show_fab_1);
        fab1.setClickable(true);

        //Floating Action Button 2
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        layoutParams2.rightMargin += (int) (fab2.getWidth() * 1.5);
        layoutParams2.bottomMargin += (int) (fab2.getHeight() * 1.5);
        fab2.setLayoutParams(layoutParams2);
        fab2.startAnimation(show_fab_2);
        fab2.setClickable(true);

        //Floating Action Button 3
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
        layoutParams3.rightMargin += (int) (fab3.getWidth() * 0.25);
        layoutParams3.bottomMargin += (int) (fab3.getHeight() * 1.7);
        fab3.setLayoutParams(layoutParams3);
        fab3.startAnimation(show_fab_3);
        fab3.setClickable(true);
    }


    private void hideFAB() {

        //Floating Action Button 1
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fab1.getLayoutParams();
        layoutParams.rightMargin -= (int) (fab1.getWidth() * 1.7);
        layoutParams.bottomMargin -= (int) (fab1.getHeight() * 0.25);
        fab1.setLayoutParams(layoutParams);
        fab1.startAnimation(hide_fab_1);
        fab1.setClickable(false);

        //Floating Action Button 2
        FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) fab2.getLayoutParams();
        layoutParams2.rightMargin -= (int) (fab2.getWidth() * 1.5);
        layoutParams2.bottomMargin -= (int) (fab2.getHeight() * 1.5);
        fab2.setLayoutParams(layoutParams2);
        fab2.startAnimation(hide_fab_2);
        fab2.setClickable(false);

        //Floating Action Button 3
        FrameLayout.LayoutParams layoutParams3 = (FrameLayout.LayoutParams) fab3.getLayoutParams();
        layoutParams3.rightMargin -= (int) (fab3.getWidth() * 0.25);
        layoutParams3.bottomMargin -= (int) (fab3.getHeight() * 1.7);
        fab3.setLayoutParams(layoutParams3);
        fab3.startAnimation(hide_fab_3);
        fab3.setClickable(false);
    }

    @Override
    public void onClick(View view) {
//        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();

        switch (view.getId()) {
            case R.id.ivHome:


                setHomeTab();

                break;
            case R.id.ivCategory:


                setCategoryTab();

                break;
            case R.id.ivMain:

                RL_TopBar.setVisibility(View.VISIBLE);

                ivSetting.setVisibility(View.GONE);
                fab.hide();
                tvTitle.setText("CUSTOMIZE");

                llBottom.setVisibility(View.VISIBLE);

                ivHome.setImageResource(R.mipmap.home);
                ivCategory.setImageResource(R.mipmap.category);
                ivMain.setImageResource(R.mipmap.product_h);
                ivNotification.setImageResource(R.mipmap.bell);
                ivProfile.setImageResource(R.mipmap.user);

                //      rlSearch.setVisibility(View.VISIBLE);
                rlProduct.setBackgroundColor(getResources().getColor(R.color.white));

                rlHome.setBackgroundColor(getResources().getColor(R.color.app_background_color));
                rlCategory.setBackgroundColor(getResources().getColor(R.color.app_background_color));
                rlNotification.setBackgroundColor(getResources().getColor(R.color.app_background_color));
                rlProfile.setBackgroundColor(getResources().getColor(R.color.app_background_color));

                Bundle mBundle = new Bundle();
                mBundle.putString("key", "category");
                strKey = "category";
                ProductFragment mFragment = new ProductFragment();
                mFragment.setArguments(mBundle);


                UpdateFragments.setFragment(getSupportFragmentManager().beginTransaction(), mFragment, R.id.frame);

                break;
            case R.id.ivNotification:

                RL_TopBar.setVisibility(View.VISIBLE);

                if (mUserId.equalsIgnoreCase("-1")) {
                    mUtilities.showLoginDialog(Home.this, this.getString(R.string.alert_login_details));
                } else {
                    ivSetting.setVisibility(View.GONE);
                    fab.hide();

                    tvTitle.setText("NOTIFICATIONS");

                    llBottom.setVisibility(View.VISIBLE);

                    ivHome.setImageResource(R.mipmap.home);
                    ivCategory.setImageResource(R.mipmap.category);
                    ivMain.setImageResource(R.mipmap.product);
                    ivNotification.setImageResource(R.mipmap.bell_h);
                    ivProfile.setImageResource(R.mipmap.user);

                    rlHome.setBackgroundColor(getResources().getColor(R.color.app_background_color));
                    rlCategory.setBackgroundColor(getResources().getColor(R.color.app_background_color));
                    rlProduct.setBackgroundColor(getResources().getColor(R.color.app_background_color));
                    rlNotification.setBackgroundColor(getResources().getColor(R.color.white));
                    rlProfile.setBackgroundColor(getResources().getColor(R.color.app_background_color));
                    UpdateFragments.setFragment(getSupportFragmentManager().beginTransaction(), new NotificationScreenFragment(), R.id.frame);

                }
                break;
            case R.id.ivProfile:

                RL_TopBar.setVisibility(View.VISIBLE);
                if (mUserId.equalsIgnoreCase("-1")) {
                    mUtilities.showLoginDialog(Home.this, this.getString(R.string.alert_login_details));
                } else {
                    setprofileTab("");
                }

                break;
            case R.id.ivSetting:

                fab.hide();
                strKey = "setting";
                tvTitle.setText("SETTINGS");
                ivSetting.setVisibility(View.GONE);

                getSupportFragmentManager().beginTransaction().add(R.id.frame, new SettingFragment())
                        .addToBackStack(null).commit();
                break;
            case R.id.ivCart:

                setShoppingCartFragment();

                break;
        }
    }

    private void setCategoryTab() {
        RL_TopBar.setVisibility(View.VISIBLE);
        fab.hide();
        ivSetting.setVisibility(View.GONE);
        tvTitle.setText("CATEGORIES");

        llBottom.setVisibility(View.VISIBLE);

        rlCategory.setBackgroundColor(getResources().getColor(R.color.white));
        ivCategory.setImageResource(R.mipmap.category_h);

        ivHome.setImageResource(R.mipmap.home);
        ivMain.setImageResource(R.mipmap.product);
        ivNotification.setImageResource(R.mipmap.bell);
        ivProfile.setImageResource(R.mipmap.user);

        rlHome.setBackgroundColor(getResources().getColor(R.color.app_background_color));
        rlProduct.setBackgroundColor(getResources().getColor(R.color.app_background_color));
        rlNotification.setBackgroundColor(getResources().getColor(R.color.app_background_color));
        rlProfile.setBackgroundColor(getResources().getColor(R.color.app_background_color));

        UpdateFragments.setFragment(getSupportFragmentManager().beginTransaction(), new CategoryFragment(), R.id.frame);

    }

    private void setShoppingCartFragment() {
        Log.d(TAG, "setShoppingCartFragment: ");
       // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN|WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        RL_TopBar.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().add(R.id.frame, new ShoppingCartFragment()).addToBackStack(null).commit();
    }

    private void setHomeTab() {

        RL_TopBar.setVisibility(View.VISIBLE);
        // HomeFragment.isCallApi = true;
        //  fab.show();
        ivSetting.setVisibility(View.GONE);

        tvTitle.setText("All DESIGNS");

        llBottom.setVisibility(View.VISIBLE);

        rlHome.setBackgroundColor(getResources().getColor(R.color.white));

        rlCategory.setBackgroundColor(getResources().getColor(R.color.app_background_color));
        rlProduct.setBackgroundColor(getResources().getColor(R.color.app_background_color));
        rlNotification.setBackgroundColor(getResources().getColor(R.color.app_background_color));
        rlProfile.setBackgroundColor(getResources().getColor(R.color.app_background_color));

        ivHome.setImageResource(R.mipmap.home_h);

        ivCategory.setImageResource(R.mipmap.category);
        ivMain.setImageResource(R.mipmap.product);
        ivNotification.setImageResource(R.mipmap.bell);
        ivProfile.setImageResource(R.mipmap.user);
        //  rlSearch.setVisibility(View.VISIBLE);


        UpdateFragments.setFragment(getSupportFragmentManager().beginTransaction(), new HomeFragment(), R.id.frame);
    }

    private void setprofileTab(String arguments) {
        ivSetting.setVisibility(View.GONE);
        fab.hide();
        tvTitle.setText("");

        llBottom.setVisibility(View.VISIBLE);

        ivHome.setImageResource(R.mipmap.home);
        ivCategory.setImageResource(R.mipmap.category);
        ivMain.setImageResource(R.mipmap.product);
        ivNotification.setImageResource(R.mipmap.bell);
        ivProfile.setImageResource(R.mipmap.user_h);

        rlProfile.setBackgroundColor(getResources().getColor(R.color.white));
        rlHome.setBackgroundColor(getResources().getColor(R.color.app_background_color));
        rlCategory.setBackgroundColor(getResources().getColor(R.color.app_background_color));
        rlProduct.setBackgroundColor(getResources().getColor(R.color.app_background_color));
        rlNotification.setBackgroundColor(getResources().getColor(R.color.app_background_color));

        //   rlSearch.setVisibility(View.GONE);

        if (arguments.equalsIgnoreCase("account")) {

            Bundle mBundle = new Bundle();
            mBundle.putString("key","account");

            MyAccountFragment mFragment = new MyAccountFragment();
            mFragment.setArguments(mBundle);

            UpdateFragments.setFragment(getSupportFragmentManager().beginTransaction(), mFragment, R.id.frame);

        }else {
            UpdateFragments.setFragment(getSupportFragmentManager().beginTransaction(), new MyAccountFragment(), R.id.frame);
        }

    }

    private void setNotifcationTab() {
        ivSetting.setVisibility(View.GONE);
        fab.hide();
        tvTitle.setText("NOTIFICATIONS");

        llBottom.setVisibility(View.VISIBLE);

        ivHome.setImageResource(R.mipmap.home);
        ivCategory.setImageResource(R.mipmap.category);
        ivMain.setImageResource(R.mipmap.product);
        ivNotification.setImageResource(R.mipmap.bell_h);
        ivProfile.setImageResource(R.mipmap.user);

        rlProfile.setBackgroundColor(getResources().getColor(R.color.app_background_color));
        rlHome.setBackgroundColor(getResources().getColor(R.color.app_background_color));
        rlCategory.setBackgroundColor(getResources().getColor(R.color.app_background_color));
        rlProduct.setBackgroundColor(getResources().getColor(R.color.app_background_color));
        rlNotification.setBackgroundColor(getResources().getColor(R.color.white));

        //   rlSearch.setVisibility(View.GONE);

        Bundle mBundle = new Bundle();
        mBundle.putString("tab", strNotificationType);

        NotificationScreenFragment mNotificationScreenFragment = new NotificationScreenFragment();
        mNotificationScreenFragment.setArguments(mBundle);


        UpdateFragments.setFragment(getSupportFragmentManager().beginTransaction(), mNotificationScreenFragment, R.id.frame);
    }

    @Override
    protected void onResume() {
        super.onResume();
        FAB_Status = false;
      //
        CartCount = sharedPreferences.getInt(getString(R.string.key_cartcount), 0);
        Log.d(TAG, "CartCount " + CartCount);
        setCartCount(CartCount);
       /* if (CartCount != 0) {
            tvCart.setText(CartCount + "");
        } else {
            tvCart.setVisibility(View.GONE);
        }*/
        fabFunctionlity();

        if (recreateActivity) {
            recreateActivity = false;
           setHomeTab();
        }
    }


    private void findRes() {

        RL_TopBar = (RelativeLayout) findViewById(R.id.rlTopBar);
        rlHome = (RelativeLayout) findViewById(R.id.rlHome);
        rlCategory = (RelativeLayout) findViewById(R.id.rlCategory);
        rlProduct = (RelativeLayout) findViewById(R.id.rlMain);
        rlNotification = (RelativeLayout) findViewById(R.id.rlNotification);
        rlProfile = (RelativeLayout) findViewById(R.id.rlUserProfile);

        llBottom = (LinearLayout) findViewById(R.id.llBottom);

        //  searchView = (SearchView) findViewById(R.id.searchView1);
        ivHome = (ImageView) findViewById(R.id.ivHome);
        ivCategory = (ImageView) findViewById(R.id.ivCategory);
        ivMain = (ImageView) findViewById(R.id.ivMain);
        ivNotification = (ImageView) findViewById(R.id.ivNotification);
        ivProfile = (ImageView) findViewById(R.id.ivProfile);

        ivCart = (ImageView) findViewById(R.id.ivCart);
        ivSetting = (ImageView) findViewById(R.id.ivSetting);

        //  rlSearch = (RelativeLayout) findViewById(R.id.rlSearch);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab1 = (FloatingActionButton) findViewById(R.id.fab_1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab_2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab_3);

        //Animations
        show_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_show);
        hide_fab_1 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab1_hide);
        show_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_show);
        hide_fab_2 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab2_hide);
        show_fab_3 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab3_show);
        hide_fab_3 = AnimationUtils.loadAnimation(getApplication(), R.anim.fab3_hide);

    }

    public void values() {
        ivSetting.setVisibility(View.GONE);
    }

    public void addDetailScreen(Bundle bundle) {
//        Bundle bundle = new Bundle();
//        bundle.putString("key_category_id", category_id);

        DesignDetailScreen designDetailScreen = new DesignDetailScreen();
        designDetailScreen.setArguments(bundle);

        UpdateFragments.setFragment(getSupportFragmentManager().beginTransaction(), designDetailScreen, R.id.frame);

    }


    public void setTitle(String title) {

        if (tvTitle != null) {
            tvTitle.setText(title);
        }

    }

    public void showHomeScreen() {
        UpdateFragments.setFragment(getSupportFragmentManager().beginTransaction(), new HomeFragment(), R.id.frame);

    }

    public void setSettingBack(int back) {
        Log.e(TAG, "setSettingBack: " + back);
        setting_back = back;
        ivSetting.setImageResource(R.mipmap.left_arrow);
        //tvTitle.setText(title);

    }

    public void setCartCount(int count) {

        Log.d(TAG, " count " + count);
        CartCount = count;
        if (tvCart != null && count > 0) {

            tvCart.setText(CartCount + "");
            tvCart.setVisibility(View.VISIBLE);
        } else {
            tvCart.setVisibility(View.GONE);
            Log.d(TAG, " tvCart null");
        }


    }


    @Override
    public void onBackPressed() {


        Fragment f = getSupportFragmentManager().findFragmentById(R.id.frame);

//         if(f instanceof ProductFragment && strKey.equalsIgnoreCase("category")){
//             super.onBackPressed();
//        }else

        if (f instanceof ProductFragment && strKey.equalsIgnoreCase("fav")) {
            tvTitle.setText("");
            ivSetting.setVisibility(View.VISIBLE);
            super.onBackPressed();
        } else if (f instanceof OrderSummaryFragment && strKey.equalsIgnoreCase("fav")) {
            tvTitle.setText("");
            ivSetting.setVisibility(View.VISIBLE);
            super.onBackPressed();
        } else if (f instanceof HomeFragment && strKey.equalsIgnoreCase("cat")) {
            tvTitle.setText("CATEGORIES");

            super.onBackPressed();
        }else if (f instanceof DesignDetailScreen && strKey.equalsIgnoreCase("cat")) {
            tvTitle.setText("CATEGORIES");
            //super.onBackPressed();
            setCategoryTab();
        }
        else if (f instanceof AddAddressFragment && strKey.equalsIgnoreCase("add_address_f")) {
            tvTitle.setText("MY PROFILE");

            super.onBackPressed();
        } else if (f instanceof UserProfile && strKey.equalsIgnoreCase("my_acc")) {
            ivSetting.setVisibility(View.VISIBLE);
            tvTitle.setText("");
            super.onBackPressed();
        } else if (f instanceof EditUserProfileFragment && strKey.equalsIgnoreCase("edit_profile")) {
            strKey = "my_acc";
            tvTitle.setText("MY PROFILE");
            super.onBackPressed();
        } else if (f instanceof SettingFragment && strKey.equalsIgnoreCase("setting")) {

            tvTitle.setText("");
            ivSetting.setVisibility(View.VISIBLE);
            super.onBackPressed();
        } else if (f instanceof ChangePasswordFragment && strKey.equalsIgnoreCase("change_pass")) {
            strKey = "setting";
            tvTitle.setText("SETTING");
            ivSetting.setVisibility(View.GONE);
            super.onBackPressed();
        } else if (f instanceof ShoppingCartFragment) {

            RL_TopBar.setVisibility(View.VISIBLE);
            super.onBackPressed();
        } else if (f instanceof ThankyouFragment) {
            Intent intent = new Intent(this, Home.class);
            intent.putExtra("move", "");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else if (f instanceof CategoryFragment && strKey.equalsIgnoreCase("product")) {
            tvTitle.setText("CUSTOMIZE");
            super.onBackPressed();
        } else if (f instanceof HomeFragment || f instanceof CategoryFragment ||
                f instanceof NotificationScreenFragment || f instanceof MyAccountFragment ||
                (f instanceof ProductFragment && strKey.equalsIgnoreCase("category")) ||
                (f instanceof ProductFragment && strKey.equalsIgnoreCase("product"))) {


            if (doubleBackToExitPressedOnce) {
                Home.this.finish();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);

        } else {

            super.onBackPressed();


        }


    }
}
