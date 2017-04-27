package com.virtualdusk.zezgn.activity.CategoryScreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.virtualdusk.zezgn.Model.CategoryList;
import com.virtualdusk.zezgn.Model.DesignsList;
import com.virtualdusk.zezgn.Product.Product;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Constant;
import com.virtualdusk.zezgn.Utilities.Utilities;
import com.virtualdusk.zezgn.activity.CustomDesignActivity;
import com.virtualdusk.zezgn.activity.DesignActivity;
import com.virtualdusk.zezgn.activity.Home;
import com.virtualdusk.zezgn.activity.HomeFragment;
import com.virtualdusk.zezgn.activity.Products.ProductFragment;
import com.virtualdusk.zezgn.activity.UpdateFragments;
import com.virtualdusk.zezgn.api.DesignListApi;
import com.virtualdusk.zezgn.api.FavoriteDesignApi;
import com.virtualdusk.zezgn.utils.ConnectionDetector;

/**
 * Created by Amit Sharma on 10/6/2016.
 */
public class DesignDetailScreen extends Fragment implements View.OnClickListener {
    private DisplayImageOptions options;
    private ImageLoader imageLoader = null;// ImageLoader.getInstance();
    private String TAG = DesignDetailScreen.class.getSimpleName().toString();

    private String mId, mTitle, mDesignImag, mAuthorName, mNoOfColors, mDesignText, mCreated, mDesignCategory, mIsFavorite;

    private TextView tvCategoryNameValue, tvColorValues, tvAuthorValue, tvCreatedDate, tvDesignTextValue, tvDesignName;
    private ImageView ivDesignPic, ivFavorite;
    private Button btnNext;
    SharedPreferences prfs;
    private String user_id;
    private Utilities mUtilities;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.design_name_screen_, container, false);
        //  ((Home)view.getContext()).setTitle("DESIGN NAME");

        user_id = Home.sharedPreferences.getString(getString(R.string.key_user_id), null);
        mUtilities = new Utilities();
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

        mId = getArguments().getString("id");
        mTitle = getArguments().getString("title");
        mDesignImag = getArguments().getString("design_img");
        mAuthorName = getArguments().getString("author_name");
        mNoOfColors = getArguments().getString("number_of_colors");
        mDesignText = getArguments().getString("design_text");
        mCreated = getArguments().getString("created");
        mDesignCategory = getArguments().getString("design_category");
        mIsFavorite = getArguments().getString("is_favorite");


        findRes(view);

        Log.e(TAG, "mIsFavorite: " + mIsFavorite);

        if (mIsFavorite.equalsIgnoreCase("1")) {
            ivFavorite.setImageResource(R.mipmap.favorite_hb);

            //Unset favorite
        } else {
            ivFavorite.setImageResource(R.mipmap.favorite_h);

            //Set  favorite
        }

        return view;
    }

    private void findRes(View view) {

        tvCategoryNameValue = (TextView) view.findViewById(R.id.tvCategoryNameValue);
        tvColorValues = (TextView) view.findViewById(R.id.tvColorValues);
        tvAuthorValue = (TextView) view.findViewById(R.id.tvAuthorValue);
        tvCreatedDate = (TextView) view.findViewById(R.id.tvCreatedDate);
        tvDesignTextValue = (TextView) view.findViewById(R.id.tvDesignTextValue);
        tvDesignName = (TextView) view.findViewById(R.id.tvDesignName);
        ivDesignPic = (ImageView) view.findViewById(R.id.ivDesignPic);
        ivFavorite = (ImageView) view.findViewById(R.id.ivFavorite);
        // btnBack=(Button)view.findViewById(R.id.btnBack);
        btnNext = (Button) view.findViewById(R.id.btnNext);

        tvColorValues.setText(mNoOfColors);
        tvAuthorValue.setText(mAuthorName);
        tvDesignTextValue.setText(mDesignText);

        tvCategoryNameValue.setText(mDesignCategory);
        tvCreatedDate.setText(mCreated);
        tvDesignName.setText(mTitle);


        imageLoader.displayImage(Constant.IMAGE_BASE_URL_Designs + mDesignImag, ivDesignPic,
                options, new ImageLoadingListener() {

                    public void onLoadingCancelled(String imageUri,
                                                   View view) {
                        ivDesignPic.setImageResource(R.mipmap.logo);
                    }

                    public void onLoadingComplete(String arg0,
                                                  View arg1, Bitmap arg2) {


                    }

                    public void onLoadingStarted(String arg0, View arg1) {
                        // TODO Auto-generated method stub

                        ivDesignPic.setImageResource(R.mipmap.logo);
                    }

                    @Override
                    public void onLoadingFailed(String arg0, View arg1,
                                                FailReason arg2) {
                        // TODO Auto-generated method stub

                        ivDesignPic.setImageResource(R.mipmap.logo);
                    }
                });
//btnBack.setOnClickListener(new View.OnClickListener() {
//    @Override
//    public void onClick(View view) {
//        Home.fab.show();
//       // UpdateFragments.backSetFragment(getActivity().getSupportFragmentManager().beginTransaction(), R.id.frame);
//        getActivity().finish();
//
//    }
//});


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(ProductFragment.strFromproduct.equalsIgnoreCase("cat")){
                    ProductFragment.strFromproduct = "";
                    Intent mIntent = new Intent(getActivity(), CustomDesignActivity.class);
                    mIntent.putExtra("product_id", ProductFragment.strProductId);
                    mIntent.putExtra("design_id", mId);
                    mIntent.putExtra("design_text", mTitle);
                    mIntent.putExtra("design_image", Constant.IMAGE_BASE_URL_Designs + mDesignImag);
                    startActivity(mIntent);
                }else{

                    Home.tvTitle.setText("CHOOSE PRODUCT");
                    Bundle bundle = new Bundle();
                    bundle.putString("id", mId);
                    bundle.putString("design_text", mTitle);
                    bundle.putString("design_img", Constant.IMAGE_BASE_URL_Designs + mDesignImag);
                    Home.strKey = "design";
                    ProductFragment product = new ProductFragment();
                    product.setArguments(bundle);
                    getFragmentManager().beginTransaction().replace(R.id.frame, product).addToBackStack(null).commit();
                }


                //UpdateFragments.setFragment(getActivity().getSupportFragmentManager().beginTransaction(), product, R.id.frame);


//        ProductFragment product=new ProductFragment();
//        Bundle bundle=new Bundle();
//
//        bundle.putString("id",mId);
//        bundle.putString("design_image",Constant.IMAGE_BASE_URL_Designs + mDesignImag);
//        product.setArguments(bundle);
//
//        getFragmentManager().beginTransaction().add(R.id.framelayout_designactivity,product).addToBackStack(null).commit();
                //UpdateFragments.setFragment(getActivity().getSupportFragmentManager().beginTransaction(),product, R.id.framelayout_designactivity);

            }
        });
        ivFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(user_id.equalsIgnoreCase("-1")){
                    mUtilities.showLoginDialog(getActivity(),getActivity().getString(R.string.alert_login_fav));
                }else {


                    if (new ConnectionDetector(getActivity()).isConnectingToInternet()) {

                        setFavoriteDesign();
                    } else {
                        Toast.makeText(getActivity(),getString(R.string.no_internet),Toast.LENGTH_LONG).show();
                    }




                }

            }
        });


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
//            case R.id.btnBack:
//                UpdateFragments.backSetFragment(getActivity().getSupportFragmentManager().beginTransaction(), R.id.frame);
//
//                break;
            case R.id.btnNext:


