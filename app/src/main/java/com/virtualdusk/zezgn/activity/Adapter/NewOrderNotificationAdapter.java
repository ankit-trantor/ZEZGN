package com.virtualdusk.zezgn.activity.Adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.virtualdusk.zezgn.Model.NewOrder;
import com.virtualdusk.zezgn.R;

/**
 * Created by Amit Sharma on 10/17/2016.
 */
public class NewOrderNotificationAdapter extends BaseAdapter {

    Context c;

    private LayoutInflater inflater = null;

private ArrayList<NewOrder> arrayList;

    public NewOrderNotificationAdapter(Context context, ArrayList<NewOrder> arrayList) {
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

        String strStatus = "";
        if(arrayList.get(i).getType().equalsIgnoreCase("1")){
            strStatus = "Placed.";

        }else if(arrayList.get(i).getType().equalsIgnoreCase("2")){
            strStatus = "Packed.";

        }else if(arrayList.get(i).getType().equalsIgnoreCase("3")){
            strStatus = "Dispatched.";

        }else if(arrayList.get(i).getType().equalsIgnoreCase("4")){
            strStatus =  "Delivered.";
        }


        String text = "<font color=#ff000000>Your Order </font> " +
                "<font color=#FF0000>"+arrayList.get(i).getOrder_unique_id()+"</font>"+
                "<font color=#ff000000> has been </font> "+
                "<font color=#FF0000>"+strStatus+"</font>";


        tvMessage.setText(Html.fromHtml(text));

//        tvMessage.setText("Your Order "+arrayList.get(i).getOrder_unique_id()+" has been Placed.");
        tvDate.setText(arrayList.get(i).getOrder_created());
        return vi;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


}
