package com.virtualdusk.zezgn.activity.MyAccount;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.gson.JsonElement;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Part;
import com.virtualdusk.zezgn.Model.CountryItem;
import com.virtualdusk.zezgn.Model.UserInfo;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Constant;
import com.virtualdusk.zezgn.Utilities.SaveRecords;
import com.virtualdusk.zezgn.activity.AbstractActivity;
import com.virtualdusk.zezgn.activity.GetProfilePicPopUp;
import com.virtualdusk.zezgn.activity.Home;
import com.virtualdusk.zezgn.api.AddPhotoApi;
import com.virtualdusk.zezgn.api.CountryListApi;
import com.virtualdusk.zezgn.api.EditUserInfoApi;
import com.virtualdusk.zezgn.utils.ConnectionDetector;

/**
 * Created by Amit Sharma on 10/21/2016.
 */
public class EditUserProfileScreenActivity extends AbstractActivity implements View.OnClickListener {

    private String TAG = EditUserProfileScreenActivity.class.getSimpleName().toString();
    private String id, email=null, fname="", lname="", gender="", str_age_group="", contact_no="", address="", city="", state="", profile_pic="", country_name="", zipcode="", country_id="", printable_name="", country="";

    private ImageView ivBack, ivUserPic;
    private EditText edFirstName, edLastName, edStreetName, edStateName, edCityName, edCountryName, edAgeRange, edGender, edPinCode, edEmail, edContactNumber;
    private DisplayImageOptions options;
    private ImageLoader imageLoader = null;// ImageLoader.getInstance();
    public static byte[] bytess = null;
    public static String mImage;
    public static Bitmap user_pic_bitmapp = null;
    private String mPicturePath;
    private ProgressDialog progress_dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user_details);


        progress_dialog = new ProgressDialog(EditUserProfileScreenActivity.this, R.style.MyTheme);
        progress_dialog.setCancelable(false);
        progress_dialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.my_progress_indeterminate));

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.logo)
                .showImageForEmptyUri(R.mipmap.logo)
                .showImageOnFail(R.mipmap.logo)
                .resetViewBeforeLoading()
                .delayBeforeLoading(1000)
                .cacheInMemory()
                .cacheOnDisc()
                .build();

        bytess = null;
        user_pic_bitmapp = null;

        findRes();
        getValues();
        ivUserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(EditUserProfileScreenActivity.this, GetProfilePicPopUp.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_up_info, R.anim.slide_down_info);
            }
        });
        getCountryListViaRetrofit();
        ivBack = (ImageView) findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    private Intent intent;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.signupbtn:
