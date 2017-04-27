package com.virtualdusk.zezgn.activity.ShoopingCart;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.virtualdusk.zezgn.InterfaceClasses.DialogInterface;
import com.virtualdusk.zezgn.Model.CartInfo;
import com.virtualdusk.zezgn.MyApplication;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Constant;
import com.virtualdusk.zezgn.Utilities.SaveRecords;
import com.virtualdusk.zezgn.Utilities.Utilities;
import com.virtualdusk.zezgn.activity.CustomDesignActivity;
import com.virtualdusk.zezgn.activity.Home;
import com.virtualdusk.zezgn.activity.Shipping.AddGuestShippingDetailsActivity;
import com.virtualdusk.zezgn.activity.Shipping.AddGuestShippingDetailsFragment;
import com.virtualdusk.zezgn.activity.Shipping.ShippingDetailsFragment;
import com.virtualdusk.zezgn.activity.Shipping.ShippingDetailsScreen;
import com.virtualdusk.zezgn.api.ApplyCouponApi;
import com.virtualdusk.zezgn.api.DeleteShoppingCartApi;
import com.virtualdusk.zezgn.api.GetCartApi;
import com.virtualdusk.zezgn.api.UpdateQuantityCartApi;
import com.virtualdusk.zezgn.utils.ConnectionDetector;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShoppingCartFragment extends Fragment implements DialogInterface {


    private String user_id;
    private String TAG = ShoppingCartActivity.class.getSimpleName().toString();
    public static String image;
    //private Gallery gallery;
    private RecyclerView RV_Cart;
    static TextView mDotsText[];
    private LinearLayout mDotsLayout;
    private CartAdapterAdapter cartAdapter;
    private TextView tvSubTotal, tvPromo, tvNoItem, tvCouponcode,tvPromoValue;
    private ImageView ivPromo, ivBack, ivRemove;
    private Button btnCheckOut, btnContinue;

    public static int  mDotsCount;
    public static float totalPrice = 0, total_shipping = 0, TotalCartPrice, couponAmount = 0;
    private TextView tvShipping, tvOrderTotalValue;
    public static String strCouponCode = "";

    private ScrollView mScrollView;
    private Utilities mUtilities;
    private DialogInterface mDialogInterface;
    private int deletePosition;

    private ProgressDialog progress_dialog;

    private EditText edPromoCode;
    private String mPromoCode;
    private ViewGroup vg;
    private int deviceWidth;

    private RecyclerView RV_PriceList;
    private int editPosition;

    public ShoppingCartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

       // getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        return inflater.inflate(R.layout.cart_summary_screen, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progress_dialog = new ProgressDialog(getActivity(), R.style.MyTheme);
        progress_dialog.setCancelable(false);
        progress_dialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.my_progress_indeterminate));
        mUtilities = new Utilities();
        mDialogInterface = this;
        user_id = Home.sharedPreferences.getString(getString(R.string.key_user_id), null);
        if (user_id.equalsIgnoreCase("-1")) {
            user_id = mUtilities.getDeviceId(getActivity());
        }
        findRes(view);

        if (new ConnectionDetector(getActivity()).isConnectingToInternet()) {

            getCart();
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
    }


    private void findRes(View v) {

        vg = (ViewGroup) v.findViewById(R.id.image_count);

        mScrollView = (ScrollView) v.findViewById(R.id.scrollView);
        //gallery = (Gallery) findViewById(R.id.gallery);
        RV_Cart = (RecyclerView) v.findViewById(R.id.rv_cart);
        LinearLayoutManager FontsLayoutManagaer
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        RV_Cart.setLayoutManager(FontsLayoutManagaer);

        tvSubTotal = (TextView) v.findViewById(R.id.tvSubTotal);
        tvCouponcode = (TextView) v.findViewById(R.id.tvCouponcode);
        tvPromoValue = (TextView) v.findViewById(R.id.tvPromoValue);
        mDotsLayout = (LinearLayout) v.findViewById(R.id.image_count);
        tvShipping = (TextView) v.findViewById(R.id.tvShipping);
        tvNoItem = (TextView) v.findViewById(R.id.tvNoItem);
        tvOrderTotalValue = (TextView) v.findViewById(R.id.tvOrderTotalValue);
        tvPromo = (TextView) v.findViewById(R.id.tvPromo);
        edPromoCode = (EditText) v.findViewById(R.id.edPromoCode);
        ivPromo = (ImageView) v.findViewById(R.id.ivPromo);
        ivRemove = (ImageView) v.findViewById(R.id.ivRemoveCode);
        ivBack = (ImageView) v.findViewById(R.id.ivBack);
        btnCheckOut = (Button) v.findViewById(R.id.btnChekOut);
        btnContinue = (Button) v.findViewById(R.id.btnContinue);
        mScrollView.setVisibility(View.GONE);

        btnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (user_id.equalsIgnoreCase(mUtilities.getDeviceId(getActivity()))) {
                    // startActivity(new Intent(getActivity(), AddGuestShippingDetailsActivity.class));
                    getFragmentManager().beginTransaction().add(R.id.frame, new AddGuestShippingDetailsFragment()).addToBackStack(null).commit();
                } else {

                    getFragmentManager().beginTransaction().add(R.id.frame, new ShippingDetailsFragment()).addToBackStack(null).commit();
                    //startActivity(new Intent(getActivity(), ShippingDetailsScreen.class));
                }

            }
        });
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Home.RL_TopBar.setVisibility(View.VISIBLE);
                getFragmentManager().popBackStack();
            }
        });

