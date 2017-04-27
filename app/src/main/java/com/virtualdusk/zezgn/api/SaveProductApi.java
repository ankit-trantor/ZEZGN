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
public interface SaveProductApi {
    @Multipart
    @POST("savedproducts/save-product.json")

    Call<JsonElement> saveProduct(@Part("title") RequestBody title,
                                  @Part("product_view") RequestBody product_view,
                                  @Part("product_image\"; filename=\"pp.png\" ") RequestBody file,
                                //  @Part("product_img") RequestBody file,
                                  @Part("design_id") RequestBody design_id,
                                  @Part("product_id") RequestBody product_id,
                                  @Part("product_style_id") RequestBody product_style_id,
                                  @Part("user_id") RequestBody user_id,
                                  @Part("device_type") RequestBody device_type,
                                 @Part("is_uploaded_design") RequestBody isUploadedDesign
                                  );

    //Creating a rest adapter
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(Constant.domain_)

            .addConverterFactory(new NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build();


}
