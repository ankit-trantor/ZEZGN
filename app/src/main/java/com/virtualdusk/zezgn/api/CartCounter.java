package com.virtualdusk.zezgn.api;

import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import com.virtualdusk.zezgn.Model.NullOnEmptyConverterFactory;
import com.virtualdusk.zezgn.Utilities.Constant;

/**
 * Created by bhart.gupta on 20-Feb-17.
 */

public interface CartCounter {


    @FormUrlEncoded
    @POST("mycarts/get-cart-count.json")
    Call<JsonElement> getCartCounter(@Field("user_id") String user_id);

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constant.domain_)
            .addConverterFactory(new NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
