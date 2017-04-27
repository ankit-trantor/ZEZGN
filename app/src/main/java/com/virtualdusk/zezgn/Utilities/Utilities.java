package com.virtualdusk.zezgn.Utilities;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.virtualdusk.zezgn.InterfaceClasses.DialogInterface;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.activity.LoginActivity;
import com.virtualdusk.zezgn.utils.helper.AppUtils;

/**
 * Created by bhart.gupta on 08-Nov-16.
 */

public class Utilities {

    private ProgressDialog progressDialog ;

    public void showProgressDialog(Context mContext, String strMessage) {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.setMessage(strMessage);
        } else {
            progressDialog = ProgressDialog.show(mContext, "", strMessage, false);
        }
    }

    public void hideProgressDialog() {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showCancelableDialog(Context mContext, String msg, String btnText, final DialogInterface mDialogInterface,final String DialogName, final String Message){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        View dialogView = inflater.inflate(R.layout.alert_dialog, null);
        dialogBuilder.setView(dialogView);

        TextView TvAlert = (TextView) dialogView.findViewById(R.id.tvAlert);
        TvAlert.setText(msg);

        Button BtnCancel = (Button)dialogView.findViewById(R.id.btnCancel);
        Button BtnYes = (Button)dialogView.findViewById(R.id.btnYes);
        BtnYes.setText(btnText);

        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        BtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
                mDialogInterface.onCancelButtonClick(DialogName,Message);
            }
        });

        BtnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
                mDialogInterface.onOkButtonClick(DialogName,Message);
            }
        });

        alertDialog.show();
    }

    public void showDialogWithInterface(Context mContext, String msg, String btnText, final DialogInterface mDialogInterface,final String DialogName,final String Message){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        View dialogView = inflater.inflate(R.layout.alert, null);
        dialogBuilder.setView(dialogView);

        TextView TvAlert = (TextView) dialogView.findViewById(R.id.tvAlert);
        TvAlert.setText(msg);

        Button BtnYes = (Button)dialogView.findViewById(R.id.btnYes);
        BtnYes.setText(btnText);

        final AlertDialog alertDialog = dialogBuilder.create();

        BtnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
                mDialogInterface.onOkButtonClick(DialogName,Message);
            }
        });

        alertDialog.show();
    }

    public void showDialog(Context mContext, String msg, String btnText){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        View dialogView = inflater.inflate(R.layout.alert, null);
        dialogBuilder.setView(dialogView);

        TextView TvAlert = (TextView) dialogView.findViewById(R.id.tvAlert);
        TvAlert.setText(msg);

        Button BtnYes = (Button)dialogView.findViewById(R.id.btnYes);
        BtnYes.setText(btnText);

        final AlertDialog alertDialog = dialogBuilder.create();

        BtnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    public String add15days(){

        Calendar c = Calendar.getInstance();
        c.setTime(c.getTime());
        c.add(Calendar.DATE, 15);
        Date d = c.getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String date = dateFormat.format(d);
        return date;

    }

    public void showLoginDialog(final Context mContext, final String Msg){

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);

        View dialogView = inflater.inflate(R.layout.alert_dialog, null);
        dialogBuilder.setView(dialogView);

        TextView TvAlert = (TextView) dialogView.findViewById(R.id.tvAlert);
        TvAlert.setText(Msg);

        Button BtnCancel = (Button)dialogView.findViewById(R.id.btnCancel);
        Button BtnYes = (Button)dialogView.findViewById(R.id.btnYes);
        BtnYes.setText("Login");

        final AlertDialog alertDialog = dialogBuilder.create();

        BtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                alertDialog.dismiss();
            }
        });

        BtnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                SharedPreferences sharedPreferences = mContext.getSharedPreferences(AppUtils.PREF_NAME, Context.MODE_PRIVATE);
                SaveRecords.saveToPreference(mContext.getString(R.string.key_user_id),null,mContext);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                Intent i = new Intent(mContext, LoginActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(i);

            }
        });

        alertDialog.show();
    }


    public String getDeviceId(Context mContext){
        String android_id = Settings.Secure.getString(mContext.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return android_id;
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }
}
