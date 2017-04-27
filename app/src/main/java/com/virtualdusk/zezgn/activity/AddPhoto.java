package com.virtualdusk.zezgn.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.virtualdusk.zezgn.Model.UserInfo;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.MarshmallowPermissions;
import com.virtualdusk.zezgn.Utilities.SaveRecords;
import com.virtualdusk.zezgn.api.AddPhotoApi;
import com.virtualdusk.zezgn.api.OTPVerifyApi;
import com.virtualdusk.zezgn.utils.helper.AppUtils;

/**
 * Created by Amit Sharma on 9/30/2016.
 */
public class AddPhoto extends AbstractActivity {

    private ImageView ivUserPic;
    private String mPicturePath, mUserId;
    private Button btnAddPhoto, btnSubmit;
    private TextView tvSkip;
    static Bitmap user_pic_bitmapp = null;
    static byte[] bytess = null;
    public static String mImage;
    SharedPreferences sharedPreferences;
    private ProgressDialog progress_dialog;
    private RelativeLayout RlSkip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_photo);

        progress_dialog = new ProgressDialog(AddPhoto.this, R.style.MyTheme);
        progress_dialog.setCancelable(false);
        progress_dialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.my_progress_indeterminate));

        bytess = null;
        user_pic_bitmapp = null;
        mImage = null;

//        sharedPreferences = getSharedPreferences(AppUtils.PREF_NAME, Context.MODE_PRIVATE);

        sharedPreferences = this.getAppSharedPreferences();
        String user_id = sharedPreferences.getString("profile_image", null);

        Log.e("GET", "onCreate: user_id:" + user_id);
        System.out.println("@@@@@@@@@...." + user_id);

        ivUserPic = (ImageView) findViewById(R.id.ivUser);
        btnAddPhoto = (Button) findViewById(R.id.btnAddPhoto);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        tvSkip = (TextView) findViewById(R.id.tvSkip);
        RlSkip = (RelativeLayout) findViewById(R.id.rl_skip);

        ivUserPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MarshmallowPermissions mPermission = new MarshmallowPermissions(AddPhoto.this);

                if (!mPermission.checkPermissionForCamera()) {
                    mPermission.requestPermissionForCamera();
                } else {
                    if (!mPermission.checkPermissionForExternalStorage()) {
                        mPermission.requestPermissionForExternalStorage();
                    } else {

                        Intent intent = new Intent(AddPhoto.this, GetProfilePicPopUp.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_up_info, R.anim.slide_down_info);
                    }
                }

            }
        });
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                startActivity(new Intent(AddPhoto.this,Home.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
//                finish();

                Intent intent = new Intent(AddPhoto.this, Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progress_dialog.show();
                fetchViaRetrofit();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (user_pic_bitmapp != null) {
                btnSubmit.setVisibility(View.VISIBLE);
                btnAddPhoto.setVisibility(View.GONE);
                RlSkip.setVisibility(View.GONE);
                ivUserPic.setImageBitmap(user_pic_bitmapp);
                BitmapDrawable background = new BitmapDrawable(user_pic_bitmapp);
                user_pic_bitmapp = null;
            }

        } catch (Exception e) {

        }
    }

    private void fetchViaRetrofit() {

        mUserId = sharedPreferences.getString(getString(R.string.key_user_id), null);

        Log.e(TAG, "fetchViaRetrofit: " + mImage + "," + mUserId);
        Log.e(TAG, "fetchViaRetrofit: ID" + mUserId);
        Retrofit restAdapter = AddPhotoApi.retrofit;
        AddPhotoApi registerApi = restAdapter.create(AddPhotoApi.class);


        MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        File file_mine = new File(mImage);
        RequestBody requestBody = RequestBody.create(MEDIA_TYPE_PNG, file_mine);
        RequestBody id = RequestBody.create(MediaType.parse("text/plain"), mUserId);
        Call<JsonElement> call = registerApi.editUser(requestBody, id);

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

    private static final String TAG = AddPhoto.class.getSimpleName().toString();


    private void parseResponse(String response) {
        try {

            JSONObject jsnon = new JSONObject(response);
            String response1 = jsnon.getString("response");
            JSONObject jsonObject = new JSONObject(response1);
            String status = jsonObject.getString("code");
            String message = jsonObject.getString("message");
            String data = jsonObject.getString("data");
            show(message);
            String profile_image = "";
            if (status.equals("200")) {
                show(message);

                JSONObject objectData = new JSONObject(data);
                profile_image = objectData.getString("profile_image");
                System.out.println(">>>>>>?????" + profile_image);
//                JSONArray countrylist = jsonObject.getJSONArray("data");
//                for (int i = 0; i < countrylist.length(); i++) {
//                    JSONObject data = countrylist.getJSONObject(i);
//                     profile_image = data.getString("profile_image");
//                }
                SaveRecords.saveToPreference(getString(R.string.key_profile_pic), profile_image, AddPhoto.this);


//                startActivity(new Intent(AddPhoto.this,Home.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
//finish();

                Intent intent = new Intent(AddPhoto.this, Home.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                show(message);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
