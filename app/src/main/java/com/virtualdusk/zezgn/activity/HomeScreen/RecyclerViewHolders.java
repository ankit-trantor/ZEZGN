package com.virtualdusk.zezgn.activity.HomeScreen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.virtualdusk.zezgn.InterfaceClasses.FavoriteInterface;
import com.virtualdusk.zezgn.InterfaceClasses.onDesignClickListner;
import com.virtualdusk.zezgn.Model.CategoryList;
import com.virtualdusk.zezgn.Model.DesignsList;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Utilities;
import com.virtualdusk.zezgn.activity.CategoryScreen.DesignDetailScreen;
import com.virtualdusk.zezgn.activity.DesignActivity;
import com.virtualdusk.zezgn.activity.Home;
import com.virtualdusk.zezgn.activity.UpdateFragments;
import com.virtualdusk.zezgn.api.FavoriteDesignApi;
import com.virtualdusk.zezgn.utils.ConnectionDetector;

/**
 * Created by Amit Sharma on 10/5/2016.
 */
public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

    private String strUserId = "";
    public TextView tvDesignName, tvDesignNameBy;
    public ImageView ivDesignPic, ivFavorite;
    private ArrayList<DesignsList> arrayList;
    public RelativeLayout rlParentLyt;
    private Context context;
    private String mIsFavorite = "0";
    private onDesignClickListner monDesignClickListner;
    private FavoriteInterface mFavoriteInterface;
    private Utilities mUtilities;

    public RecyclerViewHolders(View itemView, ArrayList<DesignsList> itemList, Context context, onDesignClickListner monDesignClickListner, FavoriteInterface mFavoriteInterface) {
        super(itemView);
        this.arrayList = itemList;
        this.context = context;
        this.mFavoriteInterface = mFavoriteInterface;
        this.monDesignClickListner = monDesignClickListner;
        itemView.setOnClickListener(this);
//tvDesignNameBy.setOnClickListener(this);
//        tvDesignName.setOnClickListener(this);
        tvDesignName = (TextView) itemView.findViewById(R.id.tvDesignName);
        tvDesignNameBy = (TextView) itemView.findViewById(R.id.tvDesignNameBy);
        ivFavorite = (ImageView) itemView.findViewById(R.id.ivFavorite);
        ivDesignPic = (ImageView) itemView.findViewById(R.id.ivDesignPic);
        rlParentLyt = (RelativeLayout) itemView.findViewById(R.id.rlParentLyt);
        ivDesignPic.setOnClickListener(this);
        ivFavorite.setOnClickListener(this);
        mUtilities = new Utilities();

        strUserId = Home.sharedPreferences.getString("user_id", "");

    }

    private String mID = "0";

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ivFavorite:

                if (strUserId.equalsIgnoreCase("-1")) {

                    mUtilities.showLoginDialog(context, context.getString(R.string.alert_login_fav));
                } else {
                    mID = arrayList.get(getPosition()).getId();
                    mIsFavorite = arrayList.get(getPosition()).getIs_favorite();
                    Log.e("ID.", "onClick: " + mID + "," + mIsFavorite);

                    if (new ConnectionDetector(context).isConnectingToInternet()) {

                        setFavoriteDesign(getPosition());
                    } else {
                        Toast.makeText(context, context.getString(R.string.no_internet), Toast.LENGTH_LONG).show();
                    }


                }


                break;

            case R.id.ivDesignPic:

