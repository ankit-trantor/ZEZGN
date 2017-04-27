package com.virtualdusk.zezgn.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.virtualdusk.zezgn.api.ForgotPasswordVerifyApi;
import com.virtualdusk.zezgn.api.OTPVerifyApi;
import com.virtualdusk.zezgn.utils.ConnectionDetector;

/**
 * Created by Amit Sharma on 9/29/2016.
 */
public class ForgotPassword extends AbstractActivity  implements View.OnClickListener{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
//        getActionBar().hide();
        progress_dialog = new ProgressDialog(ForgotPassword.this, R.style.MyTheme);
        progress_dialog.setCancelable(false);
        progress_dialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.my_progress_indeterminate));
        findRes();
    }
    private ProgressDialog progress_dialog;

    private TextView btnSubmit;
    private String mMobileNumber,mUserId;
    private EditText edMobileNumber;
    private void findRes() {
        edMobileNumber=(EditText)findViewById(R.id.edMobileNumber);
        btnSubmit=(TextView)findViewById(R.id.btnSubmit);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.btnSubmit:
                onSubmitClick();

                break;
            case R.id.ivBack:
            onBackPressed();

                break;

        }
    }

    private void onSubmitClick() {

        mMobileNumber=edMobileNumber.getText().toString();
    if (mMobileNumber.length()==0)
    {
        show("Please enter Mobile Number");

    }
        else
    {

        if (new ConnectionDetector(this).isConnectingToInternet()) {
            progress_dialog.show();
            forgotPasswordRetrofit();
        } else {
            Toast.makeText(this,getString(R.string.no_internet),Toast.LENGTH_LONG).show();
        }


    }
    }

    private void forgotPasswordRetrofit() {
        //Creating an object of our api interface

        Retrofit restAdapter = ForgotPasswordVerifyApi.retrofit;
        ForgotPasswordVerifyApi registerApi = restAdapter.create(ForgotPasswordVerifyApi.class);

        Call<JsonElement> call = registerApi.getOTPCodeViaJSON(getForgotPasswordParams());

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.e(TAG, "onResponse: " + response.body());
                progress_dialog.dismiss();
                parseResponse_ForgotPassword(response.body().toString());
            }

            @Override
            public void onFailure(Call<JsonElement> result, Throwable t) {
                Log.e(TAG, "onFailure: " + result.toString());
            }
        });



    }
    private static final String TAG = ForgotPassword.class.getSimpleName().toString();

    private UserInfo getForgotPasswordParams()
    {
        UserInfo signInData=new UserInfo();
        signInData.setContact_no(mMobileNumber);


        Map<String, String> paramsJsonObject = new ArrayMap<>();



        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(paramsJsonObject)).toString());


        return signInData;
    }


    private void parseResponse_ForgotPassword(String response)
    {
         String  data_;

        try {

            JSONObject jsnon = new JSONObject(response);
            String response1 = jsnon.getString("response");
            JSONObject jsonObject=new JSONObject(response1);
            String status = jsonObject.getString("code");
            String message = jsonObject.getString("message");


            if (status.equals("200")) {
                data_ = jsonObject.getString("data");
                show(message);
                JSONObject data = new JSONObject(data_);

                String confirmation_code=data.getString("confirmation_code");
                String id=data.getString("id");
            //    SaveRecords.saveToPreference(getString(R.string.key_user_id),id,ForgotPassword.this);

//
                Intent intent=new Intent(ForgotPassword.this,ForgotChangePassword.class);
                intent.putExtra("MOBILE",mMobileNumber);
                startActivity(intent);



            }
            else {
                show(message);

            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

}
