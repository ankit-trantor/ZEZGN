package com.virtualdusk.zezgn.activity.MyAccount;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.virtualdusk.zezgn.InterfaceClasses.DeleteItemNo;
import com.virtualdusk.zezgn.InterfaceClasses.onDesignClickListner;
import com.virtualdusk.zezgn.Model.MyCreations;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Constant;
import com.virtualdusk.zezgn.activity.CustomDesignActivity;
import com.virtualdusk.zezgn.activity.Home;

/**
 * Created by Amit Sharma on 10/5/2016.
 */
public class MyCreationsDesignsAdapter extends RecyclerView.Adapter<MyCreationsRecyclerViewHolders> implements DeleteItemNo {

    private static final String TAG = MyCreationsDesignsAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<MyCreations> arrayList;
    private DisplayImageOptions options;
    private ImageLoader imageLoader =null;
    private DeleteItemNo mDeleteItemNo;
    public static String strProductImage = "";
    private String strDesignImage = "",strId = "",strTitle = "",strProductStyleId = "",strModifiedDate = "",
            strProductView="",strProductId = "", strDesignId= "",strprodutcIdX = "",strUserId = "",strDesignCategoryId ="",strDesignTitle = "" ;
    private onDesignClickListner monDesignClickListner;
    private String strIsDesignUploaded = "";


    public MyCreationsDesignsAdapter(Context context, ArrayList<MyCreations> itemList,onDesignClickListner monDesignClickListner) {
        this.arrayList = itemList;
        this.context = context;
        this.monDesignClickListner = monDesignClickListner;
        mDeleteItemNo = this;
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
    public MyCreationsRecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_creation, null);
        MyCreationsRecyclerViewHolders rcv = new MyCreationsRecyclerViewHolders(context,layoutView,arrayList,mDeleteItemNo);

        return rcv;
    }

    @Override
    public void onBindViewHolder(MyCreationsRecyclerViewHolders holder, int position) {

        String oldFormat= "yyyy-MM-dd'T'HH:mm:ssZZZZZ";
        String newFormat= "yyyy-MM-dd";

        String formatedDate = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(oldFormat);
        Date myDate = null;
        try {
            myDate = dateFormat.parse((arrayList.get(position).getModifiedDate()));
            SimpleDateFormat timeFormat = new SimpleDateFormat(newFormat);
            formatedDate = timeFormat.format(myDate);
            holder.tvModifiedDate.setText(formatedDate);

        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.tvDesignName.setText(arrayList.get(position).getTitle());
        holder.ivDesignPic.setTag(position);
        holder.ivDesignPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Constant.SaveButton = "no";
                Log.d(TAG, "onClick: context " + context);
                int pos = (Integer)view.getTag();
                Log.d(TAG, "onClick: pos " + pos);
                MyCreations myCreations = arrayList.get(pos);

                if(myCreations.getDevice_type().equals("Android Device"))
                {
                    strId = myCreations.getId();
                    Log.d(TAG, "onClick: strId " + strId);
                    strTitle = myCreations.getTitle();
                    Log.d(TAG, "onClick: strTitle " + strTitle);
                    strProductView = myCreations.getProductView();
                    Log.d(TAG, "onClick: strProductView " + strProductView);
                    strProductImage = myCreations.getProductImage();
                    Log.d(TAG, "onClick: strProductImage " + strProductImage);
                    strProductId = myCreations.getProductId();
                    Log.d(TAG, "onClick: strProductId " + strProductId);
                    strDesignId = myCreations.getDesignId();
                    Log.d(TAG, "onClick: strDesignId " + strDesignId);
                    strprodutcIdX = myCreations.getProductIdX();
                    Log.d(TAG, "onClick: strprodutcIdX " + strprodutcIdX);
                    strProductStyleId = myCreations.getProductStyleId();
                    Log.d(TAG, "onClick: strProductStyleId " + strProductStyleId);
                    strUserId = myCreations.getUserId();
                    Log.d(TAG, "onClick: strUserId " + strUserId);
                    strDesignCategoryId = myCreations.getDesignCategoryId();
                    Log.d(TAG, "onClick: strDesignCategoryId " + strDesignCategoryId);
                    strDesignTitle = myCreations.getDesignTitle();
                    Log.d(TAG, "onClick: strDesignTitle " + strDesignTitle);
                    strDesignImage = myCreations.getDesignImage();
                    Log.d(TAG, "onClick: strDesignImage " + strDesignImage);
                    strModifiedDate = myCreations.getModifiedDate();
                    Log.d(TAG, "onClick: strModifiedDate " + strModifiedDate);
                    strIsDesignUploaded = myCreations.getModifiedDate();

                    String isDesignUploaded = myCreations.getIsUploadedDesign();
                    Intent mIntent = new Intent(context, CustomDesignActivity.class);
                    mIntent.putExtra(Constant.IS_EDIT,true);

                    mIntent.putExtra(Constant.STR_ID,strId);
                    mIntent.putExtra(Constant.STR_TITLE,strTitle);
                    mIntent.putExtra(Constant.STR_PRODUCT_VIEW,strProductView);
                    //mIntent.putExtra(Constant.STR_PRODUCT_IMAGE,strProductImage);
                    mIntent.putExtra(Constant.STR_PRODUCT_ID,strProductId);
                    mIntent.putExtra(Constant.STR_DESIGN_ID,strDesignId);
                    mIntent.putExtra(Constant.STR_PRODUCT_ID_X, strprodutcIdX);
                    mIntent.putExtra(Constant.STR_PRODUCT_STYLE_ID, strProductStyleId);
                    mIntent.putExtra(Constant.STR_USER_ID, strUserId);
                    mIntent.putExtra(Constant.STR_DESIGN_CATEGORY_ID, strDesignCategoryId);
                    mIntent.putExtra(Constant.STR_DESIGN_TITLE, strDesignTitle);
                    mIntent.putExtra(Constant.STR_DESIGN_IMAGE, strDesignImage);
                    mIntent.putExtra(Constant.STR_MODIFIED_DATE, strModifiedDate);
                    mIntent.putExtra(Constant.STR_IS_DESIGN_UPLOADED, isDesignUploaded);

                    context.startActivity(mIntent);

                    Home.recreateActivity = true;
                }
                else
                {
                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("ZEZIGN");
                    alert.setMessage("You Can Only Edit This Product in "+myCreations.getDevice_type());
                    alert.setPositiveButton("OK",null);
                    alert.show();
                }


            }
        });
        //holder.ivCross.setImageResource(R.mipmap.favorite_c);
        String product_img = arrayList.get(position).getProductImage();
        Log.d(TAG, "onBindViewHolder: image url " + Constant.IMAGE_BASE_URL_SAVED_PRODUCTS+product_img);
        imageLoader.displayImage(Constant.IMAGE_BASE_URL_SAVED_PRODUCTS+product_img, holder.ivDesignPic,
                options, new ImageLoadingListener() {

                    public void onLoadingCancelled(String imageUri,
                                                   View view) {
                    }

                    public void onLoadingComplete(String arg0,
                                                  View arg1, Bitmap arg2) {


                    }

                    public void onLoadingStarted(String arg0, View arg1) {

                    }

                    @Override
                    public void onLoadingFailed(String arg0, View arg1,
                                                FailReason arg2) {
                        // TODO Auto-generated method stub
                    }
                });

    }

    @Override
    public int getItemCount() {
        return this.arrayList.size();
    }

    @Override
    public void deleteItemNo(int position) {

        arrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, arrayList.size());

    }
}
