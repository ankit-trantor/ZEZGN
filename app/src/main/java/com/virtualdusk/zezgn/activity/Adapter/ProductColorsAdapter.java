package com.virtualdusk.zezgn.activity.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import java.util.ArrayList;

import com.virtualdusk.zezgn.InterfaceClasses.OnProductColorChangeListner;
import com.virtualdusk.zezgn.Model.ProductStyleColor;
import com.virtualdusk.zezgn.R;


/**
 * Created by bhart.gupta on 24-Oct-16.
 */

public class ProductColorsAdapter extends RecyclerView.Adapter<ProductColorsAdapter.MyviewHolder> {


    private ArrayList<ProductStyleColor> mProductColorList;
    private Context mContext;
    private OnProductColorChangeListner mOnProductColorChangeListner;
    private int lastPosition = -1;

    public ProductColorsAdapter(ArrayList<ProductStyleColor> mProductColorList, Context mContext, OnProductColorChangeListner mOnProductColorChangeListner) {

        this.mProductColorList = mProductColorList;
        this.mContext = mContext;
        this.mOnProductColorChangeListner = mOnProductColorChangeListner;

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
        final String strColor = "#"+mProductColorList.get(position).getProductStyleColor();
        final int color = Color.parseColor(strColor);
        bgShape.setColor(color);
        holder.LL_Circle.setTag(position);

        holder.LL_Circle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = (Integer) view.getTag();
                String productId = mProductColorList.get(pos).getProductStyleID();
                mOnProductColorChangeListner.onProductColorChange(color,strColor,productId);
            }
        });

        setAnimation(holder.LL_Circle,position);
    }

    @Override
    public int getItemCount() {
        return mProductColorList.size();
    }


    public class MyviewHolder extends RecyclerView.ViewHolder {

        private LinearLayout LL_Circle;

        public MyviewHolder(View itemView) {
            super(itemView);

            LL_Circle = (LinearLayout) itemView.findViewById(R.id.ll_circle);

        }
    }
    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_down_from_top);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }

    }
}
