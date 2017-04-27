package com.virtualdusk.zezgn.activity.MyAccount;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.virtualdusk.zezgn.Model.MyFavorites;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Constant;
import com.virtualdusk.zezgn.activity.DesignActivity;
import com.virtualdusk.zezgn.activity.Home;
import com.virtualdusk.zezgn.activity.Products.ProductFragment;
import com.virtualdusk.zezgn.api.FavoriteDesignApi;

/**
 * Created by Amit Sharma on 10/5/2016.
 */
public class MyFavoriteDesignsRecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView tvDesignName,tvDesignNameBy;
    public ImageView ivDesignPic,ivFavorite;
    private ArrayList<MyFavorites> arrayList;
    private Context context;
    private onDesignClickListner monDesignClickListner;
    private FavoriteInterface mFavoriteInterface;

    public MyFavoriteDesignsRecyclerViewHolders(Context context, View itemView,
                                                ArrayList<MyFavorites> itemList, onDesignClickListner monDesignClickListner, FavoriteInterface mFavoriteInterface) {
        super(itemView);
        this.arrayList = itemList;
        this.context=context;
        this.monDesignClickListner = monDesignClickListner;
        this.mFavoriteInterface = mFavoriteInterface;

        itemView.setOnClickListener(this);
//tvDesignNameBy.setOnClickListener(this);
//        tvDesignName.setOnClickListener(this);
        tvDesignName = (TextView)itemView.findViewById(R.id.tvDesignName);
        tvDesignNameBy = (TextView)itemView.findViewById(R.id.tvDesignNameBy);
        ivFavorite = (ImageView)itemView.findViewById(R.id.ivFavorite);
        ivDesignPic = (ImageView)itemView.findViewById(R.id.ivDesignPic);
        ivDesignPic.setOnClickListener(this);
        ivFavorite.setOnClickListener(this);
    }
private String mID="0";

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.ivFavorite:

                //mID=arrayList.get(getPosition()).getDesign_id();
                mFavoriteInterface.onFavClick(getPosition(),"");
//                mIsFavorite=arrayList.get(getPosition()).getIs_favorite();
//
//
//                Log.e("ID.", "onClick: "+mID+","+mIsFavorite );
               // setFavoriteDesign();


                break;

            case R.id.ivDesignPic:

//                Intent intentDesignName = new Intent(view.getContext(),DesignActivity.class);
//                intentDesignName.putExtra("id",arrayList.get(getPosition()).getDesign_id());
//                intentDesignName.putExtra("title",arrayList.get(getPosition()).getDesign_title());
//                intentDesignName.putExtra("design_img",arrayList.get(getPosition()).getDesign_design_img());
//                intentDesignName.putExtra("author_name",arrayList.get(getPosition()).getDesign_author_name());
//                intentDesignName.putExtra("number_of_colors",arrayList.get(getPosition()).getDesign_number_of_colors());
//                intentDesignName.putExtra("design_text",arrayList.get(getPosition()).getDesign_design_text());
//                intentDesignName.putExtra("created",arrayList.get(getPosition()).getDesign_created());
//                intentDesignName.putExtra("design_category",arrayList.get(getPosition()).getDesign_category_name());
//                intentDesignName.putExtra("is_favorite","1");
//                view.getContext().startActivity(intentDesignName);

                Bundle bundle = new Bundle();
                bundle.putString("id",arrayList.get(getPosition()).getDesign_id());
                bundle.putString("design_img", Constant.IMAGE_BASE_URL_Designs + arrayList.get(getPosition()).getDesign_design_img());
//                bundle.putString("author_name",arrayList.get(getPosition()).getDesign_author_name());
//                bundle.putString("number_of_colors",arrayList.get(getPosition()).getDesign_number_of_colors());
//                bundle.putString("design_text",arrayList.get(getPosition()).getDesign_design_text());
//                bundle.putString("created",arrayList.get(getPosition()).getDesign_created());
//                bundle.putString("design_category",arrayList.get(getPosition()).getDesign_category_name());
//                bundle.putString("is_favorite","1");
                monDesignClickListner.onDesignClick(bundle);

                Home.recreateActivity = true;

                break;

            default:

                break;
        }

    }
private String mIsFavorite="0";

    private CategoryList getParams()
    {


//        if (mIsFavorite.equalsIgnoreCase("1"))
//        {
//            mIsFavorite="0";
//
//            //Unset favorite
//            ivCross.setImageResource(R.mipmap.favorite_cb);
//        }
//        else {
//            mIsFavorite="1";
//            ivCross.setImageResource(R.mipmap.favorite_c);
//
//            //Set  favorite
//        }
        CategoryList categoryList=new CategoryList();

        categoryList.setUser_id(Home.sharedPreferences.getString("user_id", null));
        categoryList.setDesign_id(mID);
        categoryList.setIs_fav(mIsFavorite);


        return categoryList;
    }




}
