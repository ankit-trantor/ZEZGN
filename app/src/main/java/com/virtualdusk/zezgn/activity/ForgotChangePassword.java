package com.virtualdusk.zezgn.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.virtualdusk.zezgn.Model.UserInfo;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.SaveRecords;
import com.virtualdusk.zezgn.Utilities.Utilities;
import com.virtualdusk.zezgn.api.ChangePasswordVerifyApi;
import com.virtualdusk.zezgn.api.ForgotPasswordVerifyApi;
import com.virtualdusk.zezgn.api.ResetPasswordVerifyApi;
import com.virtualdusk.zezgn.utils.ConnectionDetector;

/**
 * Created by Amit Sharma on 9/29/2016.
 */
public class ForgotChangePassword extends AbstractActivity implements View.OnClickListener {

    private ProgressDialog progress_dialog;

    private EditText edPassword, edConfirmPassword, edOTP;
    private String mPassword, mConfirmPassword, mUserId;
    private ImageView ivBack;
    private TextView btnSubmit;
    private TextView tvResendOTP;
    private String mMobileNumber;
    private Utilities mUtilities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_change_password);

        progress_dialog = new ProgressDialog(ForgotChangePassword.this, R.style.MyTheme);
        progress_dialog.setCancelable(false);
        progress_dialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.my_progress_indeterminate));

        findRes();
        mUtilities = new Utilities();
        Intent intent = getIntent();
        try {
            mMobileNumber = intent.getStringExtra("MOBILE");

        } catch (Exception e) {
            e.printStackTrace();
        }

        sharedPreferences = this.getAppSharedPreferences();
        mUserId = sharedPreferences.getString(getString(R.string.key_user_id), null);
    }


    private void findRes() {

        btnSubmit = (TextView) findViewById(R.id.btnSubmit);
        tvResendOTP = (TextView) findViewById(R.id.tvResendOTP);
        edPassword = (EditText) findViewById(R.id.edPassword);
        ivBack = (ImageView) findViewById(R.id.ivBack);
        edConfirmPassword = (EditText) findViewById(R.id.edConfirmPassword);
        edOTP = (EditText) findViewById(R.id.edOTP);
        edPassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                mPassword.setError(null);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().contains(" ")){
                    show("Space is not allowed in password");
                    edPassword.setText("");
                }
                if (s.length() == 20) {
                    show("Password should be between 6-20 characters in length");
                } else {

                }
            }
        });

        edConfirmPassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                mPassword.setError(null);

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().contains(" ")){
                    show("Space is not allowed in password");
                    edConfirmPassword.setText("");
                }
                if (s.length() == 20) {
                    show("Confirm Password should be between 6-20 characters in length");
                } else {
//                    show("not excceds");

                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSubmit:
                onSubmissionClick();
                break;
            case R.id.ivBack:
                onBackPressed();
                break;

            case R.id.tvResendOTP:
                forgotPasswordTask();
                break;


        }
    }

    private String mOTPCode;

    private void onSubmissionClick() {
        mOTPCode = edOTP.getText().toString();
        mPassword = edPassword.getText().toString();
        mConfirmPassword = edConfirmPassword.getText().toString();


        if (mOTPCode.length() == 0) {
            show("Please enter OTP Code");
        } else if (mPassword.length() == 0) {
            show("Please enter Old Password");
        } else if (mPassword.length() < 6) {
            show("Password should be between 6-20 characters in length");
        } else if (mConfirmPassword.length() == 0) {
            show("Please enter Confirm Password");
        } else if (mConfirmPassword.length() < 6) {
            show("Confirm Password should be between 6-20 characters in length");
        } else if (!edPassword.getText().toString().equals(edConfirmPassword.getText().toString())) {
            show(getString(R.string.pass_not_match_essential));
        } else {
            if (new ConnectionDetector(ForgotChangePassword.this).isConnectingToInternet()) {
                progress_dialog.show();
                resetPasswordTask();
            } else {
                show("Unable to connect to the Internet.");
            }
        }


    }


    private void resetPasswordTask() {
        //Creating an object of our api interface

        Retrofit restAdapter = ResetPasswordVerifyApi.retrofit;
        ResetPasswordVerifyApi registerApi = restAdapter.create(ResetPasswordVerifyApi.class);

        Call<JsonElement> call = registerApi.getOTPCodeViaJSON(getResetPasswordParms());


        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.e(TAG, "onResponse: " + response.body());

                progress_dialog.dismiss();

                parseResponse(response.body().toString());
            }

            @Override
            public void onFailure(Call<JsonElement> result, Throwable t) {
                Log.e(TAG, "onFailure: " + result.toString());
            }
        });


    }

    private void parseResponse(String response) {
        try {

            JSONObject jsnon = new JSONObject(response);
            String response1 = jsnon.getString("response");
            JSONObject jsonObject = new JSONObject(response1);
            String status = jsonObject.getString("code");
            String message = jsonObject.getString("message");

            if (status.equals("200")) {
                show(message);

              //  startActivity(new Intent(ForgotChangePassword.this, LoginActivity.class));
                Intent i = new Intent(ForgotChangePassword.this, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);



            } else {

                String errors = jsonObject.getString("errors");
                JSONObject jsonObject1 = new JSONObject(errors);

                show(jsonObject1.getString("error_messages"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private static final String TAG = ForgotChangePassword.class.getSimpleName().toString();

    private SharedPreferences sharedPreferences;


    private UserInfo getResetPasswordParms() {
        UserInfo signInData = new UserInfo();
        signInData.setConfirmation_code(mOTPCode);
        signInData.setContact_no(mMobileNumber);
        signInData.setPassword(mPassword);

//        “contact_no” : 8696251627,
//        “confirmation_code” : 580607,
//        “password” : 1234567


        Map<String, String> paramsJsonObject = new ArrayMap<>();


        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), (new JSONObject(paramsJsonObject)).toString());


        return signInData;
    }

    private void parseResponse_ForgotPassword(String response) {
        try {

            JSONObject jsnon = new JSONObject(response);
            String response1 = jsnon.getString("response");
            JSONObject jsonObject = new JSONObject(response1);
            String status = jsonObject.getString("code");
            String message = jsonObject.getString("message");
            String data_ = jsonObject.getString("data");
            if (status.equals("200")) {
                show(message);
                JSONObject data = new JSONObject(data_);

//                String confirmation_code=data.getString("confirmation_code");
//                String id=data.getString("id");
//                SaveRecords.saveToPreference(getString(R.string.key_user_id),id,ForgotPassword.this);
//
////
//                Intent intent=new Intent(ForgotPassword.this,ForgotChangePassword.class);
//                intent.putExtra("MOBILE",mMobileNumber);
//                startActivity(intent);


            } else {
                show(message);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void forgotPasswordTask() {
        //Creating an object of our api interface

        mUtilities.showProgressDialog(ForgotChangePassword.this,"Resending OTP. Please wait...");

        Retrofit restAdapter = ForgotPasswordVerifyApi.retrofit;
        ForgotPasswordVerifyApi registerApi = restAdapter.create(ForgotPasswordVerifyApi.class);

        Call<JsonElement> call = registerApi.getOTPCodeViaJSON(getForgotPasswordParams());

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.e(TAG, "onResponse: " + response.body());
                mUtilities.hideProgressDialog();
                parseResponse_ForgotPassword(response.body().toString());
            }

            @Override
            public void onFailure(Call<JsonElement> result, Throwable t) {
                mUtilities.hideProgressDialog();
                Log.e(TAG, "onFailure: " + result.toString());
            }
        });


    }


    private UserInfo getForgotPasswordParams() {
        UserInfo signInData = new UserInfo();
        signInData.setContact_no(mMobileNumber);


        Map<String, String> paramsJsonObject = new ArrayMap<>();


        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(paramsJsonObject)).toString());


        return signInData;
    }


}
