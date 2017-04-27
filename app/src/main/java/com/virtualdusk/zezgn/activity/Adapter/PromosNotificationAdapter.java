package com.virtualdusk.zezgn.activity.Adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.virtualdusk.zezgn.Model.Coupon;
import com.virtualdusk.zezgn.Model.NewOrder;
import com.virtualdusk.zezgn.R;

/**
 * Created by Amit Sharma on 10/17/2016.
 */
public class PromosNotificationAdapter extends BaseAdapter {

    Context c;

    private LayoutInflater inflater = null;

private ArrayList<Coupon> arrayList;

    public PromosNotificationAdapter(Context context, ArrayList<Coupon> arrayList) {
        c = context;
        inflater = (LayoutInflater) c
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    this.arrayList=arrayList;

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

            vi = inflater.inflate(R.layout.list_row_notifiy_new_designs, null);

        TextView tvMessage=(TextView)vi.findViewById(R.id.tvMessage);
        TextView tvDate=(TextView)vi.findViewById(R.id.tvDate);

        tvDate.setText(arrayList.get(i).getCoupon_created());

        if (arrayList.get(i).getCoupon_discount_type().equals("fixed"))
        {

            //#ff000000
            //#FF0000

            String text = "<font color=#ff000000>New promo has been added for AED</font> " +
                    "<font color=#FF0000>"+arrayList.get(i).getCoupon_amount_or_percentage()+"</font>"+
                    "<font color=#ff000000> discount here is the code </font> "+
                    "<font color=#FF0000>"+arrayList.get(i).getCoupon_code()+"</font>";


            tvMessage.setText(Html.fromHtml(text));
//            tvMessage.setText("New promo has been added for AED "+arrayList.get(i).getCoupon_amount_or_percentage()
//            +" discount here is the code "+
//                    arrayList.get(i).getCoupon_code());
        }
        else
        {

            String text = "<font color=#ff000000>New promo has been added for </font> " +
                    "<font color=#FF0000>"+arrayList.get(i).getCoupon_amount_or_percentage()+"%"+"</font>"+
                    "<font color=#ff000000> discount here is the code </font> "+
                    "<font color=#FF0000>"+arrayList.get(i).getCoupon_code()+"</font>";

            tvMessage.setText(Html.fromHtml(text));

//            tvMessage.setText("New promo has been added for "+arrayList.get(i).getCoupon_amount_or_percentage()
//                    +" discount here is the code "+
//                    arrayList.get(i).getCoupon_code());
        }

        return vi;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


}
