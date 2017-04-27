package com.virtualdusk.zezgn.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.virtualdusk.zezgn.Model.CountryItem;
import com.virtualdusk.zezgn.Model.RegisterINRequest;
import com.virtualdusk.zezgn.Model.SignInRequest;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Constant;
import com.virtualdusk.zezgn.Utilities.SaveRecords;
import com.virtualdusk.zezgn.api.CountryListApi;
import com.virtualdusk.zezgn.api.OTPVerifyApi;
import com.virtualdusk.zezgn.api.RegisterApi;
import com.virtualdusk.zezgn.utils.ConnectionDetector;
import com.virtualdusk.zezgn.utils.ValidationMethod;

/**
 * Created by Amit Sharma on 9/26/2016.
 */
public class Register extends AbstractActivity implements View.OnClickListener {

    private ProgressDialog progress_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.register_user);
        progress_dialog = new ProgressDialog(Register.this, R.style.MyTheme);
        progress_dialog.setCancelable(false);
        progress_dialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.my_progress_indeterminate));
        findRes();
    }

    private EditText edFirstName, edLastName, edPassword, edConfirmPassword, edCountry,
            edEmail, edMobileNumber;
    private ImageView ivBack;

    private TextView btnRegister, tvAlreadyRegistered;

    public void show(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    private void findRes() {
        countrydata = new ArrayList<CountryItem>();
        countryData = new ArrayList<String>();

        edFirstName = (EditText) findViewById(R.id.firstname);
        edLastName = (EditText) findViewById(R.id.lastname);
        edPassword = (EditText) findViewById(R.id.password);
        edConfirmPassword = (EditText) findViewById(R.id.edConfirmPassword);
        edCountry = (EditText) findViewById(R.id.country);
        edEmail = (EditText) findViewById(R.id.emailIdd);
        edMobileNumber = (EditText) findViewById(R.id.edMobileNumber);
        btnRegister = (TextView) findViewById(R.id.signupbtn);
        ivBack = (ImageView) findViewById(R.id.ivBack);

        getCountryListViaRetrofit();

        tvAlreadyRegistered = (TextView) findViewById(R.id.tvAlreadyRegistered);
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
                    show(getString(R.string.password_validation));
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
                    show(getString(R.string.con_password_validation));
                }
            }
        });

        edFirstName.addTextChangedListener(new TextWatcher() {

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
            public void afterTextChanged(Editable et) {
                if (et.length() == 20) {
//                    firstName.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);


                    show(getString(R.string.first_name_validation));
                }


            }
        });


        edLastName.addTextChangedListener(new TextWatcher() {

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
                if (s.length() == 20) {
//                    Lastname.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

                    show(getString(R.string.last_name_validation));
                }
            }
        });
    }

    public String extractDigits(String src) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < src.length(); i++) {
            char c = src.charAt(i);
            if (Character.isDigit(c)) {
                builder.append(c);
            }
        }
        return builder.toString();
    }

    public static String capitalize(String text) {
        char[] stringArray = text.trim().toCharArray();
        boolean wordStarted = false;
        for (int i = 0; i < stringArray.length; i++) {
            char ch = stringArray[i];
            if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '\'') {
                if (!wordStarted) {
                    stringArray[i] = Character.toUpperCase(stringArray[i]);
                    wordStarted = true;
                }
            } else {
                wordStarted = false;
            }
        }
        return new String(stringArray);
    }

    ArrayList<String> countryData;

    private void signUpValidations() {


        String first_name_number = extractDigits(edFirstName.getText().toString());
        String last_name_number = extractDigits(edLastName.getText().toString());

        if (edFirstName.getText().toString().length() == 0) {
            show(getString(R.string.first_name_essential));

        } else if (first_name_number.length() > 0) {
            show(getString(R.string.first_name_number_validation));
        } else if (last_name_number.length() > 0) {


            show(getString(R.string.last_name_number_validation));
        } else if (edLastName.getText().toString().length() == 0) {


            show(getString(R.string.last_name_essential));

        } else if (edEmail.getText().toString().length() == 0) {
            show(getString(R.string.email_essential));

        } else if (!ValidationMethod.emailValidation(edEmail.getText().toString())) {
            show(getString(R.string.valid_email_essential));

        } else if (edPassword.getText().toString().length() == 0) {
            show(getString(R.string.password_essential));

        } else if (edPassword.getText().toString().length() < 6) {
            show(getString(R.string.password_validation));

        } else if (edConfirmPassword.getText().toString().length() == 0) {
            show(getString(R.string.confirm_password_essential));

        } else if (edConfirmPassword.getText().toString().length() < 6) {
            show(getString(R.string.con_password_validation));

        } else if (!edPassword.getText().toString().equals(edConfirmPassword.getText().toString())) {
            show(getString(R.string.pass_not_match_essential));
        } else if (mCountryId.equals("0")) {
            show(getString(R.string.country_essential));

        } else if (edMobileNumber.getText().toString().length() == 0) {
            show(getString(R.string.mobile_essential));
        } else {


            String str = capitalize(edFirstName.getText().toString());
            edFirstName.setText(str);

            String str1 = capitalize(edLastName.getText().toString());
            edLastName.setText(str1);

            mFirstName = edFirstName.getText().toString();
            mLastName = edLastName.getText().toString();
            mEmail = edEmail.getText().toString();
            mCountryName = edCountry.getText().toString();
            mPassword = edPassword.getText().toString();
            mConfirmPassword = edConfirmPassword.getText().toString();
            mMobileNumber = edMobileNumber.getText().toString();

            if (new ConnectionDetector(Register.this).isConnectingToInternet()) {
                progress_dialog.show();
                registerViaRetrofit();
            } else {
                show(getString(R.string.no_internet));
            }
        }
    }

    ArrayList<CountryItem> countrydata;
    public static String[] myStrings;

    private void getCountryListViaRetrofit() {
        //Creating an object of our api interface
        Retrofit restAdapter = CountryListApi.retrofit;
        CountryListApi registerApi = restAdapter.create(CountryListApi.class);

        Call<JsonElement> call = registerApi.getCountryViaJSON();

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.e(TAG, "onResponse: " + response.body());

                try {
                    JSONObject jsnon = new JSONObject(response.body().toString());
                    String response1 = jsnon.getString("response");
                    JSONObject jsonObject = new JSONObject(response1);
                    String status = jsonObject.getString("code");
                    if (status.equals("200")) {
                        JSONArray countrylist = jsonObject.getJSONArray("data");
                        myStrings = new String[countrylist.length()];
                        for (int i = 0; i < countrylist.length(); i++) {
                            JSONObject data = countrylist.getJSONObject(i);
                            CountryItem country = new CountryItem();
                            country.setId(data.getString("id"));
                            country.setIso(data.getString("iso"));
                            country.setName(data.getString("name"));
                            country.setPrintable_name(data.getString("printable_name"));
                            country.setIso3(data.getString("iso3"));
                            country.setNumcode(data.getString("numcode"));
                            countrydata.add(country);
                            myStrings[i] = data.getString("name");


                        }
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<JsonElement> result, Throwable t) {
                Log.e(TAG, "onFailure: " + result.toString());
            }
        });


    }

    private void showCountryList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Register.this);
        builder.setTitle(getString(R.string.select_country));
        builder.setItems(myStrings,
                new DialogInterface.OnClickListener() {
                    @SuppressWarnings("deprecation")

                    public void onClick(DialogInterface dialog, int position) {
                        String accountName = countrydata.get(position).getName();
                        edCountry.setText(accountName);
                        mCountryId = countrydata.get(position).getId();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private String mFirstName, mLastName, mPassword, mMobileNumber, mConfirmPassword, mEmail, mCountryName, mCountryId = "0";

    private void registerViaRetrofit() {
        //Creating an object of our api interface
        Retrofit restAdapter = RegisterApi.retrofit;
        RegisterApi registerApi = restAdapter.create(RegisterApi.class);

        Call<JsonElement> call = registerApi.createUserViaJSON(getRegisorJSON());

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
                progress_dialog.dismiss();
            }
        });


//        registerApi.createUser(first_name, Last_name, email_id, country_id, Password_, getString(R.string.device_type_android), android_id, getString(R.string.login_type_email), callback);

    }

    public static final String TAG = "Retrofit";

    private RegisterINRequest getRegisorJSON() {

        RegisterINRequest registerInData = new RegisterINRequest();

        String STR_FCM_TOKEN = sharedPreferences.getString(Constant.FCM_TOKEN, "");
        Log.d(TAG, "getRegisorJSON: STR_FCM_TOKEN " + STR_FCM_TOKEN);

        registerInData.setFname(mFirstName);
        registerInData.setLname(mLastName);
        registerInData.setEmail(mEmail);
        registerInData.setContact_no(mMobileNumber);
        registerInData.setCountry_id(mCountryId);
        registerInData.setPassword(mPassword);
        registerInData.setDevice_type("android");
        registerInData.setDevice_token(STR_FCM_TOKEN);
        registerInData.setSocial_register("false");

        Map<String, String> paramsJsonObject = new ArrayMap<>();
//        paramsJsonObject.put("first_name", mUserName);


        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(paramsJsonObject)).toString());


        return registerInData;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signupbtn:
                signUpValidations();
                break;
            case R.id.tvAlreadyRegistered:
                startActivity(new Intent(Register.this, LoginActivity.class));
                finish();
                break;

            case R.id.country:
                showCountryList();
                break;

            case R.id.ivBack:
                onBackPressed();
                break;

        }

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

                String data_ = jsonObject.getString("data");
                JSONObject data = new JSONObject(data_);

                String id = data.getString("id");
                String email = data.getString("email");
                String fname = data.getString("fname");

                String lname = data.getString("lname");
                String gender = data.getString("gender");
                String age_group = data.getString("age_group");
                String confirmation_code = data.getString("confirmation_code");
                String contact_no = data.getString("contact_no");
                String address = data.getString("address");
                String city = data.getString("city");
                String state = data.getString("state");
                String country_id = data.getString("country_id");
                String zipcode = data.getString("zipcode");
                String profile_pic = data.getString("profile_pic");
                String role = data.getString("role");
                String social_register = data.getString("social_register");
                String status_ = data.getString("status");
                String is_block = data.getString("is_block");
                String device_type = data.getString("device_type");
                String device_token = data.getString("device_token");
                String notification_on = data.getString("notification_on");
                String created = data.getString("created");
                String modified = data.getString("modified");

             //   SaveRecords.saveToPreference(getString(R.string.key_user_id), id, Register.this);
                SaveRecords.saveToPreference(getString(R.string.key_notification_on), notification_on, Register.this);
                SaveRecords.saveToPreference(getString(R.string.key_name), (fname + " " + lname), Register.this);
                SaveRecords.saveToPreference(getString(R.string.key_email), email, Register.this);
                SaveRecords.saveToPreference(getString(R.string.key_mobile), contact_no, Register.this);
                SaveRecords.saveToPreference(getString(R.string.key_profile_image), profile_pic, Register.this);

                Intent i = new Intent(Register.this, OTPVerifyActivity.class);
                i.putExtra("user_id",id);
                startActivity(i);
                Register.this.finish();


            } else {

                String errors = jsonObject.getString("errors");
                JSONObject jsonObject1 = new JSONObject(errors);

                show(jsonObject1.getString("error_messages"));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
