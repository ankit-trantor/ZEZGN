package com.virtualdusk.zezgn.activity.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.virtualdusk.zezgn.Model.NewDesign;
import com.virtualdusk.zezgn.Model.OrderClass;
import com.virtualdusk.zezgn.R;

/**
 * Created by Amit Sharma on 10/17/2016.
 */
public class OrderListAdapter extends BaseAdapter {

    Context c;

    private LayoutInflater inflater = null;

    private ArrayList<OrderClass> arrayList;

    public OrderListAdapter(Context context, ArrayList<OrderClass> arrayList) {
        c = context;
        inflater = (LayoutInflater) c
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.arrayList = arrayList;

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
            vi = inflater.inflate(R.layout.list_row_order_listing, null);

        TextView lblOrderNumber = (TextView) vi.findViewById(R.id.lblOrderNumber);
        TextView lblOrderName = (TextView) vi.findViewById(R.id.lblOrderName);
        TextView lblDate = (TextView) vi.findViewById(R.id.lblDate);
        TextView lblOrderAmount = (TextView) vi.findViewById(R.id.lblOrderAmount);
        TextView btnAction = (TextView) vi.findViewById(R.id.btnAction);


        lblOrderNumber.setText(arrayList.get(i).getUnique_id());
//
//        1- COD
//        2- PAYPAL
//        3- CASHU
        if (arrayList.get(i).getPayment_method().equals("1")) {
            lblOrderName.setText("Cash On Delivery");

        } else if (arrayList.get(i).getPayment_method().equals("2")) {
            lblOrderName.setText("PAYPAL");
        } else {
            lblOrderName.setText("CASHU");

        }
        lblDate.setText(arrayList.get(i).getCreated()
        );

        //1- placed,2-packed, 3- Dispatched, 4-Delivered
        if (arrayList.get(i).getStatus().equals("1")) {
            btnAction.setText("Placed");
        } else if (arrayList.get(i).getStatus().equals("2")) {
            btnAction.setText("Packed");

        } else if (arrayList.get(i).getStatus().equals("3")) {
            btnAction.setText("Dispatched");

        } else if (arrayList.get(i).getStatus().equals("4")) {
            btnAction.setText("Delivered");

        }

        int toal_price1 = Integer.parseInt(arrayList.get(i).getTotal_price());
        int toal_price2 = Integer.parseInt(arrayList.get(i).getShipping_price());
        int tott = toal_price1+toal_price2;
        lblOrderAmount.setText("AED " + tott);
        return vi;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


}
