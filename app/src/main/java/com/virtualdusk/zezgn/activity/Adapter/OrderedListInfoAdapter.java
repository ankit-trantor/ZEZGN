package com.virtualdusk.zezgn.activity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.virtualdusk.zezgn.Model.OrderClass;
import com.virtualdusk.zezgn.R;

/**
 * Created by Amit Sharma on 10/17/2016.
 */
public class OrderedListInfoAdapter extends BaseAdapter {

    Context c;

    private LayoutInflater inflater = null;

private ArrayList<OrderClass> arrayList;

    public OrderedListInfoAdapter(Context context, ArrayList<OrderClass> arrayList) {
        c = context;
        inflater = (LayoutInflater) c
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    this.arrayList=arrayList;

    }

    @Override
    public int getCount() {
        return 15;//arrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View vi = view;
        if (view == null)

            vi = inflater.inflate(R.layout.order_list_summary, null);

        ImageView ivImageProduct=(ImageView) vi.findViewById(R.id.ivImage);
        TextView tvProductCategory=(TextView)vi.findViewById(R.id.tvProductCategory);
        TextView tvProductQtyValue=(TextView)vi.findViewById(R.id.tvProductQtyValue);
        TextView lblOrderName=(TextView)vi.findViewById(R.id.lblOrderName);
        TextView tvProductPrice=(TextView)vi.findViewById(R.id.tvProductPrice);
        TextView tvProductShippingPrice=(TextView)vi.findViewById(R.id.tvProductShippingPrice);
        TextView tvProductDate=(TextView)vi.findViewById(R.id.tvProductDate);

//
//        1- COD
//        2- PAYPAL
//        3- CASHU

        return vi;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


}
