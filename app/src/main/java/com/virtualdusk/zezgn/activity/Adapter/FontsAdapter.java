package com.virtualdusk.zezgn.activity.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import com.virtualdusk.zezgn.InterfaceClasses.OnFontsClickListner;
import com.virtualdusk.zezgn.R;


/**
 * Created by bhart.gupta on 24-Oct-16.
 */

public class FontsAdapter extends RecyclerView.Adapter<FontsAdapter.MyviewHolder> {


    private ArrayList<String> fontList;
    private Context mContext;
    private OnFontsClickListner mOnFontsClickListner;

    public FontsAdapter( ArrayList<String> fontList, Context mContext, OnFontsClickListner mOnFontsClickListner) {

        this.fontList = fontList;
        this.mContext = mContext;
        this.mOnFontsClickListner = mOnFontsClickListner;

    }

    @Override
    public MyviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fonts, parent, false);

        return new MyviewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyviewHolder holder, int position) {

        holder.TV_Fonts.setTag(position);

        Typeface tf = Typeface.createFromAsset(mContext.getAssets(),
               // "fonts/geneva.ttf");
               "fonts/"+fontList.get(position));

        holder.TV_Fonts.setTypeface(tf);

        holder.TV_Fonts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = (Integer) view.getTag();
                mOnFontsClickListner.onFontSelected(fontList.get(pos),pos);
            }
        });


    }

    @Override
    public int getItemCount() {
        return fontList.size();
    }


    public class MyviewHolder extends RecyclerView.ViewHolder {

        private TextView TV_Fonts;


        public MyviewHolder(View itemView) {
            super(itemView);

            TV_Fonts = (TextView) itemView.findViewById(R.id.tvFonts);

        }


    }
}
