package com.virtualdusk.zezgn.api;


import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import com.virtualdusk.zezgn.Model.CartInfo;
import com.virtualdusk.zezgn.Model.NullOnEmptyConverterFactory;
import com.virtualdusk.zezgn.Model.addOrder;
import com.virtualdusk.zezgn.Utilities.Constant;

/**
 * Created by hitesh.mathur on 8/9/2016.
 */
public interface postOrderApi {


    @POST("orders/add-order.json")
//    Call<JsonElement> addOrder(@Field("user_id") String user_id,
//                                        @Field("shipping_prices") String shipping_prices, @Field("payments") String payments,
//                                        @Field("order") String order);
    Call<JsonElement> addOrder(@Body addOrder body);

    //Creating a rest adapter
    Retrofit retrofit = new Retrofit.Builder()
           // .baseUrl("http://192.168.1.39/zezign/webservice/")
            .baseUrl(Constant.domain_)
            .addConverterFactory(new NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build();


}
