package com.virtualdusk.zezgn.api;


import com.google.gson.JsonElement;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import com.virtualdusk.zezgn.Model.NullOnEmptyConverterFactory;
import com.virtualdusk.zezgn.Utilities.Constant;

/**
 * Created by hitesh.mathur on 8/9/2016.
 */
public interface AddCustomDesignApi {

    @Multipart
    @POST("designs/add-custom-design.json")

    Call<JsonElement> addDesign(@Part("title") RequestBody title,
                                @Part("design_img\"; filename=\"pp\" ") RequestBody file,
                                @Part("user_id") RequestBody id);

//    Call<JsonElement> uploadImage(@Part("user_id") String user_id, @Part("profile_image") RequestBody image);


    //Creating a rest adapter
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constant.domain_)
            .addConverterFactory(new NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build();


}
