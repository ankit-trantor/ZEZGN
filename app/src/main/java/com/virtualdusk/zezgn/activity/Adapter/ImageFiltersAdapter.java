package com.virtualdusk.zezgn.activity.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.virtualdusk.zezgn.InterfaceClasses.OnEffectListClickListner;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.FilterEffects;


/**
 * Created by bhart.gupta on 24-Oct-16.
 */

public class ImageFiltersAdapter extends RecyclerView.Adapter<ImageFiltersAdapter.MyviewHolder> {


    private String[] filterEffectList;
    private Context mContext;
    private Bitmap mBitmap;
    private OnEffectListClickListner mOnEffectListClickListner;
    private int lastPosition = -1 ;
    //private FilterEffects mFilterEffects;

    public ImageFiltersAdapter(String[] filterEffectList, Context mContext, OnEffectListClickListner mOnEffectListClickListner , Bitmap mBitmap) {

        this.filterEffectList = filterEffectList;
        this.mContext = mContext;
        this.mBitmap = mBitmap;
        this.mOnEffectListClickListner = mOnEffectListClickListner;

        Log.d("bharat","list size "+filterEffectList.length);

    }

    @Override
    public MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filter, parent, false);

        return new MyviewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyviewHolder holder, int position) {



        Log.d("bharat","pos  " + position + "filterEffectList[pos] " +filterEffectList[position] );

        FilterEffects mFilterEffects = new FilterEffects(mContext);
        mFilterEffects.applyfilter(filterEffectList[position],mBitmap,holder.ImgFilter,null);
        holder.ImgFilter.setTag(position);
        holder.ImgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int pos = (Integer)view.getTag();

                mOnEffectListClickListner.onFilterEffectChange(filterEffectList[pos],pos);
            }
        });

        //setAnimation( holder.ImgFilter,position);

    }

    @Override
    public int getItemCount() {
        return filterEffectList.length;
    }
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }


    public class MyviewHolder extends RecyclerView.ViewHolder {

        private ImageView ImgFilter = null;

        public MyviewHolder(View itemView) {
            super(itemView);

         ImgFilter = (ImageView) itemView.findViewById(R.id.img_filter);

        }


    }
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }

    }

}
