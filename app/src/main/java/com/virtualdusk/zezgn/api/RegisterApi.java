package com.virtualdusk.zezgn.api;


import com.google.gson.JsonElement;


import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import com.virtualdusk.zezgn.Model.NullOnEmptyConverterFactory;
import com.virtualdusk.zezgn.Model.RegisterINRequest;
import com.virtualdusk.zezgn.Model.SignInRequest;
import com.virtualdusk.zezgn.Utilities.Constant;

/**
 * Created by hitesh.mathur on 8/9/2016.
 */
public interface RegisterApi {

    @POST("users/add.json")
//    @Headers("Content-Type: application/json")
    //    http://192.168.1.39/zezign/webservice/users/login.json

    Call<JsonElement> createUserViaJSON(@Body RegisterINRequest body);

    //Creating a rest adapter
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constant.domain_)
            .addConverterFactory(new NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build();


}
