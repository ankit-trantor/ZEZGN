package com.virtualdusk.zezgn.activity.Setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.virtualdusk.zezgn.Model.UserInfo;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.SaveRecords;
import com.virtualdusk.zezgn.activity.ChangePassword;
import com.virtualdusk.zezgn.activity.Home;
import com.virtualdusk.zezgn.activity.LoginActivity;
import com.virtualdusk.zezgn.api.LogOutApi;
import com.virtualdusk.zezgn.api.PushNotificationApi;
import com.virtualdusk.zezgn.utils.ConnectionDetector;

/**
 * Created by Amit Sharma on 10/5/2016.
 */
public class Settings extends Fragment implements View.OnClickListener {

    private String TAG = Settings.class.getSimpleName().toString();
    private RelativeLayout rlChangePass, rlLogOut;

    private Switch aSwitchPushNotify;
    private String mIsPushNotify, mNotificationStatus;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.settings, container, false);

        mUserId = Home.sharedPreferences.getString(getString(R.string.key_user_id), null);
        mNotificationStatus = Home.sharedPreferences.getString(getString(R.string.key_notification_on), null);
        Log.e(TAG, "onCreateView:mNotificationStatus: " + mNotificationStatus);

        rlChangePass = (RelativeLayout) v.findViewById(R.id.rlChangePass);
        rlLogOut = (RelativeLayout) v.findViewById(R.id.rlLogOut);

        ((Home)getActivity()).values();
//        ((Home)v.getContext()).setSettingBack(1);



        rlLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             String  msg = getString(R.string.log_out_msg);
logOutDialog(msg);
            }
        });


        rlChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getActivity(), ChangePassword.class));
            }
        });


        aSwitchPushNotify = (Switch) v.findViewById(R.id.switchPushNotification);

        aSwitchPushNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    mIsPushNotify = "1";
                    mNotificationStatus = "true";
                    setPushNotification();

                } else {
                    mNotificationStatus = "false";
                    mIsPushNotify = "0";
                    setPushNotification();

                }
            }
        });

        if (mNotificationStatus!=null)
        {
            if (mNotificationStatus.equals("false")) {
                mNotificationStatus = "false";
                aSwitchPushNotify.setChecked(false);
            } else {
                aSwitchPushNotify.setChecked(true);
                mNotificationStatus = "true";
            }
        }


        return v;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.rlChangePass:

                break;

            case R.id.rlLogOut:
                show("Logout");
                LogOutTask();
                break;

        }
    }
    private void logOutDialog(String msg) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Are you sure you want to Logout");
        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        if (new ConnectionDetector(getActivity()).isConnectingToInternet()) {
                            LogOutTask();
                        } else {
                            Toast.makeText(getActivity(),"Unable to connect to the Internet.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        alertDialog.setNeutralButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }


    private void setPushNotification() {
        //Creating an object of our api interface

        Retrofit restAdapter = PushNotificationApi.retrofit;
        PushNotificationApi registerApi = restAdapter.create(PushNotificationApi.class);

        Call<JsonElement> call = registerApi.setPushNotifyViaJSON(getNotificationParams());

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.e(TAG, "onResponse: " + response.body());
                parseResponse_Notification(response.body().toString());
            }

            @Override
            public void onFailure(Call<JsonElement> result, Throwable t) {
                Log.e(TAG, "onFailure: " + result.toString());
            }
        });


    }


    private void LogOutTask() {
        //Creating an object of our api interface

        Retrofit restAdapter = LogOutApi.retrofit;
        LogOutApi registerApi = restAdapter.create(LogOutApi.class);

        Call<JsonElement> call = registerApi.logOutViaJSON(getLogOutParams());

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.e(TAG, "onResponse: " + response.body());
                parseResponse_logOut(response.body().toString());
            }

            @Override
            public void onFailure(Call<JsonElement> result, Throwable t) {
                Log.e(TAG, "onFailure: " + result.toString());
            }
        });


    }


    private String mUserId = "";

    private UserInfo getLogOutParams() {

        UserInfo signInData = new UserInfo();
        signInData.setUser_id(mUserId);


        return signInData;
    }

    private UserInfo getNotificationParams() {

        UserInfo signInData = new UserInfo();
        signInData.setUser_id(mUserId);
        signInData.setNotification_on(mIsPushNotify);


        return signInData;
    }

    private void parseResponse_logOut(String response) {

        try {


            JSONObject jsnon = new JSONObject(response);
            String response1 = jsnon.getString("response");
            JSONObject jsonObject = new JSONObject(response1);
            String status = jsonObject.getString("code");
            String message = jsonObject.getString("message");
            if (status.equals("200")) {
                Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();


                SaveRecords.saveToPreference(getString(R.string.key_user_id), null, getActivity());
                startActivity(new Intent(getActivity(), LoginActivity.class));
            } else {
                show(message);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void parseResponse_Notification(String response) {

        try {


            JSONObject jsnon = new JSONObject(response);
            String response1 = jsnon.getString("response");
            JSONObject jsonObject = new JSONObject(response1);
            String status = jsonObject.getString("code");
            String message = jsonObject.getString("message");
            if (status.equals("200")) {

                Log.e(TAG, "parseResponse_Notification: " + mNotificationStatus);

                SaveRecords.saveToPreference(getString(R.string.key_notification_on), mNotificationStatus, getActivity());
            } else {
                show(message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void show(String status) {
        Toast.makeText(getActivity(), "" + status, Toast.LENGTH_SHORT).show();
    }


}
