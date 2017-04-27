package com.virtualdusk.zezgn.activity.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;

import java.util.ArrayList;

import com.virtualdusk.zezgn.Model.DesignsList;
import com.virtualdusk.zezgn.Model.OrderClass;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Constant;

/**
 * Created by Amit Sharma on 10/17/2016.
 */
public class AllDesignsAdapter extends BaseAdapter {

    Context c;

    private LayoutInflater inflater = null;

    private ArrayList<DesignsList> arrayList;
    private DisplayImageOptions options;
    private ImageLoader imageLoader = null;// ImageLoader.getInstance();

    public AllDesignsAdapter(Context context, ArrayList<DesignsList> arrayList) {
        c = context;
        inflater = (LayoutInflater) c
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.arrayList = arrayList;
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
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View vi = view;
        if (view == null)

            vi = inflater.inflate(R.layout.list_row_design, null);


        TextView tvDesignName = (TextView) vi.findViewById(R.id.tvDesignName);
        TextView tvDesignNameBy = (TextView) vi.findViewById(R.id.tvDesignNameBy);
        ImageView ivFavorite = (ImageView) vi.findViewById(R.id.ivFavorite);
        ImageView ivDesignPic = (ImageView) vi.findViewById(R.id.ivDesignPic);
        tvDesignName.setText(arrayList.get(i).getTitle());
        tvDesignNameBy.setVisibility(View.GONE);

        if (arrayList.get(i).getIs_favorite().equalsIgnoreCase("1")) {

            ivFavorite.setImageResource(R.mipmap.favorite_c);
        } else {
            ivFavorite.setImageResource(R.mipmap.favorite_cb);

        }
//        holder.tvDesignNameBy.setText(arrayList.get(position).getAuthor_name());

        imageLoader.displayImage(Constant.IMAGE_BASE_URL_Designs + arrayList.get(i).getDesign_img(), ivDesignPic,
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


        return vi;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


}
