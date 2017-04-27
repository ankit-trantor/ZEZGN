package com.virtualdusk.zezgn.activity.HomeScreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import java.util.ArrayList;

import com.virtualdusk.zezgn.InterfaceClasses.FavoriteInterface;
import com.virtualdusk.zezgn.InterfaceClasses.onDesignClickListner;
import com.virtualdusk.zezgn.Model.DesignsList;
import com.virtualdusk.zezgn.Model.UserInfo;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Constant;
import com.virtualdusk.zezgn.activity.ItemClickSupport;

/**
 * Created by Amit Sharma on 10/5/2016.
 */
public class DesignsAdapter extends RecyclerView.Adapter<RecyclerViewHolders> {

    private Context context;
    private ArrayList<DesignsList> arrayList;
    private DisplayImageOptions options;
    private ImageLoader imageLoader =null;// ImageLoader.getInstance();
    private onDesignClickListner monDesignClickListner;
    private FavoriteInterface mFavoriteInterface;
    private int lastPosition = -1;

    public DesignsAdapter(Context context, ArrayList<DesignsList> itemList,
                          onDesignClickListner monDesignClickListner,FavoriteInterface mFavoriteInterface) {
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
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row_design, null);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView,arrayList,context,monDesignClickListner,mFavoriteInterface);

        return rcv;
    }

    @Override
    public void onBindViewHolder(RecyclerViewHolders holder, int position) {
        holder.tvDesignName.setText(arrayList.get(position).getTitle());

        if (arrayList.get(position).getIs_favorite().equalsIgnoreCase("1"))
        {

            holder.ivFavorite.setImageResource(R.mipmap.favorite_cb);
        }
        else {
            holder.ivFavorite.setImageResource(R.mipmap.favorite_c);


            //Set  favorite
        }
      holder.tvDesignNameBy.setText(arrayList.get(position).getAuthor_name());

        imageLoader.displayImage(Constant.IMAGE_BASE_URL_Designs+arrayList.get(position).getDesign_img(), holder.ivDesignPic,
                options, new ImageLoadingListener() {

                    public void onLoadingCancelled(String imageUri,
                                                   View view) {
                    }

                    public void onLoadingComplete(String arg0,
                                                  View arg1, Bitmap arg2) {


                    }

                    public void onLoadingStarted(String arg0, View arg1) {
                        // TODO Auto-generated method stub
                        //if (post_image.getVisibility() == View.VISIBLE) {
                        //   load_progressbar.setVisibility(View.VISIBLE);
                        //}
                    }

                    @Override
                    public void onLoadingFailed(String arg0, View arg1,
                                                FailReason arg2) {
                        // TODO Auto-generated method stub
                        //load_progressbar.setVisibility(View.GONE);
                    }
                });


        setAnimation(holder.rlParentLyt,position);

    }



    @Override
    public int getItemCount() {
        return this.arrayList.size();
    }


    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.enter_from_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }

    }
}
