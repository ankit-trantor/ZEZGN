package com.virtualdusk.zezgn.activity.CategoryScreen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import com.virtualdusk.zezgn.Model.CategoryList;
import com.virtualdusk.zezgn.Model.DesignsList;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Constant;
import com.virtualdusk.zezgn.activity.HomeScreen.RecyclerViewHolders;

/**
 * Created by Amit Sharma on 10/5/2016.
 */
public class CategoryAdapter extends RecyclerView.Adapter<RecyclerViewCategoryHolders> {

    private String TAG = CategoryAdapter.class.getSimpleName().toString();
    private Context context;
    private ArrayList<CategoryList> arrayList;
    private DisplayImageOptions options;
    private ImageLoader imageLoader =null;// ImageLoader.getInstance();

    private int lastPosition = -1;

    public CategoryAdapter(Context context, ArrayList<CategoryList> itemList) {
        this.arrayList = itemList;
        this.context = context;
        imageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading()
                .delayBeforeLoading(1000)
                .cacheInMemory()
                .cacheOnDisc()
                .build();

    }
    @Override
    public RecyclerViewCategoryHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_list_row_category, null);
        RecyclerViewCategoryHolders rcv = new RecyclerViewCategoryHolders(layoutView);

        return rcv;
    }

    @Override
    public void onBindViewHolder(final RecyclerViewCategoryHolders holder, int position) {


        holder.tvDesignName.setText(arrayList.get(position).getName());

        holder.tvDesignNameBy.setText(arrayList.get(position).getTotal()+" Design(s)");
        Log.e(TAG, "getTotal() : "+ arrayList.get(position).getTotal() );

        Log.e("URLLLLL..",""+arrayList.size());

        String profile_image = arrayList.get(position).getIcon();


        imageLoader.displayImage(Constant.IMAGE_BASE_URL_design_categories+""+profile_image, holder.ivDesignPic,
                options, new ImageLoadingListener() {

                    public void onLoadingCancelled(String imageUri,
                                                   View view) {
                    }

                    public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
                        Bitmap bitmap = getRoundedCornerBitmap(arg2,100);
                        holder.ivDesignPic.setImageBitmap(bitmap);

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
    public Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, 10, 10, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
//    @Override
//    public void onBindViewHolder(DesignsAdapter holder, int position) {
////        holder.countryName.setText(itemList.get(position).getName());
//    }

    @Override
    public int getItemCount() {
        return this.arrayList.size();
    }

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.slide_down_from_top);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }

    }
}
