package com.virtualdusk.zezgn.Profile;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.virtualdusk.zezgn.Model.UserInfo;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Constant;
import com.virtualdusk.zezgn.activity.Home;
import com.virtualdusk.zezgn.activity.Shipping.AddAddressFragment;
import com.virtualdusk.zezgn.api.GetProfileInfoApi;

/**
 * Created by Amit Sharma on 10/5/2016.
 */
public class UserProfile extends Fragment {

    private String TAG = UserProfile.class.getSimpleName().toString();
    private Button btnSignIn, btnAddAddress;
    private TextView tvName, tvEmail, tvPhone, tvAddress;
    private ImageView ivProfilePic;
    private String user_id;
    private DisplayImageOptions options;
    private ProgressDialog progress_dialog;
    private ImageLoader imageLoader = null;// ImageLoader.getInstance();
    private String id, email, fname, lname, gender, age_group, contact_no, address, city, state, profile_pic, country_name, zipcode, country_id, printable_name, country;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.my_account, container, false);
        btnSignIn = (Button) v.findViewById(R.id.btnSignIn);
        btnAddAddress = (Button) v.findViewById(R.id.btnAddAddress);
        user_id = Home.sharedPreferences.getString(getString(R.string.key_user_id), null);
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


        tvName = (TextView) v.findViewById(R.id.tvName);
        tvEmail = (TextView) v.findViewById(R.id.tvEmail);
        tvPhone = (TextView) v.findViewById(R.id.tvPhone);
        tvAddress = (TextView) v.findViewById(R.id.tvAddress);
        ivProfilePic = (ImageView) v.findViewById(R.id.ivProfilePic);
        progress_dialog = new ProgressDialog(getActivity(), R.style.MyTheme);
        progress_dialog.setCancelable(false);
        progress_dialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.my_progress_indeterminate));



        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Intent intent = new Intent(getActivity(), EditUserProfileScreenActivity.class);
