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
public interface AddToCartApi {
    @Multipart
    @POST("mycarts/add-to-cart.json")

    //        Call<JsonElement> call = addToCartApi.addToCart
        // (title, product_thumb, product_data, price, quantity, user_id, design_id, product_style_id, product_id);

    //    Call<JsonElement> editUser (@Part("profile_image\"; filename=\"pp\" ") RequestBody file , @Part("user_id") RequestBody id);

    Call<JsonElement> addToCart(@Part("title") RequestBody title,
                                @Part("product_image\"; filename=\"pp.png\" ") RequestBody file,
                              //  @Part("product_thumb") RequestBody file,
                                @Part("product_data") RequestBody product_view,
                                @Part("price") RequestBody price,
                                @Part("quantity") RequestBody quantity,
                                @Part("user_id") RequestBody user_id,
                                @Part("design_id") RequestBody design_id,
                                @Part("product_id") RequestBody product_style_id,
                                @Part("product_style_id") RequestBody product_id,
                                @Part("is_uploaded_design") RequestBody isUploadedDesign,
                                @Part("device_type") RequestBody device_type);


    @Multipart
    @POST("mycarts/update-cart-design.json")
    Call<JsonElement> updateCart(@Part("cart_product_id") RequestBody cart_product_id,
                                 @Part("title") RequestBody title,
                                @Part("product_image\"; filename=\"pp.png\" ") RequestBody file,
                                //  @Part("product_thumb") RequestBody file,
                                @Part("product_data") RequestBody product_view,
                                @Part("price") RequestBody price,
                                @Part("quantity") RequestBody quantity,
                                @Part("user_id") RequestBody user_id,
                                @Part("design_id") RequestBody design_id,
                                @Part("product_id") RequestBody product_style_id,
                                @Part("product_style_id") RequestBody product_id,
                                @Part("device_type") RequestBody device_type);
                             //    @Part("is_uploaded_design") RequestBody isUploadedDesign);



    //Creating a rest adapter
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constant.domain_)
            .addConverterFactory(new NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build();


}
