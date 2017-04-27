package com.virtualdusk.zezgn.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonElement;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.virtualdusk.zezgn.Model.UserInfo;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.SaveRecords;
import com.virtualdusk.zezgn.api.OTPVerifyApi;

/**
 * Created by Amit Sharma on 9/29/2016.
 */
public class OTPVerifyActivity extends AbstractActivity implements View.OnClickListener {

    private ProgressDialog progress_dialog;
    private TextView btnSubmit;
    private EditText edOTPVerificationCode;
    private String mOTPCode, mUserId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_otp);

        Intent i = this.getIntent();
        if(i != null){
            mUserId =  i.getExtras().getString("user_id","");
        }

        progress_dialog = new ProgressDialog(OTPVerifyActivity.this, R.style.MyTheme);
        progress_dialog.setCancelable(false);
        progress_dialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.my_progress_indeterminate));

        findRes();
    }



    private void findRes() {
        btnSubmit = (TextView) findViewById(R.id.btnSignIn);
        edOTPVerificationCode = (EditText) findViewById(R.id.edOtp);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSubmit:

                onSubmission();
                break;

            case R.id.ivBack:

                onBackPressed();
                break;

        }
    }


    private void onSubmission() {
        mOTPCode = edOTPVerificationCode.getText().toString();

        if (mOTPCode.length() == 0) {
            show("Please Enter OTP Code");
        } else {
            progress_dialog.show();

            fetchViaRetrofit();
        }
    }

    private static final String TAG = "OTP_VERIFY";

    private void fetchViaRetrofit() {
        //Creating an object of our api interface

        Retrofit restAdapter = OTPVerifyApi.retrofit;
        OTPVerifyApi registerApi = restAdapter.create(OTPVerifyApi.class);

        Call<JsonElement> call = registerApi.getOTPCodeViaJSON(getSignInJSON());

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.e(TAG, "onResponse: " + response.body());

                progress_dialog.dismiss();
//                startActivity(new Intent(OTPVerifyActivity.this,AddPhoto.class));
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
                Log.d(TAG, "parseResponse: mUserId " + mUserId);
                SaveRecords.saveToPreference(getString(R.string.key_user_id), mUserId, OTPVerifyActivity.this);
                startActivity(new Intent(OTPVerifyActivity.this, AddPhoto.class));
                OTPVerifyActivity.this.finish();

            } else {

                show("Sorry, this OTP is not correct");
//                String errors = jsonObject.getString("errors");
//                JSONObject jsonObject1=new JSONObject(errors);
//                show(jsonObject1.getString("error_messages"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private SharedPreferences sharedPreferences;

    private UserInfo getSignInJSON() {
        sharedPreferences = this.getAppSharedPreferences();
      //  mUserId = sharedPreferences.getString(getString(R.string.key_user_id), null);

        UserInfo signInData = new UserInfo();
        signInData.setConfirmation_code(mOTPCode);
        signInData.setUser_id(mUserId);

        Map<String, String> paramsJsonObject = new ArrayMap<>();
        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(paramsJsonObject)).toString());


        return signInData;
    }


}
