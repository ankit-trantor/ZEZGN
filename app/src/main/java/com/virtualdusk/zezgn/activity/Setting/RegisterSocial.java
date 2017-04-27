package com.virtualdusk.zezgn.activity.Setting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.util.ArrayMap;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
import com.virtualdusk.zezgn.Model.SocialRegisterModel;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.SaveRecords;
import com.virtualdusk.zezgn.Utilities.Utilities;
import com.virtualdusk.zezgn.activity.AbstractActivity;
import com.virtualdusk.zezgn.activity.Home;
import com.virtualdusk.zezgn.activity.LoginActivity;
import com.virtualdusk.zezgn.activity.OTPVerifyActivity;
import com.virtualdusk.zezgn.api.CountryListApi;
import com.virtualdusk.zezgn.api.RegisterApi;
import com.virtualdusk.zezgn.api.SocialSignupApi;
import com.virtualdusk.zezgn.instagram.instagram_data.InstagramApp;
import com.virtualdusk.zezgn.utils.ConnectionDetector;
import com.virtualdusk.zezgn.utils.ValidationMethod;

/**
 * Created by Amit Sharma on 9/26/2016.
 */
public class RegisterSocial extends AbstractActivity implements View.OnClickListener {


    private int status_social;
    private String username, email, profile_picture, name;
    private Bundle b;
    private String strUserId = "";

    private Utilities mUtilities;
    private EditText edFirstName, edLastName, edPassword, edConfirmPassword, edCountry,
            edEmail, edMobileNumber;
    private ImageView ivBack;
    private String mFirstName, mLastName, mPassword, mMobileNumber, mConfirmPassword, mEmail, mCountryName, mCountryId = "0";
    private TextView btnRegister, tvAlreadyRegistered;
    public static final String TAG = "Retrofit";

    ArrayList<CountryItem> countrydata;
    public static String[] myStrings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.social_register_user);
        findRes();
        mUtilities = new Utilities();
        Intent in = getIntent();
        b = in.getExtras();
        strUserId = b.getString("user_id");
        name = b.getString("name", "");
        email = b.getString("email_address", "");
        mFirstName = b.getString("first_name", "");
        mLastName = b.getString("last_name", "");

        edFirstName.setText(mFirstName);
        edLastName.setText(mLastName);
        edEmail.setText(email);
        //status_social = b.getInt("status");
        //getValuesFromSocial(status_social);
    }

    private void getValuesFromSocial(int status_social) {

//        b.putString("user_id", id);
//        b.putString("email_address", strSocialEmailAddress);
//        b.putString("name", strSocialFullName);
//        b.putString("first_name", strSocialFirstName);
//        b.putString("last_name", strSocialLastName);

        switch (status_social) {
            case 0:
                break;
            case 1:
                //Instagram
                name = b.getString("name");
                email = b.getString("email_address");
                mFirstName = b.getString("first_name");
                mLastName = b.getString("last_name");

                edFirstName.setText(mFirstName);
                edLastName.setText(mLastName);
                edEmail.setText(email);

                Log.e(TAG, "11: " + LoginActivity.userInfoHashmap.get(InstagramApp.TAG_USERNAME));

                break;
            case 2:
                name = b.getString("personName");
                email = b.getString("email");

                String[] sd1 = name.split("\\s+");
                edFirstName.setText(sd1[0]);
                edLastName.setText(sd1[1]);
                edEmail.setText(email);

                break;
            case 3:
                //facebookasdsadsa

                name = b.getString("name");
                email = b.getString("email_address");
                strUserId = b.getString("user_id");

                String[] sd = name.split("\\s+");
                edFirstName.setText(sd[0]);
                edLastName.setText(sd[1]);
                edEmail.setText(email);

                //profile_picture=b.getString("image");
                break;

        }
    }




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

//        if (edFirstName.getText().toString().length() == 0) {
//            show(getString(R.string.first_name_essential));
//
//        } else if (first_name_number.length() > 0) {
//            show(getString(R.string.first_name_number_validation));
//        } else if (last_name_number.length() > 0) {
//
//
//            show(getString(R.string.last_name_number_validation));
//        } else if (edLastName.getText().toString().length() == 0) {
//
//
//            show(getString(R.string.last_name_essential));
//
//        } else


