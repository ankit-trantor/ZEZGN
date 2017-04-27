package com.virtualdusk.zezgn.Profile;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.virtualdusk.zezgn.InterfaceClasses.DialogInterface;
import com.virtualdusk.zezgn.Model.CountryItem;
import com.virtualdusk.zezgn.Model.UserInfo;
import com.virtualdusk.zezgn.MyApplication;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Constant;
import com.virtualdusk.zezgn.Utilities.MarshmallowPermissions;
import com.virtualdusk.zezgn.Utilities.SaveRecords;
import com.virtualdusk.zezgn.Utilities.Utilities;
import com.virtualdusk.zezgn.activity.CustomDesignActivity;
import com.virtualdusk.zezgn.activity.GetProfilePicPopUp;
import com.virtualdusk.zezgn.activity.Home;
import com.virtualdusk.zezgn.activity.MyAccount.EditUserProfileScreenActivity;
import com.virtualdusk.zezgn.api.CountryListApi;
import com.virtualdusk.zezgn.api.EditUserInfoApi;
import com.virtualdusk.zezgn.utils.ConnectionDetector;
import com.virtualdusk.zezgn.utils.ValidationMethod;
import com.virtualdusk.zezgn.utils.helper.AppUtils;

import static com.virtualdusk.zezgn.activity.Home.sharedPreferences;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditUserProfileFragment extends Fragment implements View.OnClickListener, DialogInterface {


    private Intent intent;
    private String TAG = EditUserProfileScreenActivity.class.getSimpleName().toString();
    private String id = "", email = "", fname = "", lname = "", gender = "", str_age_group = "", contact_no = "",
            address = "", city = "", state = "", profile_pic = "", country_name = "", zipcode = "", country_id = "", printable_name = "", country = "";

    private ImageView ivBack, ivUserPic;
    private EditText edFirstName, edLastName, edStreetName, edStateName, edCityName, edCountryName, edAgeRange,
            edGender, edPinCode, edEmail, edContactNumber;
    private DisplayImageOptions options;
    private ImageLoader imageLoader = null;// ImageLoader.getInstance();
    public static byte[] bytess = null;
    public static String mImage;
    // public static Bitmap user_pic_bitmapp = null;
    private String mPicturePath;
    private ProgressDialog progress_dialog;
    private TextView BtnSubmit;

    private DialogInterface mDialogInterface;
    private ArrayList<String> countryData;
    private ArrayList<CountryItem> countrydata;
    public static String[] myStrings;
    private String mCountryName, mCountryId = "0";

    public String[] myStrings_Gender = {"Male", "Female"};
    public String[] myStrings_Age = {"1-14", "15-24", "25-34", "35-50", "51-60", "Above 60"};
    private String userChoosenTask;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;


    public EditUserProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.edit_user_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDialogInterface = this;
        sharedPreferences = getActivity().getSharedPreferences(AppUtils.PREF_NAME, Context.MODE_PRIVATE);
        progress_dialog = new ProgressDialog(getActivity(), R.style.MyTheme);
        progress_dialog.setCancelable(false);
        progress_dialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.my_progress_indeterminate));

        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.user_placeholder)
                .showImageForEmptyUri(R.mipmap.user_placeholder)
                .showImageOnFail(R.mipmap.user_placeholder)
                .resetViewBeforeLoading()
                .delayBeforeLoading(1000)
                .cacheInMemory()
                .cacheOnDisc()
                .build();

        bytess = null;
        //  user_pic_bitmapp = null;

        findRes(view);
        getValues();
        ivUserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent intent = new Intent(getActivity(), GetProfilePicPopUp.class);
//                startActivity(intent);
//                getActivity().overridePendingTransition(R.anim.slide_up_info, R.anim.slide_down_info);

                selectImage();


            }
        });
        getCountryListViaRetrofit();
        ivBack = (ImageView) view.findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getActivity().finish();
            }
        });
    }

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

