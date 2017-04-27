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
 * Created by hitesh.mathur on 8/9/2016.
 */
public interface AddAddressApi {

    @FormUrlEncoded
    @POST("addresses/set-address.json")
    Call<JsonElement> setAddressViaJSON(@Field("user_id") String user_id,
                                        @Field("address_type") String address_type, @Field("address") String address,
                                        @Field("city") String city, @Field("state") String state,
                                        @Field("country_id") String country_id,
                                        @Field("zipcode") String zipcode);

    //Creating a rest adapter
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constant.domain_)
            .addConverterFactory(new NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build();

//    Url :  http://192.168.1.39/zezign/webservice/addresses/ set- address.json
//
//
//    Method : Post
//
//    Request :
//    {
//        "user_id": "46",
//            address_type :  “office 2”
//        address :  “31-32 al-bagdadi road, ibne- batuta mall”
//        city : “riyad”
//        state : “makka”
//        country_id :  “25”
//        zipcode :  “AL-5685”
//
//    }

}
