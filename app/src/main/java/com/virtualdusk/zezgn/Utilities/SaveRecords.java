package com.virtualdusk.zezgn.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.utils.helper.AppUtils;

/**
 * Created by Amit Sharma on 10/3/2016.
 */
public class SaveRecords {

    public static void saveToPreference(String key,String value, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppUtils.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key, value).apply();
    }
    public static void saveIntegerToPreference(String key,int value, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppUtils.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt(key, value).apply();
    }
   /* public static void saveToPreference(String lang, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppUtils.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(context.getString(R.string.active_locale), lang).apply();
    }*/
}