//        if (edEmail.getText().toString().length() == 0) {
//            show(getString(R.string.email_essential));
//
//        } else if (!ValidationMethod.emailValidation(edEmail.getText().toString())) {
//            show(getString(R.string.valid_email_essential));
//
//        } else

        if (edPassword.getText().toString().length() == 0) {
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

            mFirstName = edFirstName.getText().toString() + " ";
            mLastName = edLastName.getText().toString() + " ";
            mEmail = edEmail.getText().toString();
            mCountryName = edCountry.getText().toString();
            mPassword = edPassword.getText().toString();
            mConfirmPassword = edConfirmPassword.getText().toString();
            mMobileNumber = edMobileNumber.getText().toString();

            if (new ConnectionDetector(RegisterSocial.this).isConnectingToInternet()) {

                registerViaRetrofit();
            } else {
                show(getString(R.string.no_internet));
            }
        }
    }


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
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterSocial.this);
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



    private void registerViaRetrofit() {
        //Creating an object of our api interface
        Retrofit restAdapter = RegisterApi.retrofit;
        SocialSignupApi registerApi = restAdapter.create(SocialSignupApi.class);
        mUtilities.showProgressDialog(RegisterSocial.this, "Please wait...");

        Call<JsonElement> call = registerApi.createUserViaJSON(getRegisorJSON());

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.e(TAG, "onResponse: " + response.body());
                mUtilities.hideProgressDialog();
                parseResponse(response.body().toString());

            }

            @Override
            public void onFailure(Call<JsonElement> result, Throwable t) {
                mUtilities.hideProgressDialog();
                Log.e(TAG, "onFailure: " + result.toString());
            }
        });


//        registerApi.createUser(first_name, Last_name, email_id, country_id, Password_, getString(R.string.device_type_android), android_id, getString(R.string.login_type_email), callback);

    }



    private SocialRegisterModel getRegisorJSON() {

        SocialRegisterModel mSocialRegisterModel = new SocialRegisterModel();

        mSocialRegisterModel.setUser_id(strUserId);
        mSocialRegisterModel.setContact_no(mMobileNumber);
        mSocialRegisterModel.setCountry_id(mCountryId);
        mSocialRegisterModel.setPassword(mPassword);

        Map<String, String> paramsJsonObject = new ArrayMap<>();
//        paramsJsonObject.put("first_name", mUserName);


        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), (new JSONObject(paramsJsonObject)).toString());


        return mSocialRegisterModel;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signupbtn:
                signUpValidations();
                break;
            case R.id.tvAlreadyRegistered:

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
                //show(message);
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
                Log.e(TAG, "parseResponse: id " + id );
                Log.e(TAG, "parseResponse: notification_on " + notification_on );
                Log.e(TAG, "parseResponse: fname " + fname );
                Log.e(TAG, "parseResponse: lname " + lname );
                Log.e(TAG, "parseResponse: email " + email );
                Log.e(TAG, "parseResponse: contact_no " + contact_no );
                Log.e(TAG, "parseResponse: profile_pic " + profile_pic );

                //SaveRecords.saveToPreference(getString(R.string.key_user_id), id, RegisterSocial.this);
                SaveRecords.saveToPreference(getString(R.string.key_notification_on), notification_on, RegisterSocial.this);
                SaveRecords.saveToPreference(getString(R.string.key_name), (fname + " " + lname), RegisterSocial.this);
                SaveRecords.saveToPreference(getString(R.string.key_email), email, RegisterSocial.this);
                SaveRecords.saveToPreference(getString(R.string.key_mobile), contact_no, RegisterSocial.this);
                SaveRecords.saveToPreference(getString(R.string.key_profile_image), profile_pic, RegisterSocial.this);

                Intent i = new Intent(RegisterSocial.this, OTPVerifyActivity.class);
                i.putExtra("user_id",id);
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
}
