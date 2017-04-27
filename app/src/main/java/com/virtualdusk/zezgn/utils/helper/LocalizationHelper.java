package com.virtualdusk.zezgn.utils.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

import com.virtualdusk.zezgn.R;


/**
 * Created by hitesh.mathur on 8/8/2016.
 */
public class LocalizationHelper {

    public static void setLocale(String lang, Context context) {
        Locale myLocale = new Locale(lang);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }

    public static void saveToPreference(String lang, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(AppUtils.PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(context.getString(R.string.active_locale), lang).apply();
    }
}
