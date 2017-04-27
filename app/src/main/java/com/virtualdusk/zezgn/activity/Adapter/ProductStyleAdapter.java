package com.virtualdusk.zezgn.activity.Adapter;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.virtualdusk.zezgn.Model.Style;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Constant;
import com.virtualdusk.zezgn.activity.CustomDesignActivity;

/**
 * Created by bhart.gupta on 03-Nov-16.
 */

public class ProductStyleAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<Style> mStyleList;
    private LayoutInflater inflter;

    public ProductStyleAdapter(Context mContext, ArrayList<Style> mStyleList){

        this.mContext = mContext;
        this.mStyleList = mStyleList;
        inflter = (LayoutInflater.from(mContext));
    }

    @Override
    public int getCount() {
        return mStyleList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
      /*  view = inflter.inflate(R.layout.spinner_text_item, null);
        TextView TvSpinnerItem = (TextView) view.findViewById(R.id.tvproduct);
        TvSpinnerItem.setText(mStyleList.get(i).getStyleName());


        SpannableString content = new SpannableString(mStyleList.get(i).getStyleName());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        TvSpinnerItem.setText(content);*/
        view = inflter.inflate(R.layout.product_spinner_item, null);
        TextView TvSpinnerItem = (TextView) view.findViewById(R.id.tvItem);
        TextView TvCost = (TextView) view.findViewById(R.id.tvCost);
        ImageView IvShirt = (ImageView) view.findViewById(R.id.img);
        TvSpinnerItem.setText(mStyleList.get(i).getStyleName());
        TvCost.setText("AED " + mStyleList.get(i).getPrice());
        String productUrl = Constant.IMAGE_BASE_URL_PRODUCT_STYLES + mStyleList.get(i).getStyleImage();



        Picasso.with(mContext)
                .load(productUrl)
                .into(IvShirt);
        return view;
    }

    @Override
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.product_spinner_item, null);
        TextView TvSpinnerItem = (TextView) view.findViewById(R.id.tvItem);
        TextView TvCost = (TextView) view.findViewById(R.id.tvCost);
        ImageView IvShirt = (ImageView) view.findViewById(R.id.img);
        TvSpinnerItem.setText(mStyleList.get(i).getStyleName());
        TvCost.setText("AED " + mStyleList.get(i).getPrice());
        String productUrl = Constant.IMAGE_BASE_URL_PRODUCT_STYLES + mStyleList.get(i).getStyleImage();



        Picasso.with(mContext)
                .load(productUrl)
                .into(IvShirt);
        return view;
    }
}
