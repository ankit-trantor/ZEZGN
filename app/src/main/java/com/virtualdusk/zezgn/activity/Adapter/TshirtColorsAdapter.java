package com.virtualdusk.zezgn.activity.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.virtualdusk.zezgn.InterfaceClasses.OnColorListClickListner;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.activity.CustomDesignActivity;

import static android.support.design.R.attr.icon;
import static com.facebook.FacebookSdk.getApplicationContext;
import static com.virtualdusk.zezgn.activity.LoginActivity.context;


/**
 * Created by bhart.gupta on 24-Oct-16.
 */

public class TshirtColorsAdapter extends RecyclerView.Adapter<TshirtColorsAdapter.MyviewHolder> {


    private String[] colorsList;

    private Context mContext;
    private OnColorListClickListner mOnColorListClickListner;
    private int lastPosition = -1;



    public TshirtColorsAdapter(String[] colorsList, Context mContext, OnColorListClickListner mOnColorListClickListner) {

        this.colorsList = colorsList;
        this.mContext = mContext;
        this.mOnColorListClickListner = mOnColorListClickListner;

    }

    @Override
    public MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_color, parent, false);

        return new MyviewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyviewHolder holder, int position) {

        GradientDrawable bgShape = (GradientDrawable) holder.LL_Circle.getBackground();
        bgShape.setColor(Color.parseColor(colorsList[position]));
        holder.LL_Circle.setTag(position);


//        if(position==0){
//
////            holder.LL_Circle.setBackgroundResource(R.drawable.transp);
//            holder.transImageView.setVisibility(View.VISIBLE);
//
//        }



        holder.LL_Circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = (Integer) view.getTag();
                mOnColorListClickListner.onColorChange(colorsList[pos]);

            }
        });

        setAnimation(holder.LL_Circle,position);


    }

    @Override
    public int getItemCount() {
        return colorsList.length;
    }


    public class MyviewHolder extends RecyclerView.ViewHolder {

        private LinearLayout LL_Circle;
        private ImageView transImageView;



        public MyviewHolder(View itemView) {
            super(itemView);

            LL_Circle = (LinearLayout) itemView.findViewById(R.id.ll_circle);
            transImageView=(ImageView)itemView.findViewById(R.id.transimg);





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
