package com.virtualdusk.zezgn.activity.Adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import com.virtualdusk.zezgn.Model.NewDesign;
import com.virtualdusk.zezgn.R;

/**
 * Created by Amit Sharma on 10/17/2016.
 */
public class NewDesignNotificationAdapter extends BaseAdapter {

    Context c;

    private LayoutInflater inflater = null;

private ArrayList<NewDesign> arrayList;

    public NewDesignNotificationAdapter(Context context, ArrayList<NewDesign> arrayList) {
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
//
//        String text = "<font color=#ff000000>New Design </font> " +
//                "<font color=#FF0000>"+arrayList.get(i).getDesign_text()+"</font>"+
//                "<font color=#ff000000>  has been added </font> "
//                +"</font>";

//        tvMessage.setText(Html.fromHtml(text));

        tvMessage.setText("New Design "+arrayList.get(i).getDesign_text()+" has been added");
        tvDate.setText(arrayList.get(i).getDesign_created());
        return vi;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


}