//        ivBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onBackPressed();
//            }
//        });
//        gallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView adapterView, View view, int pos, long l) {
//
//                for (int i = 0; i < mDotsCount; i++) {
//                    ShoppingCartActivity.mDotsText[i]
//                            .setTextColor(Color.GRAY);
//                }
//
//                ShoppingCartActivity.mDotsText[pos]
//                        .setTextColor(Color.BLACK);
////                        .setTextColor(Color.WHITE);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView adapterView) {
//
//            }
//        });
        ivPromo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPromoCode = edPromoCode.getText().toString();
                if (mPromoCode.length() == 0) {
                    show("Please enter Promo Code");
                } else {

                    if (new ConnectionDetector(getActivity()).isConnectingToInternet()) {

                        applyCoupon();
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                    }

                }

            }
        });
        ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ivPromo.setVisibility(View.VISIBLE);
                ivRemove.setVisibility(View.GONE);
                tvCouponcode.setText("Coupon Removed");
                edPromoCode.setText("");

                TotalCartPrice = TotalCartPrice + couponAmount;
                tvOrderTotalValue.setText("AED " + TotalCartPrice);
                couponAmount = 0;
                strCouponCode = "";

                tvPromoValue.setText("AED 00.00" );

            }
        });

    }


    private void getCart() {

        progress_dialog.show();
        //Creating an object of our api interface
        Retrofit restAdapter = GetCartApi.retrofit;
        GetCartApi registerApi = restAdapter.create(GetCartApi.class);

        Call<JsonElement> call = registerApi.getCart(getParms());

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.e(TAG, "onResponse: " + response.body());
                progress_dialog.dismiss();
                parseResponse(response.body().toString());
            }

            @Override
            public void onFailure(Call<JsonElement> result, Throwable t) {
                Log.e(TAG, "onFailure: " + result.toString());
            }
        });


    }

    private void updateQuantity() {
        progress_dialog.show();
        //Creating an object of our api interface
        Retrofit restAdapter = UpdateQuantityCartApi.retrofit;
        UpdateQuantityCartApi registerApi = restAdapter.create(UpdateQuantityCartApi.class);

        Call<JsonElement> call = registerApi.getCart(getCartQuantityParms());

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.e(TAG, "onResponse: UpdateQuantity:" + response.body());
                progress_dialog.dismiss();
//                parseResponse(response.body().toString());
            }

            @Override
            public void onFailure(Call<JsonElement> result, Throwable t) {
                Log.e(TAG, "onFailure: " + result.toString());
                progress_dialog.dismiss();
            }
        });


    }


    private void applyCoupon() {
        progress_dialog.show();
        //Creating an object of our api interface
        Retrofit restAdapter = ApplyCouponApi.retrofit;
        ApplyCouponApi registerApi = restAdapter.create(ApplyCouponApi.class);

        Call<JsonElement> call = registerApi.getCart(getCouponParms());

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.e(TAG, "onResponse: " + response.body());
                progress_dialog.dismiss();
                parseResponseCoupon(response.body().toString());
            }

            @Override
            public void onFailure(Call<JsonElement> result, Throwable t) {
                progress_dialog.dismiss();

                Log.e(TAG, "onFailure: " + result.toString());

            }
        });


    }

    private void deleteCart(final int pos) {
        progress_dialog.show();
        //Creating an object of our api interface
        Retrofit restAdapter = DeleteShoppingCartApi.retrofit;
        DeleteShoppingCartApi registerApi = restAdapter.create(DeleteShoppingCartApi.class);

        Call<JsonElement> call = registerApi.getCart(getDeleteCartParms());

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.e(TAG, "onResponse: " + response.body());
                progress_dialog.dismiss();
                parseResponseDeletedCart(response.body().toString(), pos);
            }

            @Override
            public void onFailure(Call<JsonElement> result, Throwable t) {
                progress_dialog.dismiss();

                Log.e(TAG, "onFailure: " + result.toString());

            }
        });


    }

    private void parseResponseCoupon(String message) {

        try {
            JSONObject jsnon = new JSONObject(message);
            String response1 = jsnon.getString("response");
            JSONObject jsonObject = new JSONObject(response1);
            String message_ = jsonObject.getString("message");
            String status = jsonObject.getString("code");
            if (status.equals("200")) {
                String data = jsonObject.getString("data");
                JSONObject jsonObject1 = new JSONObject(data);

                String coupon_discount_type = jsonObject1.getString("coupon_discount_type");
                String coupon_amount_or_percentage = jsonObject1.getString("coupon_amount_or_percentage");
                strCouponCode = jsonObject1.getString("coupon_code");
                tvCouponcode.setText("Coupon " + strCouponCode + "Applied");
                tvCouponcode.setVisibility(View.VISIBLE);

                if (coupon_discount_type != null) {

                    if (coupon_discount_type.equalsIgnoreCase("fixed")) {

                        try {

                            couponAmount = Integer.parseInt(coupon_amount_or_percentage);
                            TotalCartPrice = TotalCartPrice - couponAmount;

                            tvOrderTotalValue.setText("AED " +  String.format("%.2f", TotalCartPrice));
                            tvPromoValue.setText("-AED " +  String.format("%.2f", couponAmount));

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else if (coupon_discount_type.equalsIgnoreCase("percentage")) {

                        try {
                            float percentage = Float.parseFloat(coupon_amount_or_percentage);
                            couponAmount = (int) ((percentage / 100.0f) * TotalCartPrice);
                            TotalCartPrice = TotalCartPrice - couponAmount;
                            tvOrderTotalValue.setText("AED " + String.format("%.2f", TotalCartPrice));
                            tvPromoValue.setText("-AED " +  String.format("%.2f", couponAmount));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }


                    ivPromo.setVisibility(View.GONE);
                    ivRemove.setVisibility(View.VISIBLE);
                }


                show("Coupon code available");
                // show(message_ + "Amount of Percentage is " + coupon_amount_or_percentage);
            } else {
                show(message_);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void parseResponseDeletedCart(String message, int pos) {

        try {
            JSONObject jsnon = new JSONObject(message);
            String response1 = jsnon.getString("response");
            JSONObject jsonObject = new JSONObject(response1);
            String message_ = jsonObject.getString("message");
            String status = jsonObject.getString("code");


            if (status.equals("200")) {
                show(message_);
                arrCartItems.remove(pos);
                cartAdapter.notifyDataSetChanged();
                vg.removeAllViews();
                vg.refreshDrawableState();

                mDotsCount = RV_Cart.getAdapter().getItemCount();

                //here we create the dots
                //as you can see the dots are nothing but "."  of large size
                mDotsText = new TextView[mDotsCount];

                Log.e(TAG, "delete setAdapter:mDotsCount: " + mDotsCount);

                Home.CartCount = mDotsCount;
                if (Home.tvCart != null && Home.CartCount > 0) {

                    Home.tvCart.setText(Home.CartCount + "");
                    Home.tvCart.setVisibility(View.VISIBLE);
                }

                SaveRecords.saveIntegerToPreference(getString(R.string.key_cartcount), mDotsCount, getActivity());

                //here we set the dots
                for (int i = 0; i < mDotsCount; i++) {
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);

                    params.gravity = Gravity.CENTER;

                    mDotsText[i] = new TextView(getActivity());
                    mDotsText[i].setText(".");

                    mDotsText[i].setLayoutParams(params);
                    mDotsText[i].setTextSize(30);
                    mDotsText[i].setTypeface(null, Typeface.BOLD);
                    mDotsText[i].setTextColor(Color.GRAY);
                    mDotsLayout.addView(mDotsText[i]);
                }

                //when we scroll the images we have to set the dot that corresponds to the image to White and the others
                //will be Gray

                notifyPrice();

                if (mDotsCount == 1) {
                    mDotsLayout.setVisibility(View.GONE);
                    tvNoItem.setVisibility(View.GONE);
                    mScrollView.setVisibility(View.VISIBLE);

                    Log.d(TAG, "parseResponseDeletedCart: positioning");

                    //getActivity().recreate();
                } else if (mDotsCount > 1) {
                    mDotsLayout.setVisibility(View.GONE);
                    mScrollView.setVisibility(View.VISIBLE);
                }
                if (mDotsCount == 2) {
//                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) RV_Cart.getLayoutParams();
//                    params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//                    RV_Cart.setLayoutParams(params); //causes layout update
                }
                if (mDotsCount > 2) {
                    mDotsLayout.setVisibility(View.VISIBLE);
                }
                if (mDotsCount == 0) {
                    tvNoItem.setVisibility(View.VISIBLE);
                    mScrollView.setVisibility(View.GONE);
                    Home.tvCart.setVisibility(View.GONE);


                    SaveRecords.saveIntegerToPreference(getString(R.string.key_cartcount), mDotsCount, getActivity());
                }
                //   gallery.setSelection(2);
            } else {
                show(message_);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private ArrayList<CartInfo> arrCartItems;

    private void parseResponse(String message) {
        arrCartItems = new ArrayList<>();
        try {
            JSONObject jsnon = new JSONObject(message);
            String response1 = jsnon.getString("response");
            JSONObject jsonObject = new JSONObject(response1);
            String status = jsonObject.getString("code");
            if (status.equals("200")) {

                String data_ = jsonObject.getString("data");

                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject data = jsonArray.getJSONObject(i);
                    CartInfo cartInfo = new CartInfo();
                    cartInfo.setId(data.getString("id"));
                    cartInfo.setDevice_type(data.getString("device_type"));
                    if (data.has("is_uploaded_design") && data.getString("is_uploaded_design") != null) {

                        if(data.getString("is_uploaded_design").toString().equalsIgnoreCase("true")){
                            cartInfo.setIsUploadedDesign("1");
                        }else{
                            cartInfo.setIsUploadedDesign("0");
                        }

                    } else {
                        cartInfo.setIsUploadedDesign("");
                    }
                    cartInfo.setTitle(data.getString("title"));
                    if (data.has("product_image_url")) {
                        cartInfo.setProduct_thumb(data.getString("product_image_url"));
                    } else {
                        cartInfo.setProduct_thumb("");
                    }
                    cartInfo.setProduct_data(data.getString("product_data"));
                    cartInfo.setPrice(data.getString("price"));
                    cartInfo.setQuantity(data.getString("quantity"));

                    int order_quantity = Integer.parseInt(data.getString("quantity"));
                    int productPrice = Integer.parseInt(data.getString("price"));
                    cartInfo.setActualPrice(data.getString("price"));
                    productPrice = productPrice * order_quantity;
//                    cartInfo.setPrice(productPrice+"");

//                    cartInfo.setPrice(productPrice+"");
                    cartInfo.setUser_id(data.getString("user_id"));
                    cartInfo.setGuest_user_id(data.getString("guest_user_id"));
                    cartInfo.setDesign_id(data.getString("design_id"));
                    cartInfo.setProduct_style_id(data.getString("product_style_id"));
                    cartInfo.setProduct_id(data.getString("product_id"));
                    if (data.has("product_idx")) {
                        cartInfo.setProduct_idx(data.getString("product_idx"));
                    } else {
                        cartInfo.setProduct_idx("");
                    }

                    cartInfo.setCreated(data.optString("created"));
                    cartInfo.setModified(data.optString("modified"));
                    cartInfo.setProduct(data.optString("product"));
                    cartInfo.setDesign(data.optString("design"));
                    cartInfo.setProduct_style(data.optString("product_style"));


                    try {
                        if (data.getString("product") != null) {

                            Object json = new JSONTokener(data.getString("product")).nextValue();
                            if (json instanceof JSONObject) {
                                JSONObject jsonObject_Product = new JSONObject(data.getString("product"));

                                cartInfo.setProduct_id(jsonObject_Product.getString("id"));
                                cartInfo.setProduct_product_name(jsonObject_Product.getString("product_name"));
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    JSONObject jsonObject_design = new JSONObject(data.getString("design"));

                    cartInfo.setDesign_title(jsonObject_design.getString("title"));
                    cartInfo.setDesign_design_img(jsonObject_design.getString("design_img"));
                    cartInfo.setDesign_author_id(jsonObject_design.getString("author_id"));
                    cartInfo.setDesign_author_name(jsonObject_design.getString("author_name"));
                    cartInfo.setDesign_number_of_colors(jsonObject_design.getString("number_of_colors"));
                    cartInfo.setDesign_design_text(jsonObject_design.getString("design_text"));
                    cartInfo.setDesign_status(jsonObject_design.getString("status"));
                    cartInfo.setCreated(jsonObject_design.getString("created"));
                    cartInfo.setModified(jsonObject_design.getString("modified"));

                    JSONObject jsonObject_product_style = new JSONObject(data.getString("product_style"));

                    cartInfo.setProduct_style_id(jsonObject_product_style.getString("id"));
                    cartInfo.setProduct_style_user_id(jsonObject_product_style.getString("user_id"));
                    cartInfo.setProduct_style_product_id(jsonObject_product_style.getString("product_id"));
                    cartInfo.setProduct_style_style_name(jsonObject_product_style.getString("style_name"));
                    cartInfo.setProduct_style_style_img(jsonObject_product_style.getString("style_img"));
                    cartInfo.setProduct_style_img_printable_area(jsonObject_product_style.getString("img_printable_area"));
                    cartInfo.setProduct_style_price(jsonObject_product_style.getString("price"));
                    cartInfo.setProduct_style_status(jsonObject_product_style.getString("status"));
                    cartInfo.setProduct_style_display_order(jsonObject_product_style.getString("display_order"));
                    cartInfo.setProduct_style_created(jsonObject_product_style.getString("created"));
                    cartInfo.setProduct_style_modified(jsonObject_product_style.getString("modified"));

                    arrCartItems.add(cartInfo);


                }

                setAdapter();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private void setAdapter() {

        cartAdapter = new CartAdapterAdapter(getActivity(), arrCartItems);
        RV_Cart.setAdapter(cartAdapter);
//        gallery.setAdapter(cartAdapter);

        mDotsCount = RV_Cart.getAdapter().getItemCount();

        //here we create the dots
        //as you can see the dots are nothing but "."  of large size
        mDotsText = new TextView[mDotsCount];

        Log.e(TAG, "setAdapter:mDotsCount: " + mDotsCount);

        Home.CartCount = mDotsCount;
        SaveRecords.saveIntegerToPreference(getString(R.string.key_cartcount), mDotsCount, getActivity());
        if (Home.tvCart != null && Home.CartCount > 0) {

            Home.tvCart.setText(Home.CartCount + "");
            Home.tvCart.setVisibility(View.VISIBLE);
        }

        //here we set the dots
        for (int i = 0; i < mDotsCount; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);

            params.gravity = Gravity.CENTER;
            mDotsText[i] = new TextView(getActivity());
            mDotsText[i].setText(".");

            mDotsText[i].setTextSize(30);
            mDotsText[i].setLayoutParams(params);
            mDotsText[i].setTypeface(null, Typeface.BOLD);
            mDotsText[i].setTextColor(Color.GRAY);
            mDotsLayout.addView(mDotsText[i]);
        }

        if (mDotsCount == 1) {
            mDotsLayout.setVisibility(View.GONE);
            tvNoItem.setVisibility(View.GONE);
            mScrollView.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) RV_Cart.getLayoutParams();
            params.addRule(RelativeLayout.CENTER_HORIZONTAL);
            RV_Cart.setLayoutParams(params); //causes layout update
        } else if (mDotsCount > 1) {
            mDotsLayout.setVisibility(View.GONE);
            mScrollView.setVisibility(View.VISIBLE);
        }
        if (mDotsCount == 2) {
//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) RV_Cart.getLayoutParams();
//            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
//            RV_Cart.setLayoutParams(params); //causes layout update
        }
        if (mDotsCount > 2) {
            mDotsLayout.setVisibility(View.VISIBLE);
        }
        if (mDotsCount == 0) {
            tvNoItem.setVisibility(View.VISIBLE);
            mScrollView.setVisibility(View.GONE);
        }
        //when we scroll the images we have to set the dot that corresponds to the image to White and the others
        //will be Gray
        notifyPrice();
        // gallery.setSelection(1);


    }


    private void notifyPrice() {
        totalPrice = 0;
        total_shipping = 18;
        TotalCartPrice = 0;

        for (int i = 0; i < arrCartItems.size(); i++) {
            totalPrice = totalPrice + Float.parseFloat(arrCartItems.get(i).getPrice());
        //    total_shipping = total_shipping + 20;
//            int quantity = Integer.parseInt(arrCartItems.get(i).getQuantity());
//            if(quantity > 1){
//                for(int k=0;k<quantity-1;k++){
//                    total_shipping = total_shipping + 20;
//                }
//            }
        }



        tvShipping.setText("AED " +String.format("%.2f", total_shipping) );
        TotalCartPrice = totalPrice + total_shipping;
        tvSubTotal.setText("AED " + String.format("%.2f", totalPrice));

        tvOrderTotalValue.setText("AED " +String.format("%.2f", TotalCartPrice) );


    }

    private CartInfo getParms() {
        Log.d(TAG, "user_id " + user_id);
        CartInfo cartInfo = new CartInfo();
        cartInfo.setUser_id(user_id);


        return cartInfo;
    }


    private CartInfo getCouponParms() {

        mPromoCode = edPromoCode.getText().toString();

        CartInfo cartInfo = new CartInfo();
        cartInfo.setCoupon_code(mPromoCode);


        return cartInfo;
    }

    private String cart_product_id;

    private CartInfo getDeleteCartParms() {

        CartInfo cartInfo = new CartInfo();
        cartInfo.setCart_product_id(cart_product_id);

        return cartInfo;
    }

    private String mQuantity;

    private CartInfo getCartQuantityParms() {

        CartInfo cartInfo = new CartInfo();
        cartInfo.setCart_product_id(cart_product_id);
        cartInfo.setQuantity(mQuantity);


        return cartInfo;
    }


    public class CartAdapterAdapter extends RecyclerView.Adapter<MyviewHolder> {

        Context c;
        private LayoutInflater inflater = null;
        private ArrayList<CartInfo> arrCartItems;
        private DisplayImageOptions options;
        private ImageLoader imageLoader = null;// ImageLoader.getInstance();

        public CartAdapterAdapter(Context context, ArrayList<CartInfo> arrayList) {
            c = context;
            inflater = (LayoutInflater) c
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.arrCartItems = arrayList;
            imageLoader = ImageLoader.getInstance();
            options = new DisplayImageOptions.Builder()
                    .showStubImage(R.mipmap.logo)
                    .showImageForEmptyUri(R.mipmap.logo)
                    .showImageOnFail(R.mipmap.logo)
                    .resetViewBeforeLoading()
                    .delayBeforeLoading(1000)
                    .cacheInMemory()
                    .cacheOnDisc()
                    .build();

        }

        @Override
        public int getItemCount() {
            return arrCartItems.size();
        }

        @Override
        public MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_row_shoppoing_cart, parent, false);

            return new MyviewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(MyviewHolder holder, final int position) {

            holder.tvDesignName.setText(arrCartItems.get(position).getDesign_title());
            holder.tvTotalProductQuantity.setText(arrCartItems.get(position).getQuantity());
            float st = Float.parseFloat(arrCartItems.get(position).getPrice());
            holder.tvTotalPrice.setText("AED " + String.format("%.2f",st ));

            holder.tvQuantityIncrease.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int quantity = Integer.parseInt(arrCartItems.get(position).getQuantity());
                    if (quantity >= 1) {
                        increaseQuantitySize(position, arrCartItems.get(position).getQuantity(), arrCartItems.get(position).getPrice(), 1);

                    }
                }
            });
            holder.ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    cart_product_id = arrCartItems.get(position).getId();
                    Log.e(TAG, "onClick: " + cart_product_id);
                    mUtilities.showCancelableDialog(getActivity(), "Do you want to delete this product?", "Delete",
                            mDialogInterface, "delete", "delete");

                    deletePosition = position;

                }
            });

            holder.ivCartEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Constant.SaveButton = "no";
                    mUtilities.showCancelableDialog(getActivity(), "Do you want to edit this product?", "Edit",
                            mDialogInterface, "edit", "edit");

                    editPosition = position;



                }
            });

           image = arrCartItems.get(position).getProduct_thumb();
            Log.d(TAG, "onBindViewHolder: Constant.image " + image);

            if (image != null && !image.isEmpty()) {
                imageLoader.displayImage(image, holder.ivDesignPic);
            }
            holder.tvQuantityLess.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int quantity = Integer.parseInt(arrCartItems.get(position).getQuantity());
                    if (quantity >= 2) {
                        increaseQuantitySize(position, arrCartItems.get(position).getQuantity(), arrCartItems.get(position).getPrice(), 0);

                    }
                }
            });

        }

        @Override
        public long getItemId(int i) {
            return i;
        }


    }

    public class MyviewHolder extends RecyclerView.ViewHolder {

        private TextView tvDesignName, tvTotalPrice, tvTotalProductQuantity, tvQuantityLess, tvQuantityIncrease;
        private ImageView ivDelete, ivDesignPic,ivCartEdit;
        private RelativeLayout RlParentSupport;


        public MyviewHolder(View vi) {
            super(vi);

            tvDesignName = (TextView) vi.findViewById(R.id.tvDesignName);
            tvTotalPrice = (TextView) vi.findViewById(R.id.tvTotalPrice);
            tvTotalProductQuantity = (TextView) vi.findViewById(R.id.tvTotalProductQuantity);
            tvQuantityLess = (TextView) vi.findViewById(R.id.tvQuantityLess);
            tvQuantityIncrease = (TextView) vi.findViewById(R.id.tvQuantityIncrease);
            ivDelete = (ImageView) vi.findViewById(R.id.ivFavorite);
            ivDesignPic = (ImageView) vi.findViewById(R.id.ivDesignPic);
            ivCartEdit = (ImageView) vi.findViewById(R.id.img_editcart);
            RlParentSupport = (RelativeLayout) vi.findViewById(R.id.rlParentLyt);

            DisplayMetrics metrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

            deviceWidth = metrics.widthPixels;
            float height = getActivity().getResources().getDimension(R.dimen.cart_height);
            float Width_10 = getActivity().getResources().getDimension(R.dimen.margin_10);
            Log.d(TAG, "MyviewHolder: deviceWidth " + deviceWidth + "height " + height);
            RlParentSupport.setLayoutParams(new RelativeLayout.LayoutParams(
                    ((deviceWidth / 2) - (int) Width_10), (int) height));

        }


    }




    private void increaseQuantitySize(int i, String quantity, String price, int type) {

        CartInfo cartInfo = new CartInfo();
        cartInfo.setDevice_type(arrCartItems.get(i).getDevice_type());
        cartInfo.setProduct_style_id(arrCartItems.get(i).getProduct_style_id());
        cartInfo.setId(arrCartItems.get(i).getId());
        cartInfo.setTitle(arrCartItems.get(i).getTitle());
        cartInfo.setProduct_thumb(arrCartItems.get(i).getProduct_thumb());
        cartInfo.setProduct_data(arrCartItems.get(i).getProduct_data());
        cartInfo.setActualPrice(arrCartItems.get(i).getActualPrice());
        cartInfo.setUser_id(arrCartItems.get(i).getUser_id());
        cartInfo.setGuest_user_id(arrCartItems.get(i).getGuest_user_id());
        cartInfo.setDesign_id(arrCartItems.get(i).getDesign_id());
        cartInfo.setProduct_style_id(arrCartItems.get(i).getProduct_style_id());
        cartInfo.setProduct_id(arrCartItems.get(i).getProduct_id());
        cartInfo.setProduct_idx(arrCartItems.get(i).getProduct_idx());
        cartInfo.setCreated(arrCartItems.get(i).getCreated());
        cartInfo.setModified(arrCartItems.get(i).getModified());
        cartInfo.setProduct(arrCartItems.get(i).getProduct());
        cartInfo.setDesign(arrCartItems.get(i).getDesign());
        cartInfo.setProduct_style(arrCartItems.get(i).getProduct_style());
        cartInfo.setProduct_id(arrCartItems.get(i).getProduct_id());
        cartInfo.setProduct_product_name(arrCartItems.get(i).getProduct_product_name());
        cartInfo.setDesign_title(arrCartItems.get(i).getDesign_title());
        cartInfo.setDesign_design_img(arrCartItems.get(i).getDesign_design_img());
        cartInfo.setDesign_author_id(arrCartItems.get(i).getDesign_author_id());
        cartInfo.setDesign_author_name(arrCartItems.get(i).getDesign_author_name());
        cartInfo.setDesign_number_of_colors(arrCartItems.get(i).getDesign_number_of_colors());
        cartInfo.setDesign_design_text(arrCartItems.get(i).getDesign_design_text());
        cartInfo.setDesign_status(arrCartItems.get(i).getDesign_status());
        cartInfo.setCreated(arrCartItems.get(i).getCreated());
        cartInfo.setModified(arrCartItems.get(i).getModified());
        cartInfo.setProduct_style_id(arrCartItems.get(i).getProduct_style_id());
        cartInfo.setProduct_style_user_id(arrCartItems.get(i).getProduct_style_user_id());
        cartInfo.setProduct_style_product_id(arrCartItems.get(i).getProduct_style_product_id());
        cartInfo.setProduct_style_style_name(arrCartItems.get(i).getProduct_style_style_name());
        cartInfo.setProduct_style_style_img(arrCartItems.get(i).getProduct_style_style_img());
        cartInfo.setProduct_style_img_printable_area(arrCartItems.get(i).getProduct_style_img_printable_area());
        cartInfo.setProduct_style_price(arrCartItems.get(i).getProduct_style_price());
        cartInfo.setProduct_style_status(arrCartItems.get(i).getProduct_style_status());
        cartInfo.setProduct_style_display_order(arrCartItems.get(i).getProduct_style_display_order());
        cartInfo.setProduct_style_created(arrCartItems.get(i).getProduct_style_created());
        cartInfo.setProduct_style_modified(arrCartItems.get(i).getProduct_style_modified());


        if (type == 0) // decrease
        {
            int order_quantity = Integer.parseInt(quantity) - 1;
            int productPrice = Integer.parseInt(arrCartItems.get(i).getActualPrice());

            productPrice = productPrice * order_quantity;
            cartInfo.setPrice(productPrice + "");

            cartInfo.setQuantity(order_quantity + "");
            mQuantity = order_quantity + "";
            cart_product_id = arrCartItems.get(i).getId();

        } else {
            int order_quantity = Integer.parseInt(quantity) + 1;
            int productPrice = Integer.parseInt(arrCartItems.get(i).getActualPrice());

            productPrice = productPrice * order_quantity;
            cartInfo.setPrice(productPrice + "");

            cartInfo.setQuantity(order_quantity + "");
            mQuantity = order_quantity + "";
            cart_product_id = arrCartItems.get(i).getId();


        }

        arrCartItems.set(i, cartInfo);

        if (new ConnectionDetector(getActivity()).isConnectingToInternet()) {

            updateQuantity();
            cartAdapter.notifyDataSetChanged();
            notifyPrice();

        } else {
            Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }


    }

    @Override
    public void onCancelButtonClick(String DialogName, String Message) {

    }

    @Override
    public void onOkButtonClick(String DialogName, String Message) {

        if (Message.equalsIgnoreCase("delete")) {

            if (new ConnectionDetector(getActivity()).isConnectingToInternet()) {

                deleteCart(deletePosition);
            } else {
                Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            }

        }else if(Message.equalsIgnoreCase("edit")){

            String device_type = arrCartItems.get(editPosition).getDevice_type();
            if(device_type.equalsIgnoreCase("Android Device"))
            {
                String strId = arrCartItems.get(editPosition).getId();
                String strTitle = arrCartItems.get(editPosition).getTitle();
                String strProductView = arrCartItems.get(editPosition).getProduct_data();
                String strProductId = arrCartItems.get(editPosition).getProduct_id();
                String strDesignId = arrCartItems.get(editPosition).getDesign_id();
                String strprodutcIdX = arrCartItems.get(editPosition).getProduct_idx();
                String strProductStyleId = arrCartItems.get(editPosition).getProduct_style_product_id();
                String strUserId = arrCartItems.get(editPosition).getUser_id();
                String strDesignCategoryId = arrCartItems.get(editPosition).getDesign_category_id();
                String strDesignTitle = arrCartItems.get(editPosition).getDesign_title();
                String strDesignImage = arrCartItems.get(editPosition).getDesign_design_img();
                String strModifiedDate = arrCartItems.get(editPosition).getModified();
                String strIsUploadedDesign = arrCartItems.get(editPosition).getIsUploadedDesign();

                Intent mIntent = new Intent(getActivity(), CustomDesignActivity.class);
                mIntent.putExtra(Constant.IS_EDIT, true);
                mIntent.putExtra(Constant.IS_EDIT_CART, true);
                mIntent.putExtra(Constant.STR_ID, strId);
                mIntent.putExtra(Constant.STR_TITLE, strTitle);
                mIntent.putExtra(Constant.STR_PRODUCT_VIEW, strProductView);
                mIntent.putExtra(Constant.STR_PRODUCT_ID, strProductId);
                mIntent.putExtra(Constant.STR_DESIGN_ID, strDesignId);
                mIntent.putExtra(Constant.STR_PRODUCT_ID_X, strprodutcIdX);
                mIntent.putExtra(Constant.STR_PRODUCT_STYLE_ID, strProductStyleId);
                mIntent.putExtra(Constant.STR_USER_ID, strUserId);
                mIntent.putExtra(Constant.STR_DESIGN_CATEGORY_ID, strDesignCategoryId);
                mIntent.putExtra(Constant.STR_DESIGN_TITLE, strDesignTitle);
                mIntent.putExtra(Constant.STR_DESIGN_IMAGE, strDesignImage);
                mIntent.putExtra(Constant.STR_MODIFIED_DATE, strModifiedDate);
                mIntent.putExtra(Constant.STR_IS_DESIGN_UPLOADED, strIsUploadedDesign);
                startActivity(mIntent);
            }
            else
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("ZEZIGN");
                alert.setMessage("You Can Only Edit This Product in "+device_type);
                alert.setPositiveButton("OK",null);
                alert.show();
            }

        }
    }

    public void show(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    public void onWindowFocusChanged(boolean hasFocus) {

    }


}