//                intent.putExtra("fname", fname);
//                intent.putExtra("lname", lname);
//                intent.putExtra("gender", gender);
//                intent.putExtra("age", age_group);
//                intent.putExtra("phone", contact_no);
//                intent.putExtra("address", address);
//                intent.putExtra("city", city);
//                intent.putExtra("state", state);
//                intent.putExtra("pic", profile_pic);
//                intent.putExtra("country_name", country_name);
//                intent.putExtra("zipcode", zipcode);
//                intent.putExtra("country_id", country_id);
//                intent.putExtra("printable_name", printable_name);
//                intent.putExtra("country", country);
//                intent.putExtra("email", email);
//                startActivity(intent);

                Bundle mBundle = new Bundle();
                mBundle.putString("fname", fname);
                mBundle.putString("lname", lname);
                mBundle.putString("gender", gender);
                mBundle.putString("age", age_group);
                mBundle.putString("phone", contact_no);
                mBundle.putString("address", address);
                mBundle.putString("city", city);
                mBundle.putString("state", state);
                mBundle.putString("pic", profile_pic);
                mBundle.putString("country_name", country_name);
                mBundle.putString("zipcode", zipcode);
                mBundle.putString("country_id", country_id);
                mBundle.putString("printable_name", printable_name);
                mBundle.putString("country", country);
                mBundle.putString("email", email);
                Log.d(TAG, "getValues: age_group " + age_group);
                Home.strKey = "edit_profile";

                EditUserProfileFragment mEditUserProfileFragment = new EditUserProfileFragment();
                mEditUserProfileFragment.setArguments(mBundle);

                getActivity().getSupportFragmentManager().beginTransaction().add(R.id.frame,mEditUserProfileFragment ).addToBackStack(null).commit();
            }
        });

        btnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Home.ivSetting.setVisibility(View.GONE);
                Home.tvTitle.setText("ADD ADDRESS");
                Home.strKey = "add_address_f";

                Bundle mBundle = new Bundle();
                mBundle.putBoolean("hide_topbar",true);

                AddAddressFragment mAddAddressFragment = new AddAddressFragment();
                mAddAddressFragment.setArguments(mBundle);

                getFragmentManager().beginTransaction().add(R.id.frame, mAddAddressFragment).addToBackStack(null).commit();
            }
        });

        getInfoTask();
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Home.ivSetting.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {

        Log.e(TAG, "onResume: "+"called" );
        super.onResume();
        Home.ivSetting.setVisibility(View.GONE);
        Home.tvTitle.setText("MY PROFILE");

    }
    public void getInfoTask() {
        progress_dialog.show();
        //Creating an object of our api interface
        Retrofit restAdapter = GetProfileInfoApi.retrofit;
        GetProfileInfoApi registerApi = restAdapter.create(GetProfileInfoApi.class);

        Call<JsonElement> call = registerApi.createUserViaJSON(getParms());

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


    }

    private UserInfo getParms() {
        UserInfo signInData = new UserInfo();
        signInData.setUser_id(user_id);

        return signInData;
    }


    private void parseResponse(String response) {
        try {
            JSONObject jsnon = new JSONObject(response);
            String response1 = jsnon.getString("response");
            JSONObject jsonObject = new JSONObject(response1);
            String status = jsonObject.getString("code");
            if (status.equals("200")) {

                String data = jsonObject.getString("data");

                JSONObject jsonObject_Data = new JSONObject(data);

                id = jsonObject_Data.getString("id");
                email = jsonObject_Data.getString("email");
                fname = jsonObject_Data.getString("fname");
                lname = jsonObject_Data.getString("lname");
                gender = jsonObject_Data.getString("gender");
                age_group = jsonObject_Data.getString("age_group");
                String confirmation_code = jsonObject_Data.getString("confirmation_code");
                contact_no = jsonObject_Data.getString("contact_no");
                address = jsonObject_Data.getString("address");
                city = jsonObject_Data.getString("city");
                state = jsonObject_Data.getString("state");
                country_id = jsonObject_Data.getString("country_id");
                zipcode = jsonObject_Data.getString("zipcode");
                profile_pic = jsonObject_Data.getString("profile_pic");
                String role = jsonObject_Data.getString("role");
                String social_register = jsonObject_Data.getString("social_register");
                String status1 = jsonObject_Data.getString("status");
                country = jsonObject_Data.getString("country");

                JSONObject jsonObject_Country = new JSONObject(country);
                country_name = jsonObject_Country.getString("name");
                printable_name = jsonObject_Country.getString("printable_name");
                Home.ivSetting.setVisibility(View.GONE);



                tvName.setText(fname + " " + lname);
                tvEmail.setText(email);
                tvAddress.setText(address + "," + city + "-" + state + "(" + country_name + ")");
                tvPhone.setText(contact_no);
                String url = (Constant.IMAGE_BASE_URL + profile_pic);

              //  try{


//                    if(url != null && !url.equalsIgnoreCase("") && !profile_pic.equalsIgnoreCase("")){
//                        Picasso.with(getActivity())
//                                .load(url)
//                                .error(R.mipmap.user_placeholder)
//                                .into(ivProfilePic);
//                    }
            //    }catch (Exception e){
                  //  e.printStackTrace();
          //      }




                imageLoader.displayImage(url, ivProfilePic,
                        options, new ImageLoadingListener() {

                            public void onLoadingCancelled(String imageUri,
                                                           View view) {

                                ivProfilePic.setImageResource(R.mipmap.user_placeholder);
                            }

                            public void onLoadingComplete(String arg0,
                                                          View arg1, Bitmap arg2) {


                            }

                            public void onLoadingStarted(String arg0, View arg1) {
                                // TODO Auto-generated method stub
                                //if (post_image.getVisibility() == View.VISIBLE) {
                                //   load_progressbar.setVisibility(View.VISIBLE);
                                //}
                                ivProfilePic.setImageResource(R.mipmap.user_placeholder);
                            }

                            @Override
                            public void onLoadingFailed(String arg0, View arg1,
                                                        FailReason arg2) {
                                // TODO Auto-generated method stub
                                //load_progressbar.setVisibility(View.GONE);

                                ivProfilePic.setImageResource(R.mipmap.user_placeholder);
                            }
                        });

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
