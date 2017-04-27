package com.virtualdusk.zezgn.api;


import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import com.virtualdusk.zezgn.Model.CategoryList;
import com.virtualdusk.zezgn.Model.NullOnEmptyConverterFactory;
import com.virtualdusk.zezgn.Utilities.Constant;

/**
 * Created by hitesh.mathur on 8/9/2016.
 */
public interface FavoriteDesignApi {


    @POST("designs/add-to-favorite.json")
//    Call<JsonElement> createUserViaJSON(@Body SignInRequest body);

    //http://192.168.1.39/zezign/webservice/designs/add-to-favorite.json
    Call<JsonElement> getDesignsViaJSON(@Body CategoryList body);
    //Creating a rest adapter
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constant.domain_)
            .addConverterFactory(new NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build();


}
