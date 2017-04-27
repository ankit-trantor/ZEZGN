package com.virtualdusk.zezgn.utils;


import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Window;

public class StoreInfo {

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private Context context;

    public StoreInfo(Context context) {
        this.context = context;
        sp = context.getSharedPreferences("user", Context.MODE_PRIVATE);
    }


    //store user_id & access_token
    public void store_user_info(String itemname, String itemsalesprice, String Regularprice, String price, String raing, String offerName, String offerDescription, String offerLabel, String description) {
        editor = sp.edit();
        editor.putString("itemname", itemname);
        editor.putString("itemsalesprice", itemsalesprice);
        editor.putString("Regularprice", Regularprice);
        editor.putString("price", price);
        editor.putString("raing", raing);
        editor.putString("offerName", offerName);
        editor.putString("offerDescription", offerDescription);
        editor.putString("offerLabel", offerLabel);
        editor.putString("description", description);
        editor.commit();
    }
    public void store_register_id(String register_id) {
        editor = sp.edit();
        editor.putString("register_id",register_id);
        editor.commit();
    }
    public String getRegister_id() {

        SharedPreferences myPrefs = context.getSharedPreferences("user", 0);
        return myPrefs.getString("register_id", "0");
    }
    public void store_res(String register_id) {
        editor = sp.edit();
        editor.putString("res",register_id);
        editor.commit();
    }
    public String getRes() {

        SharedPreferences myPrefs = context.getSharedPreferences("user", 0);
        return myPrefs.getString("res", "0");
    }
    public String getdescription()
    {
        SharedPreferences myPrefs = context.getSharedPreferences("user", 0);
        return myPrefs.getString("description", "0");
    }
    public String getitemname()
    {
        SharedPreferences myPrefs = context.getSharedPreferences("user", 0);
        return myPrefs.getString("itemname", "0");
    }
    public String getitemsalesprice()
    {
        SharedPreferences myPrefs = context.getSharedPreferences("user", 0);
        return myPrefs.getString("itemsalesprice", "0");
    }

    public String getRegular()
    {
        SharedPreferences myPrefs = context.getSharedPreferences("user", 0);
        return myPrefs.getString("Regularprice", "0");
    }
    public String getprice()
    {
        SharedPreferences myPrefs = context.getSharedPreferences("user", 0);
        return myPrefs.getString("price", "0");
    }
    public String getraing()
    {
        SharedPreferences myPrefs = context.getSharedPreferences("user", 0);
        return myPrefs.getString("raing", "0");
    }
    public String getofferName()
    {
        SharedPreferences myPrefs = context.getSharedPreferences("user", 0);
        return myPrefs.getString("offerName", "0");
    }
    public String getofferDescription()
    {
        SharedPreferences myPrefs = context.getSharedPreferences("user", 0);
        return myPrefs.getString("offerDescription", "0");
    }
    public String getofferLabel()
    {
        SharedPreferences myPrefs = context.getSharedPreferences("user", 0);
        return myPrefs.getString("offerLabel", "0");
    }

    // check network access
    public static boolean isNetworkAvaliable(Context ctx) {
        ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if ((connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED)
                || (connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null && connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI)
                .getState() == NetworkInfo.State.CONNECTED)) {
            return true;
        } else {
            return false;
        }
    }


    // set text error
    public static SpannableStringBuilder setError(String message, Context context) {
        int ecolor = Color.RED; // whatever color you want
        ForegroundColorSpan fgcspan = new ForegroundColorSpan(ecolor);
        SpannableStringBuilder ssbuilder = new SpannableStringBuilder(message);
        ssbuilder.setSpan(fgcspan, 0, 0, 0);
        return ssbuilder;
    }

    // check email validation
    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    // Alert Messages
    public static void alertMessage(Context context, String msg) {
        try {
            final Dialog dialog = new Dialog(context);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            // dialog.setContentView(R.layout.custom_alert_dialogbox);
            //  Button ok_button = (Button) dialog.findViewById(R.id.ok_button);
            //  TextView msg_txt = (TextView) dialog.findViewById(R.id.msg_txt);
            //  msg_txt.setText(msg);
            // if button is clicked, close the custom dialog
            //   ok_button.setOnClickListener(new View.OnClickListener() {
            //         @Override
            //        public void onClick(View v) {
            //           dialog.dismiss();
            //        }
            //   });

            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
