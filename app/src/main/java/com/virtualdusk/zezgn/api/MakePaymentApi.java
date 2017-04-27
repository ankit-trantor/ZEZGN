package com.virtualdusk.zezgn.api;


import com.google.gson.JsonElement;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import com.virtualdusk.zezgn.Model.NullOnEmptyConverterFactory;
import com.virtualdusk.zezgn.Model.Payment;
import com.virtualdusk.zezgn.Model.RegisterINRequest;
import com.virtualdusk.zezgn.Utilities.Constant;

/**
 * Created by hitesh.mathur on 8/9/2016.
 */
public interface MakePaymentApi {

  //  @POST("orders/make-payment.json")

   // Call<JsonElement> makePaymentViaJSON(@Body Payment body);

    @FormUrlEncoded
    @POST("orders/make-payment.json")
    Call<JsonElement> makePaymentViaJSON(@Field("total_amount") String total_amount,
                                        @Field("card_type") String card_type,
                                         @Field("card_no") String card_no,
                                        @Field("exp_month") String exp_month,
                                         @Field("exp_year") String exp_year,
                                        @Field("cvv") String cvv);

    //Creating a rest adapter
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constant.domain_)
            .addConverterFactory(new NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build();


}