//            case R.id.ivBack:
//                onBackPressed();
//                break;

        }

    }

    private void onSubmission() {

        email = edEmail.getText().toString();
        fname = edFirstName.getText().toString();
        lname = edLastName.getText().toString();
        gender = edGender.getText().toString();
        str_age_group = edAgeRange.getText().toString();
        contact_no = edContactNumber.getText().toString();
        address = edStreetName.getText().toString();
        city = edCityName.getText().toString();
        state = edStateName.getText().toString();
        country_name = edCountryName.getText().toString();
        zipcode = edPinCode.getText().toString();

        if (edFirstName.getText().toString().length() == 0) {
            show("Please enter First Name");
        } else if (edLastName.getText().toString().length() == 0) {
            show("Please enter Last Name");

        } else if (edGender.getText().toString().length() == 0) {
            show("Please select Gender");

        } else if (edAgeRange.getText().toString().length() == 0) {
            show("Please enter Age Range");

        }


//        else if (edEmail.getText().toString().length() == 0) {
//            show("Please enter Email");
//
//        }


        else if (!ValidationMethod.emailValidation(edEmail.getText().toString())) {
            show("Please enter valid email");
        } else if (edStreetName.getText().toString().length() == 0) {
            show("Please enter address");

        } else if (edCityName.getText().toString().length() == 0) {
            show("Please enter City Name");

        } else if (edStateName.getText().toString().length() == 0) {
            show("Please enter State Name");

        } else if (mCountryId == null) {
            show("Please select Country");

        } else if (edPinCode.getText().toString().length() == 0) {
            show("Please enter ZipCode");

        }

//        else if (edContactNumber.getText().toString().length() == 0) {
//            show("Please enter Contact Number");
//
//        }


        else {
            if (new ConnectionDetector(getActivity()).isConnectingToInternet()) {
                Log.e(TAG, "onClick: " + "Connecting");
                editUserInfoTask();
            } else {
                show(getString(R.string.no_internet));
            }

        }
    }

    private void getValues() {

        // intent = getActivity().getIntent();
        //if (intent != null) {
        Bundle bundle = this.getArguments();
        if (bundle != null) {

            id = bundle.getString("id", "");
            email = bundle.getString("email", "");
            fname = bundle.getString("fname", "");
            lname = bundle.getString("lname", "");
            gender = bundle.getString("gender", "");
            str_age_group = bundle.getString("age", "");
            contact_no = bundle.getString("phone", "");
            address = bundle.getString("address", "");
            city = bundle.getString("city", "");
            state = bundle.getString("state", "");
            profile_pic = bundle.getString("pic", "");
            country_name = bundle.getString("country_name", "");
            zipcode = bundle.getString("zipcode", "");
            country_id = bundle.getString("country_id", "");
            printable_name = bundle.getString("printable_name", "");
            country = bundle.getString("country", "");

            Log.d(TAG, "getValues: str_age_group " + str_age_group);

        } else {
            Log.e(TAG, "getValues: " + "bundle is null");
        }
//        }else{
//            Log.e(TAG, "getValues: "+"intent is null" );
//        }


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
        String url = (Constant.IMAGE_BASE_URL + profile_pic);

//        try {
//
//            Log.d(TAG, "parseResponse: url " + url);
//            if (url != null && !url.equalsIgnoreCase("") && !profile_pic.equalsIgnoreCase("")) {
//                Picasso.with(getActivity())
//                        .load(url)
//                        .error(R.mipmap.user_placeholder)
//                        .into(ivUserPic);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        imageLoader.displayImage(url, ivUserPic,
                options, new ImageLoadingListener() {

                    public void onLoadingCancelled(String imageUri,
                                                   View view) {
                        ivUserPic.setImageResource(R.mipmap.user_placeholder);
                    }

                    public void onLoadingComplete(String arg0,
                                                  View arg1, Bitmap arg2) {


                    }

                    public void onLoadingStarted(String arg0, View arg1) {
                        // TODO Auto-generated method stub
                        //if (post_image.getVisibility() == View.VISIBLE) {
                        //   load_progressbar.setVisibility(View.VISIBLE);
                        //}
                        ivUserPic.setImageResource(R.mipmap.user_placeholder);
                    }

                    @Override
                    public void onLoadingFailed(String arg0, View arg1,
                                                FailReason arg2) {
                        // TODO Auto-generated method stub
                        //load_progressbar.setVisibility(View.GONE);
                        ivUserPic.setImageResource(R.mipmap.user_placeholder);
                    }
                });
    }


    @Override
    public void onResume() {
        super.onResume();

        Home.ivSetting.setVisibility(View.GONE);
        Home.tvTitle.setText("EDIT PROFILE");
//        try {
//            if (user_pic_bitmapp != null) {
//                ivUserPic.setImageBitmap(user_pic_bitmapp);
//                user_pic_bitmapp = null;
//            }
//
//        } catch (Exception e) {
//
//        }
    }


    private void showAgeGroupList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Please Select Age Group");
        builder.setItems(myStrings_Age,
                new android.content.DialogInterface.OnClickListener() {

                    @SuppressWarnings("deprecation")

                    public void onClick(android.content.DialogInterface dialog, int position) {
                        edAgeRange.setText(myStrings_Age[position]);

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showGenderList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Gender");
        builder.setItems(myStrings_Gender,
                new android.content.DialogInterface.OnClickListener() {
                    @SuppressWarnings("deprecation")

                    public void onClick(android.content.DialogInterface dialog, int position) {
                        edGender.setText(myStrings_Gender[position]);


                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void showCountryList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.select_country));
        builder.setItems(myStrings,
                new android.content.DialogInterface.OnClickListener() {
                    @SuppressWarnings("deprecation")

                    public void onClick(android.content.DialogInterface dialog, int position) {
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

    private void findRes(View view) {

        BtnSubmit = (TextView) view.findViewById(R.id.signupbtn);
        BtnSubmit.setOnClickListener(this);

        edFirstName = (EditText) view.findViewById(R.id.firstname);
        edLastName = (EditText) view.findViewById(R.id.lastname);
        edStreetName = (EditText) view.findViewById(R.id.edStreetName);
        edStateName = (EditText) view.findViewById(R.id.edStateName);
        edCityName = (EditText) view.findViewById(R.id.edCityName);
        edCountryName = (EditText) view.findViewById(R.id.country);
        edAgeRange = (EditText) view.findViewById(R.id.edAgeRange);
        edGender = (EditText) view.findViewById(R.id.edGender);
        edPinCode = (EditText) view.findViewById(R.id.edPinCode);
        edEmail = (EditText) view.findViewById(R.id.edEmail);
        edContactNumber = (EditText) view.findViewById(R.id.edMobileNumber);
        ivUserPic = (ImageView) view.findViewById(R.id.ivUserPic);
        countrydata = new ArrayList<CountryItem>();
        countryData = new ArrayList<String>();

        edGender.setOnClickListener(this);
        edAgeRange.setOnClickListener(this);
        edCountryName.setOnClickListener(this);
    }

    private String mUserId;

    private void editUserInfoTask() {

        progress_dialog.show();
        String STR_FCM_TOKEN = sharedPreferences.getString(Constant.FCM_TOKEN, "");
        Log.d(TAG, "editUserInfoTask: STR_FCM_TOKEN " + STR_FCM_TOKEN);

        mUserId = sharedPreferences.getString(getString(R.string.key_user_id), null);
        email = edEmail.getText().toString();

        Retrofit restAdapter = EditUserInfoApi.retrofit;
        EditUserInfoApi registerApi = restAdapter.create(EditUserInfoApi.class);

//

        Log.e(TAG, "editUserInfoTask: Image" + mImage);
        //
        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        if (mImage == null) {
            mImage = "";
        }

        Log.e(TAG, "editUserInfoTask: lname" + lname);
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
        RequestBody age_group = RequestBody.create(MediaType.parse("text/plain"), str_age_group);
        RequestBody device_type_ = RequestBody.create(MediaType.parse("text/plain"), "android");
        RequestBody device_token_ = RequestBody.create(MediaType.parse("text/plain"), STR_FCM_TOKEN);


        Log.e(TAG, "editUserInfoTask: ID:" + mUserId + ",Email:" + email + ",Name:" + fname + ",lname_:" + lname + ",contact_no_:" + contact_no + ",address_:" + address + ",city_:"
                + city + ",state_:" + state + ",country_id_:" + country_id + ",zipcode_:" + zipcode);

        if (mImage.equalsIgnoreCase("")) {
            Call<JsonElement> call = registerApi.editUserWithoutImage(id, email_, fname_, lname_, contact_no_,
                    address_, city_, state_, country_id_, zipcode_, device_type_, device_token_, age_group);

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
        } else {


            Call<JsonElement> call = registerApi.editUser(requestBody, id, email_, fname_, lname_, contact_no_, address_,
                    city_, state_, country_id_, zipcode_, device_type_, device_token_, age_group);

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


    }


    private void parseResponse(String response) {
        try {

            JSONObject jsnon = new JSONObject(response);
            String response1 = jsnon.getString("response");
            JSONObject jsonObject = new JSONObject(response1);
            String status = jsonObject.getString("code");
            String message = jsonObject.getString("message");
            //  show(message);
            String profile_image = "";
            if (status.equals("200")) {


                //show(message);
                String strData = jsonObject.getString("data");
                JSONObject data = new JSONObject(strData);


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

                SaveRecords.saveToPreference(getString(R.string.key_user_id), id, getActivity());
                SaveRecords.saveToPreference(getString(R.string.key_notification_on), notification_on, getActivity());
                SaveRecords.saveToPreference(getString(R.string.key_name), (fname + " " + lname), getActivity());
                SaveRecords.saveToPreference(getString(R.string.key_email), email, getActivity());
                SaveRecords.saveToPreference(getString(R.string.key_mobile), contact_no, getActivity());
                SaveRecords.saveToPreference(getString(R.string.key_profile_image), profile_pic, getActivity());

                Utilities mUtilities = new Utilities();
                mUtilities.showDialogWithInterface(getActivity(), message, "OK", mDialogInterface, "", "");


//                JSONArray countrylist = jsonObject.getJSONArray("data");
//                for (int i = 0; i < countrylist.length(); i++) {
//                    JSONObject data = countrylist.getJSONObject(i);
//
//                }

                //-------------------


//                JSONObject objectData = new JSONObject(data);
//                profile_image = objectData.getString("profile_pic");
//                SaveRecords.saveToPreference(getString(R.string.key_profile_pic), profile_image, getActivity());

                // startActivity(new Intent(getActivity(), Home.class));
                // getActivity().finish();
            } else {
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

    public void show(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onCancelButtonClick(String DialogName, String Message) {

    }

    @Override
    public void onOkButtonClick(String DialogName, String Message) {

        Intent i = new Intent(getActivity(), Home.class);
        i.putExtra("move","account");
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);


    }

    // ----------- code to select or capture image---------

    private void selectImage() {

        // this will open dialog to add image from camera or gallery
        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Image");
        builder.setItems(items, new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int item) {
                boolean result = MyApplication.checkPermission(getActivity());

                if (items[item].equals("Camera")) {
                    userChoosenTask = "Camera";
                    if (result)
                        cameraIntent();

                } else if (items[item].equals("Gallery")) {
                    userChoosenTask = "Gallery";
                    if (result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {

        MarshmallowPermissions mPermission = new MarshmallowPermissions(getActivity());

        if (!mPermission.checkPermissionForCamera()) {
            mPermission.requestPermissionForCamera();
        } else {
            if (!mPermission.checkPermissionForExternalStorage()) {
                mPermission.requestPermissionForExternalStorage();
            } else {

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, REQUEST_CAMERA);

            }
        }

    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        try {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            Log.d(TAG, "onActivityResult: bitmap " + thumbnail.getHeight() + "  " + thumbnail.getWidth());
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);


            //sadsad

            File destination = new File(Environment.getExternalStorageDirectory(),
                    System.currentTimeMillis() + ".png");

            FileOutputStream fo;

            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();

            String path = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), thumbnail, "Title", null);
            Uri selectedImageUri = Uri.parse(path);
            mImage = AppUtils.getRealPathFromURI(getActivity(), selectedImageUri);

            if (thumbnail != null) {
                ivUserPic.setImageBitmap(thumbnail);

            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm = null;
        String strGalleryPath = "";
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                Uri selectedImageUri = data.getData();
                mImage = AppUtils.getRealPathFromURI(getActivity(), selectedImageUri);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

                int newH,newW;
                int R_H = getResources().getDimensionPixelSize(R.dimen.image_height);
                int R_W = getResources().getDimensionPixelSize(R.dimen.image_width);
                int O_H = bm.getHeight();
                int O_W = bm.getWidth();
                if (O_W > R_W) {
                    float C_W = O_W / R_W;
                    float N_W = O_W / C_W;
                    newW = (int) N_W;

                    //   float C_H = O_H / R_H;
                    if (O_H < R_H) {
                        newH = O_H;
                    } else {
                        float N_H = O_H / C_W;
                        newH = (int) N_H;
                    }
                }else {
                    newW = O_W;
                    newH = O_H;
                }

                bm = Bitmap.createScaledBitmap(bm, newW, newH, false);

                Log.d(TAG, "onSelectFromGalleryResult: mImage " + mImage);
                Log.d(TAG, "onSelectFromGalleryResult: bm " + bm);
                if (bm != null) {


                    ivUserPic.setImageBitmap(bm);
                 //   imageLoader.displayImage(mImage, ivUserPic);


                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

}

