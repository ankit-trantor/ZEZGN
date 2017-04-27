package com.virtualdusk.zezgn.activity;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;


import me.leolin.shortcutbadger.ShortcutBadger;
import com.virtualdusk.zezgn.FCM.MyFirebaseInstanceIDService;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Constant;
import com.virtualdusk.zezgn.utils.ConnectionDetector;

public class SplashActivity extends AbstractActivity {

    private static final int STOPSPLASH = 0;
    private static final int SPLASHTIME = 3000;

    String lat, lon;
    SharedPreferences sharedPreferences;
    String mUserId = null, country_id;
    private String strNotificationType = "", strNotificationBadge = "";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash);

        sharedPreferences = this.getAppSharedPreferences();
        mUserId = sharedPreferences.getString(getString(R.string.key_user_id), null);
        Log.d("SplashActivity", "mUserId: " + mUserId);

        if (getIntent().getExtras() != null) {
            Log.d("SplashActivity", "onCreate type: " + getIntent().getExtras().getString("type"));
            Log.d("SplashActivity", "onCreate badge: " + getIntent().getExtras().getString("badge"));
            strNotificationType = getIntent().getExtras().getString("type", "");
            strNotificationBadge = getIntent().getExtras().getString("badge", "");

//            try {
//                ShortcutBadger.removeCount(SplashActivity.this);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }

        Message msg = new Message();
        msg.what = STOPSPLASH;
        splashHandler.sendMessageDelayed(msg, SPLASHTIME);

    }

    // Handler for Splashscreen
    private Handler splashHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case STOPSPLASH:


                    if (new ConnectionDetector(SplashActivity.this).isConnectingToInternet()) {
                        if (mUserId != null) {
                            Intent i = new Intent(SplashActivity.this, Home.class);//.class);
                            i.putExtra("move", "");
                            if (!strNotificationType.isEmpty()) {
                                i.putExtra(Constant.NOTIFICATION_TYPE, strNotificationType);
                                i.putExtra(Constant.IS_NOTIFICATION, true);
                                Home.recreateActivity = false;
                            }

                            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                            startActivity(i);
                            finish();
                        } else {
                            Intent i = new Intent(SplashActivity.this, LoginActivity.class);//.class);
                            i.putExtra("move", "");
                            overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
                            startActivity(i);
                            finish();

                        }
                    } else {
                        Toast.makeText(getApplicationContext(), getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                    }

            }

            super.handleMessage(msg);
        }
    };


}


