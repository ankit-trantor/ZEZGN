package com.virtualdusk.zezgn.api;


import com.google.gson.JsonElement;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import com.virtualdusk.zezgn.Model.NullOnEmptyConverterFactory;
import com.virtualdusk.zezgn.Model.UserInfo;
import com.virtualdusk.zezgn.Utilities.Constant;

/**
 * Created by hitesh.mathur on 8/9/2016.
 */
public interface EditUserInfoApi {

    @Multipart
    @POST("users/edit-profile.json")

    Call<JsonElement> editUser(@Part("profile_image\"; filename=\"pp\" ") RequestBody file, @Part("user_id") RequestBody user_id, @Part("email") RequestBody email,
                               @Part("fname") RequestBody fname, @Part("lname") RequestBody lname, @Part("contact_no") RequestBody contact_no,
                               @Part("address") RequestBody address, @Part("city") RequestBody city, @Part("state") RequestBody state,
                               @Part("country_id") RequestBody country_id, @Part("zipcode") RequestBody zipcode,
                               @Part("device_type") RequestBody device_type,
                               @Part("device_token") RequestBody device_token,@Part("age_group") RequestBody age_group);

    @Multipart
    @POST("users/edit-profile.json")
    Call<JsonElement> editUserWithoutImage(@Part("user_id") RequestBody user_id, @Part("email") RequestBody email,
                               @Part("fname") RequestBody fname, @Part("lname") RequestBody lname, @Part("contact_no") RequestBody contact_no,
                               @Part("address") RequestBody address, @Part("city") RequestBody city, @Part("state") RequestBody state,
                               @Part("country_id") RequestBody country_id, @Part("zipcode") RequestBody zipcode,
                                           @Part("device_type") RequestBody device_type,
                               @Part("device_token") RequestBody device_token,@Part("age_group") RequestBody age_group);

    //Creating a rest adapter
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constant.domain_)
            .addConverterFactory(new NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

}
