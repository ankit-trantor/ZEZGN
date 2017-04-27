package com.virtualdusk.zezgn.api;


import com.google.gson.JsonElement;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import com.virtualdusk.zezgn.Model.NullOnEmptyConverterFactory;
import com.virtualdusk.zezgn.Model.UserInfo;
import com.virtualdusk.zezgn.Utilities.Constant;
import com.virtualdusk.zezgn.utils.User;

/**
 * Created by hitesh.mathur on 8/9/2016.
 */
public interface AddPhotoApi {

    @Multipart
    @POST("users/upload-profile-image.json")


    Call<JsonElement> editUser (@Part("profile_image\"; filename=\"pp\" ") RequestBody file , @Part("user_id") RequestBody id);



    //Creating a rest adapter
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constant.domain_)
            .addConverterFactory(new NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build();


}
