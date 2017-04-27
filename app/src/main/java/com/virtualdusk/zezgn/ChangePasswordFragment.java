package com.virtualdusk.zezgn;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.virtualdusk.zezgn.Utilities.SaveRecords;
import com.virtualdusk.zezgn.activity.ChangePassword;
import com.virtualdusk.zezgn.activity.Home;
import com.virtualdusk.zezgn.activity.LoginActivity;
import com.virtualdusk.zezgn.api.ChangePasswordVerifyApi;
import com.virtualdusk.zezgn.utils.ConnectionDetector;
import com.virtualdusk.zezgn.utils.helper.AppUtils;



/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePasswordFragment extends Fragment implements View.OnClickListener {

    private EditText edPassword, edConfirmPassword, edNewPassword;
    private String mPassword, mConfirmPassword, mUserId, mNewPassword;
    private ImageView ivBack;
    private TextView btnSubmit;

    private ProgressDialog progress_dialog;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(com.virtualdusk.zezgn.R.layout.change_password, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findRes(view);
        sharedPreferences = getActivity().getSharedPreferences(AppUtils.PREF_NAME, Context.MODE_PRIVATE);
        mUserId = sharedPreferences.getString(getString(com.virtualdusk.zezgn.R.string.key_user_id), null);

        progress_dialog = new ProgressDialog(getActivity(), com.virtualdusk.zezgn.R.style.MyTheme);
        progress_dialog.setCancelable(false);
        progress_dialog.setIndeterminateDrawable(getResources().getDrawable(com.virtualdusk.zezgn.R.drawable.my_progress_indeterminate));
    }

    private void findRes(View view) {

        btnSubmit = (TextView)view. findViewById(com.virtualdusk.zezgn.R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);
        edPassword = (EditText)view. findViewById(com.virtualdusk.zezgn.R.id.edPassword);
        ivBack = (ImageView)view.findViewById(com.virtualdusk.zezgn.R.id.ivBack);
        edConfirmPassword = (EditText)view.findViewById(com.virtualdusk.zezgn.R.id.edConfirmPassword);
        edNewPassword = (EditText)view.findViewById(com.virtualdusk.zezgn.R.id.edNewPassword);
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
        edNewPassword.addTextChangedListener(new TextWatcher() {

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
                    edNewPassword.setText("");
                }
                if (s.length() == 20) {
                    show("New Password should be between 6-20 characters in length");
                } else {
//                    show("not excceds");

                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case com.virtualdusk.zezgn.R.id.btnSubmit:
                onSubmissionClick();
                break;
            case com.virtualdusk.zezgn.R.id.ivBack:
                //onBackPressed();
                break;

        }
    }

    private void onSubmissionClick() {
        mPassword = edPassword.getText().toString();
        mConfirmPassword = edConfirmPassword.getText().toString();
        mNewPassword = edNewPassword.getText().toString();


        if (mPassword.length() == 0) {
            show("Please enter Old Password");
        } else if (mPassword.length() < 6) {
            show("Password should be between 6-20 characters in length");
        } else if (mNewPassword.length() == 0) {
            show("Please enter New Password");
        } else if (mNewPassword.length() < 6) {
            show("New Password should be between 6-20 characters in length");
        } else if (mConfirmPassword.length() == 0) {
            show("Please enter Confirm Password");
        } else if (mConfirmPassword.length() < 6) {
            show("Confirm Password should be between 6-20 characters in length");
        } else if (!edNewPassword.getText().toString().equals(edConfirmPassword.getText().toString())) {
            show(getString(com.virtualdusk.zezgn.R.string.pass_not_match_essential));
        } else {
            if (new ConnectionDetector(getActivity()).isConnectingToInternet()) {
                progress_dialog.show();
                fetchViaRetrofit();
            } else {
                show("Unable to connect to the Internet.");
            }
        }


    }


    private void fetchViaRetrofit() {
        //Creating an object of our api interface

        Retrofit restAdapter = ChangePasswordVerifyApi.retrofit;
        ChangePasswordVerifyApi registerApi = restAdapter.create(ChangePasswordVerifyApi.class);

        Call<JsonElement> call = registerApi.getOTPCodeViaJSON(getSignInJSON());


        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.e(TAG, "onResponse: " + response.body());
                progress_dialog.dismiss();
                parseResponse(response.body().toString());
            }

            @Override
            public void onFailure(Call<JsonElement> result, Throwable t) {

                progress_dialog.dismiss();

                Log.e(TAG, "onFailure: " + result.toString());
            }
        });


    }

    private static final String TAG = ChangePassword.class.getSimpleName().toString();

    private void parseResponse(String response) {
        try {

            JSONObject jsnon = new JSONObject(response);
            String response1 = jsnon.getString("response");
            JSONObject jsonObject = new JSONObject(response1);
            String status = jsonObject.getString("code");
            String message = jsonObject.getString("message");

            if (status.equals("200")) {
                show(message);

                SaveRecords.saveToPreference(getString(com.virtualdusk.zezgn.R.string.key_user_id), null, getActivity());

              //  startActivity(new Intent(getActivity(), LoginActivity.class));
                Intent i = new Intent(getActivity(), LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);


            } else {

                String errors = jsonObject.getString("errors");
                JSONObject jsonObject1 = new JSONObject(errors);

                String errorMessages = jsonObject1.getString("error_messages");

                JSONObject json_error = new JSONObject(errorMessages);

                String old_password = json_error.getString("old_password");

                JSONObject json_Old_Pass = new JSONObject(old_password);

                String custom_message = json_Old_Pass.getString("custom");
                show(custom_message);


//                "errors":{
//                    "error_messages":{
//                        "old_password":{
//                            "custom":"The current password does not match!"
//                        }
//                    }
//                }
//            }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private SharedPreferences sharedPreferences;


    private UserInfo getSignInJSON() {
        UserInfo signInData = new UserInfo();
        signInData.setUser_id(mUserId);
        signInData.setOld_password(mPassword);
        signInData.setNew_password(mConfirmPassword);


        Map<String, String> paramsJsonObject = new ArrayMap<>();


        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(paramsJsonObject)).toString());


        return signInData;
    }
    public void show(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Home.tvTitle.setText("CHANGE PASSWORD");
    }
}