//                Intent mIntent = new Intent(getActivity(), DesignActivity.class);
//                getActivity().startActivity(mIntent);
//                ProductFragment product=new ProductFragment();
//                UpdateFragments.setFragment(getActivity().getSupportFragmentManager().beginTransaction(),product, R.id.frame);

                break;
        }
    }

    private void setFavoriteDesign() {
        //Creating an object of our api interface
        Retrofit restAdapter = FavoriteDesignApi.retrofit;
        FavoriteDesignApi registerApi = restAdapter.create(FavoriteDesignApi.class);

        Call<JsonElement> call = registerApi.getDesignsViaJSON(getParams());

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.e(TAG, "onResponse: " + response.body());

                /*{
                    "response":{
                    "message":"Design Successfully Added to Favorite List.",
                            "url":"http://in2.endivesoftware.com/sites/zezign/webservice/designs/add-to-favorite.json",
                            "code":200
                }
                }
                */

                try {
                    JSONObject jsnon = new JSONObject(response.body().toString());
                    String response1 = jsnon.getString("response");
                    JSONObject jsonObject = new JSONObject(response1);
                    String status = jsonObject.getString("code");
                    String message = jsonObject.getString("message");
                    if (status.equals("200")) {

                        showToast(message);
                        //HomeFragment.isCallApi = true;

//                        setAdapter();
                    } else {
                        showToast(message);

                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<JsonElement> result, Throwable t) {
                Log.e(TAG, "onFailure: " + result.toString());
            }
        });

    }

    private void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private CategoryList getParams() {


        if (mIsFavorite.equalsIgnoreCase("1")) {
            mIsFavorite = "0";
            //Unset favorite
            ivFavorite.setImageResource(R.mipmap.favorite_h);
        } else {
            mIsFavorite = "1";
            ivFavorite.setImageResource(R.mipmap.favorite_hb);
            //Set  favorite
        }
        CategoryList categoryList = new CategoryList();

        categoryList.setUser_id(user_id);
        categoryList.setDesign_id(mId);
        categoryList.setIs_fav(mIsFavorite);

//        user_id” : 1,,
//        “design_id” : 4,
//        “is_fav” : 0/1,


        return categoryList;
    }

    @Override
    public void onResume() {
        super.onResume();
        Home.tvTitle.setText("DESIGN");
    }
}
