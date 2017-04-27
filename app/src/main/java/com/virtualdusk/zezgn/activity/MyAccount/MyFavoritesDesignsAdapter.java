package com.virtualdusk.zezgn.activity.MyAccount;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import java.util.ArrayList;

import com.virtualdusk.zezgn.InterfaceClasses.FavoriteInterface;
import com.virtualdusk.zezgn.InterfaceClasses.onDesignClickListner;
import com.virtualdusk.zezgn.Model.DesignsList;
import com.virtualdusk.zezgn.Model.MyFavorites;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Constant;
import com.virtualdusk.zezgn.activity.HomeScreen.RecyclerViewHolders;

/**
 * Created by Amit Sharma on 10/5/2016.
 */
public class MyFavoritesDesignsAdapter extends RecyclerView.Adapter<MyFavoriteDesignsRecyclerViewHolders> {

    private Context context;
    private ArrayList<MyFavorites> arrayList;
    private DisplayImageOptions options;
    private ImageLoader imageLoader =null;// ImageLoader.getInstance();
    private onDesignClickListner monDesignClickListner;
    private FavoriteInterface mFavoriteInterface;

    public MyFavoritesDesignsAdapter(Context context, ArrayList<MyFavorites> itemList,onDesignClickListner monDesignClickListner,FavoriteInterface mFavoriteInterface) {
        this.arrayList = itemList;
        this.context = context;
        this.monDesignClickListner = monDesignClickListner;
        this.mFavoriteInterface = mFavoriteInterface;
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
    public MyFavoriteDesignsRecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_design, null);
        MyFavoriteDesignsRecyclerViewHolders rcv = new MyFavoriteDesignsRecyclerViewHolders(context,layoutView,arrayList,monDesignClickListner,mFavoriteInterface);

        return rcv;
    }

    @Override
    public void onBindViewHolder(MyFavoriteDesignsRecyclerViewHolders holder, int position) {
        holder.tvDesignName.setText(arrayList.get(position).getDesign_title());
        holder.tvDesignNameBy.setText(arrayList.get(position).getDesign_author_name());

        holder.ivFavorite.setImageResource(R.mipmap.favorite_cb);

//        if (arrayList.get(position).getIs_favorite().equalsIgnoreCase("1"))
//        {
//
//            holder.ivCross.setImageResource(R.mipmap.favorite_c);
//        }
//        else {
//            holder.ivCross.setImageResource(R.mipmap.favorite_cb);
//
//
//        }

        imageLoader.displayImage(Constant.IMAGE_BASE_URL_Designs+arrayList.get(position).getDesign_design_img(), holder.ivDesignPic,
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
                        //load_progressbar.setVisibility(View.GONE);
                    }
                });




    }



    @Override
    public int getItemCount() {
        return this.arrayList.size();
    }
}