//                signUpValidations();
      onSubmission();

                break;


            case R.id.country:
                showCountryList();
                break;

            case R.id.edAgeRange:
                showAgeGroupList();
                break;

            case R.id.edGender:
                showGenderList();
                break;

            case R.id.ivBack:
                onBackPressed();
                break;

        }

    }

    private void onSubmission()
    {
        if (edFirstName.getText().toString().length()==0)
        {
            show("Please enter First Name");
        }
        else if (edLastName.getText().toString().length()==0)
        {
            show("Please enter Last Name");

        } else if (edGender.getText().toString().length()==0)
        {
            show("Please select Gender");

        } else if (edAgeRange.getText().toString().length()==0)
        {
            show("Please enter Age Range");

        } else if (edEmail.getText().toString().length()==0)
        {
            show("Please enter Email");

        }

        else if (edCityName.getText().toString().length()==0)
        {
            show("Please enter City Name");

        }else if (edStateName.getText().toString().length()==0)
        {
            show("Please enter State Name");

        }else if (mCountryId==null)
        {
            show("Please select Country");

        }else if (edPinCode.getText().toString().length()==0)
        {
            show("Please enter ZipCode");

        }else if (edContactNumber.getText().toString().length()==0)
        {
            show("Please enter Contact Number");

        }
        else
        {
            if (new ConnectionDetector(EditUserProfileScreenActivity.this).isConnectingToInternet()) {
                Log.e(TAG, "onClick: "+"Connecting" );
                editUserInfoTask();
            } else {
                show(getString(R.string.no_internet));
            }

        }
    }

    private void getValues() {
        intent = getIntent();
        id = intent.getStringExtra("id");
        email = intent.getStringExtra("email");
        fname = intent.getStringExtra("fname");
        lname = intent.getStringExtra("lname");
        gender = intent.getStringExtra("gender");
        str_age_group = intent.getStringExtra("age");
        contact_no = intent.getStringExtra("phone");
        address = intent.getStringExtra("address");
        city = intent.getStringExtra("city");
        state = intent.getStringExtra("state");
        profile_pic = intent.getStringExtra("pic");
        country_name = intent.getStringExtra("country_name");
        zipcode = intent.getStringExtra("zipcode");
        country_id = intent.getStringExtra("country_id");
        printable_name = intent.getStringExtra("printable_name");
        country = intent.getStringExtra("country");

        edEmail.setText(email);
        edFirstName.setText(fname);
        edLastName.setText(lname);
        edGender.setText(gender);
        edAgeRange.setText(str_age_group);
        edPinCode.setText(zipcode);
        edStreetName.setText(address);
        edCityName.setText(city);
        edContactNumber.setText(contact_no);
        //.setText(state);

        edStateName.setText(state);
        edCountryName.setText(printable_name);

        imageLoader.displayImage(Constant.IMAGE_BASE_URL + profile_pic, ivUserPic,
                options, new ImageLoadingListener() {

                    public void onLoadingCancelled(String imageUri,
                                                   View view) {
                    }

                    public void onLoadingComplete(String arg0,
                                                  View arg1, Bitmap arg2) {


                    }

                    public void onLoadingStarted(String arg0, View arg1) {
                        // TODO Auto-generated method stub
                        //if (post_image.getVisibility() == View.VISIBLE) {
                        //   load_progressbar.setVisibility(View.VISIBLE);
                        //}
                    }

                    @Override
                    public void onLoadingFailed(String arg0, View arg1,
                                                FailReason arg2) {
                        // TODO Auto-generated method stub
                        //load_progressbar.setVisibility(View.GONE);
                    }
                });
    }

    private ArrayList<String> countryData;
    private ArrayList<CountryItem> countrydata;
    public static String[] myStrings;
    private String mCountryName, mCountryId = "0";

    public String[] myStrings_Gender = {"Male", "Female"};
    public String[] myStrings_Age = {"18-20", "20-24", "18-20", "20-24", "18-20", "20-24", "18-20", "20-24"};

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (user_pic_bitmapp != null) {
                ivUserPic.setImageBitmap(user_pic_bitmapp);
                BitmapDrawable background = new BitmapDrawable(user_pic_bitmapp);
                user_pic_bitmapp = null;
            }

        } catch (Exception e) {

        }
    }

    private void showAgeGroupList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditUserProfileScreenActivity.this);
        builder.setTitle("Select");
        builder.setItems(myStrings_Age,
                new DialogInterface.OnClickListener() {
                    @SuppressWarnings("deprecation")

                    public void onClick(DialogInterface dialog, int position) {
                        edAgeRange.setText(myStrings_Age[position]);

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showGenderList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditUserProfileScreenActivity.this);
        builder.setTitle("Select");
        builder.setItems(myStrings_Gender,
                new DialogInterface.OnClickListener() {
                    @SuppressWarnings("deprecation")

                    public void onClick(DialogInterface dialog, int position) {
                        edGender.setText(myStrings_Gender[position]);


                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void showCountryList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditUserProfileScreenActivity.this);
        builder.setTitle(getString(R.string.select_country));
        builder.setItems(myStrings,
                new DialogInterface.OnClickListener() {
                    @SuppressWarnings("deprecation")

                    public void onClick(DialogInterface dialog, int position) {
                        String accountName = countrydata.get(position).getName();
                        edCountryName.setText(accountName);
                        mCountryId = countrydata.get(position).getId();
                        country_id = countrydata.get(position).getId();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
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

    private void findRes() {

        edFirstName = (EditText) findViewById(R.id.firstname);
        edLastName = (EditText) findViewById(R.id.lastname);
        edStreetName = (EditText) findViewById(R.id.edStreetName);
        edStateName = (EditText) findViewById(R.id.edStateName);
        edCityName = (EditText) findViewById(R.id.edCityName);
        edCountryName = (EditText) findViewById(R.id.country);
        edAgeRange = (EditText) findViewById(R.id.edAgeRange);
        edGender = (EditText) findViewById(R.id.edGender);
        edPinCode = (EditText) findViewById(R.id.edPinCode);
        edEmail = (EditText) findViewById(R.id.edEmail);
        edContactNumber = (EditText) findViewById(R.id.edMobileNumber);
        ivUserPic = (ImageView) findViewById(R.id.ivUserPic);
        countrydata = new ArrayList<CountryItem>();
        countryData = new ArrayList<String>();
    }

    private String mUserId;

    private void editUserInfoTask() {

        progress_dialog.show();

        mUserId = sharedPreferences.getString(getString(R.string.key_user_id), null);
        email=edEmail.getText().toString();

        Retrofit restAdapter = EditUserInfoApi.retrofit;
        EditUserInfoApi registerApi = restAdapter.create(EditUserInfoApi.class);

//

        Log.e(TAG, "editUserInfoTask: Image"+mImage );
        //
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        File file_mine = new File(mImage);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_PNG, file_mine);

        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), mUserId);
        RequestBody email_ = RequestBody.create(MediaType.parse("text/plain"), email);
        RequestBody fname_ = RequestBody.create(MediaType.parse("text/plain"), fname);
        RequestBody lname_ = RequestBody.create(MediaType.parse("text/plain"), lname);
        RequestBody contact_no_ = RequestBody.create(MediaType.parse("text/plain"), contact_no);
        RequestBody address_ = RequestBody.create(MediaType.parse("text/plain"), address);
        RequestBody city_ = RequestBody.create(MediaType.parse("text/plain"), city);
        RequestBody state_ = RequestBody.create(MediaType.parse("text/plain"), state);
        RequestBody country_id_ = RequestBody.create(MediaType.parse("text/plain"), country_id);
        RequestBody zipcode_ = RequestBody.create(MediaType.parse("text/plain"), zipcode);
        RequestBody age = RequestBody.create(MediaType.parse("text/plain"), str_age_group);
        RequestBody device_type_ = RequestBody.create(MediaType.parse("text/plain"), "android");
        RequestBody device_token_ = RequestBody.create(MediaType.parse("text/plain"), "xsdbnvbbbbb");


        Log.e(TAG, "editUserInfoTask: ID:"+mUserId+",Email:"+email+",Name:"+fname+",lname_:"+lname+",contact_no_:"+contact_no+",address_:"+address+",city_:"
        +city+",state_:"+state+",country_id_:"+country_id+",zipcode_:"+zipcode);

//
        Call<JsonElement> call = registerApi.editUser(requestBody, id, email_, fname_, lname_, contact_no_, address_, city_,
                state_, country_id_, zipcode_, device_type_, device_token_,age);

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


    private void parseResponse(String response) {
        try {

            JSONObject jsnon = new JSONObject(response);
            String response1 = jsnon.getString("response");
            JSONObject jsonObject = new JSONObject(response1);
            String status = jsonObject.getString("code");
            String message = jsonObject.getString("message");
            show(message);
            String profile_image="";
            if (status.equals("200")) {
                String data = jsonObject.getString("data");

                show(message);

                JSONObject objectData=new JSONObject(data);
                profile_image=objectData.getString("profile_pic");
                SaveRecords.saveToPreference(getString(R.string.key_profile_pic),profile_image,EditUserProfileScreenActivity.this);

                startActivity(new Intent(EditUserProfileScreenActivity.this,Home.class));
                finish();
            }

            else {
                show(message);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private UserInfo getParms() {
        UserInfo signInData = new UserInfo();
        signInData.setUser_id(mUserId);
        signInData.setEmail(email);
        signInData.setFname(fname);
        signInData.setLname(lname);
        signInData.setContact_no(contact_no);
        signInData.setAddress(address);
        signInData.setCity(city);
        signInData.setState(state);
        signInData.setCountry_id(country_id);
        signInData.setZipcode(zipcode);
        signInData.setDevice_type("android");
        signInData.setDevice_token("gvvbhbbhbhjbjmnmnmn");


//        "user_id": "46"
//        "email": "endivesofttest@gmail.com",
//            "fname": "endive",
//            "lname": "test",

//        “profile_image”: data,
//            "contact_no": "8696251627",
//            "address": "",
//            "city": "",
//            "state": "",
//            "country_id": 20,
//            "zipcode": "",
//            "device_type": "Iphone",
//            "device_token": "dddaDADASDaweghdfhrbt"


        return signInData;
    }


}
