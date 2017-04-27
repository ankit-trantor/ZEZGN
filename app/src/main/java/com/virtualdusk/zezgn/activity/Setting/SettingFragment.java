package com.virtualdusk.zezgn.activity.Setting;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.virtualdusk.zezgn.ChangePasswordFragment;
import com.virtualdusk.zezgn.Model.UserInfo;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Constant;
import com.virtualdusk.zezgn.Utilities.SaveRecords;
import com.virtualdusk.zezgn.Utilities.Utilities;
import com.virtualdusk.zezgn.activity.ChangePassword;
import com.virtualdusk.zezgn.activity.Home;
import com.virtualdusk.zezgn.activity.LoginActivity;
import com.virtualdusk.zezgn.activity.SettingActivity;
import com.virtualdusk.zezgn.api.LogOutApi;
import com.virtualdusk.zezgn.api.PushNotificationApi;
import com.virtualdusk.zezgn.utils.ConnectionDetector;
import com.virtualdusk.zezgn.utils.helper.AppUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends Fragment implements View.OnClickListener , com.virtualdusk.zezgn.InterfaceClasses.DialogInterface{
    private String TAG = SettingFragment.class.getSimpleName().toString();
    private RelativeLayout rlChangePass, rlLogOut;

    private Switch aSwitchPushNotify;
    private String mIsPushNotify, mNotificationStatus;
    public SharedPreferences sharedPreferences;
    private String mUserId;
    private TextView tvNotificationStatus;
    private Utilities mUtilities;

    private com.virtualdusk.zezgn.InterfaceClasses.DialogInterface mDialogInterface;
    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.settings_screen, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDialogInterface = this;
        sharedPreferences = getActivity().getSharedPreferences(AppUtils.PREF_NAME, Context.MODE_PRIVATE);
        mUserId = sharedPreferences.getString(getString(R.string.key_user_id), null);
        mNotificationStatus = sharedPreferences.getString(getString(R.string.key_notification_on), null);
        tvNotificationStatus = (TextView)view.findViewById(R.id.tvNotificationStatus);
        rlChangePass = (RelativeLayout) view.findViewById(R.id.rlChangePass);
        rlLogOut = (RelativeLayout) view.findViewById(R.id.rlLogOut);

        mUtilities = new Utilities();

        rlLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String msg = getString(R.string.log_out_msg);
                logOutDialog(msg);
            }
        });


        rlChangePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Home.tvTitle.setText("CHANGE PASSWORD");
                Home.strKey = "change_pass";
                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame, new ChangePasswordFragment())
                        .addToBackStack(null).commit();
                //startActivity(new Intent(getActivity(), ChangePassword.class));
            }
        });
        aSwitchPushNotify = (Switch) view.findViewById(R.id.switchPushNotification);
        view.findViewById(R.id.ivSetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish();
            }
        });


        if (mNotificationStatus != null) {
            if (mNotificationStatus.equals("false")) {
                aSwitchPushNotify.setOnCheckedChangeListener (null);
                mNotificationStatus = "false";
                aSwitchPushNotify.setChecked(false);
                setSwitchListner();
                tvNotificationStatus.setText(getActivity().getResources().getString(R.string.enable_sending_push_notifications_to_devices));
            } else {
                aSwitchPushNotify.setOnCheckedChangeListener (null);
                aSwitchPushNotify.setChecked(true);
                mNotificationStatus = "true";
                tvNotificationStatus.setText(getActivity().getResources().getString(R.string.disable_sending_push_notifications_to_devices));

                setSwitchListner();
            }
        }
    }

    private void setSwitchListner() {
        aSwitchPushNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean bChecked) {
                if (bChecked) {
                    mIsPushNotify = "1";
                    mNotificationStatus = "true";
                    if (new ConnectionDetector(getActivity()).isConnectingToInternet()) {

                        setPushNotification();
                    } else {
                        Toast.makeText(getActivity(),getString(R.string.no_internet),Toast.LENGTH_LONG).show();
                    }

                    tvNotificationStatus.setText(getActivity().getResources().getString(R.string.disable_sending_push_notifications_to_devices));
                } else {
                    mNotificationStatus = "false";
                    mIsPushNotify = "0";
                    if (new ConnectionDetector(getActivity()).isConnectingToInternet()) {

                        setPushNotification();
                    } else {
                        Toast.makeText(getActivity(),getString(R.string.no_internet),Toast.LENGTH_LONG).show();
                    }
                    tvNotificationStatus.setText(getActivity().getResources().getString(R.string.enable_sending_push_notifications_to_devices));
                }
            }
        });
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.rlChangePass:

                break;

//            case R.id.rlLogOut:
//                show("Logout");
//                LogOutTask();
//                break;

        }
    }

    private void logOutDialog(String msg) {

        mUtilities.showCancelableDialog(getActivity(),"Are you sure you want to logout?","Logout",mDialogInterface,"","logout");
//
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
//        alertDialog.setTitle("Alert");
//        alertDialog.setMessage("Are you sure you want to Logout?");
//        alertDialog.setPositiveButton("YES",
//                new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                        if (new ConnectionDetector(getActivity()).isConnectingToInternet()) {
//                            LogOutTask();
//                        } else {
//                            Toast.makeText(getActivity(), "Unable to connect to the Internet.", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//        alertDialog.setNeutralButton("NO",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                });
//        alertDialog.show();
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
                FacebookSdk.sdkInitialize(getActivity());
                LoginManager.getInstance().logOut();

                String STR_FCM_TOKEN = sharedPreferences.getString(Constant.FCM_TOKEN, "");

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();

                SaveRecords.saveToPreference(Constant.FCM_TOKEN, STR_FCM_TOKEN, getActivity());

                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
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
                if(mNotificationStatus.equalsIgnoreCase("true")){
                    show("Notifications turned on");
                }else{
                    show("Notifications turned off");
                }

            } else {
                show(message);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void show(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Home.ivSetting.setVisibility(View.GONE);
        Home.tvTitle.setText("SETTINGS");

    }

    @Override
    public void onCancelButtonClick(String DialogName, String Message) {

    }

    @Override
    public void onOkButtonClick(String DialogName, String Message) {

        if(Message.equalsIgnoreCase("logout")){
            if (new ConnectionDetector(getActivity()).isConnectingToInternet()) {
                LogOutTask();
            } else {
                Toast.makeText(getActivity(), "Unable to connect to the Internet.", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