//                Intent intentDesignName = new Intent(view.getContext(),DesignActivity.class);
//                intentDesignName.putExtra("id",arrayList.get(getPosition()).getId());
//                intentDesignName.putExtra("title",arrayList.get(getPosition()).getTitle());
//                intentDesignName.putExtra("design_img",arrayList.get(getPosition()).getDesign_img());
//                intentDesignName.putExtra("author_name",arrayList.get(getPosition()).getAuthor_name());
//                intentDesignName.putExtra("number_of_colors",arrayList.get(getPosition()).getNumber_of_colors());
//                intentDesignName.putExtra("design_text",arrayList.get(getPosition()).getDesign_text());
//                intentDesignName.putExtra("created",arrayList.get(getPosition()).getCreated());
//                intentDesignName.putExtra("design_category",arrayList.get(getPosition()).getName());
//                intentDesignName.putExtra("is_favorite",arrayList.get(getPosition()).getIs_favorite());
//                view.getContext().startActivity(intentDesignName);


                Bundle bundle = new Bundle();
                bundle.putString("id", arrayList.get(getPosition()).getId());
                bundle.putString("title", arrayList.get(getPosition()).getTitle());
                bundle.putString("design_img", arrayList.get(getPosition()).getDesign_img());
                bundle.putString("author_name", arrayList.get(getPosition()).getAuthor_name());
                bundle.putString("number_of_colors", arrayList.get(getPosition()).getNumber_of_colors());
                bundle.putString("design_text", arrayList.get(getPosition()).getDesign_text());
                bundle.putString("created", arrayList.get(getPosition()).getCreated());
                bundle.putString("design_category", arrayList.get(getPosition()).getName());
                bundle.putString("is_favorite", arrayList.get(getPosition()).getIs_favorite());


                monDesignClickListner.onDesignClick(bundle);
//                Home mHome = new Home();
//                mHome.addDetailScreen(bundle);


//
//                ((Home)view.getContext()).openDetailPage(bundle);

                break;

            default:
//                Toast.makeText(view.getContext(), "not identifying", Toast.LENGTH_SHORT).show();

                break;
        }

        //((Home)view.getContext()).addDetailScreen(getPosition()+"");

        //  Toast.makeText(view.getContext(), "Clicked tvDesignName Position = " + getPosition(), Toast.LENGTH_SHORT).show();

//        switch (view.getId())
//        {
//            case R.id.tvDesignName :
//                Toast.makeText(view.getContext(), "Clicked tvDesignName Position = " + getPosition(), Toast.LENGTH_SHORT).show();
//
//                break;
//            case R.id.tvDesignNameBy:
//                Toast.makeText(view.getContext(), "Clicked tvDesignNameBy Position = " + getPosition(), Toast.LENGTH_SHORT).show();
//
//                break;
//
//            case R.id.ivCross:
//                Toast.makeText(view.getContext(), "Clicked ivCross Position = " + getPosition(), Toast.LENGTH_SHORT).show();
//
//                break;
//        }

    }


    private CategoryList getParams() {

        if (mIsFavorite.equalsIgnoreCase("1")) {
            mIsFavorite = "0";

            //Unset favorite
            ivFavorite.setImageResource(R.mipmap.favorite_c);
        } else {
            mIsFavorite = "1";
            ivFavorite.setImageResource(R.mipmap.favorite_cb);

            //Set  favorite
        }
        CategoryList categoryList = new CategoryList();
        categoryList.setUser_id(strUserId);
        categoryList.setDesign_id(mID);
        categoryList.setIs_fav(mIsFavorite);


//        user_id” : 1,,
//        “design_id” : 4,
//        “is_fav” : 0/1,


        return categoryList;
    }


    private void setFavoriteDesign(final int Position) {
        //Creating an object of our api interface
        Retrofit restAdapter = FavoriteDesignApi.retrofit;
        FavoriteDesignApi registerApi = restAdapter.create(FavoriteDesignApi.class);

        Call<JsonElement> call = registerApi.getDesignsViaJSON(getParams());

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.e("RESPONSE:", "onResponse: " + response.body());

                try {
                    JSONObject jsnon = new JSONObject(response.body().toString());
                    String response1 = jsnon.getString("response");
                    JSONObject jsonObject = new JSONObject(response1);
                    String status = jsonObject.getString("code");
                    String message = jsonObject.getString("message");
                    if (status.equals("200")) {
                        mFavoriteInterface.onFavClick(Position, mIsFavorite);
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        //showToast(message);

//                        setAdapter();
                    } else {
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<JsonElement> result, Throwable t) {
            }
        });

    }

}
