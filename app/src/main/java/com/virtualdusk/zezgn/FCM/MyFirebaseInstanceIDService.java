package com.virtualdusk.zezgn.FCM;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import com.virtualdusk.zezgn.Utilities.Constant;
import com.virtualdusk.zezgn.Utilities.SaveRecords;

/**
 * Created by bhart.gupta on 30-Nov-16.
 */

public class MyFirebaseInstanceIDService  extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    public static String refreshedToken = "";

    @Override
    public void onTokenRefresh() {

        //Getting registration token
         refreshedToken = FirebaseInstanceId.getInstance().getToken();


        //Displaying token on logcat
        Log.d(TAG, "Refreshed token: " + refreshedToken);

    }

    private void sendRegistrationToServer(String token) {
        //You can implement this method to store the token on your server
        //Not required for current project
    }
}