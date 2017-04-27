package com.virtualdusk.zezgn.utils.helper;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by hitesh.mathur on 8/8/2016.
 */
public class AppUtils {

    public static final String PREF_NAME = "ZEZIGN_PREF_NAME";
//    public static String getRealPathFromURI(Context mContext, Uri contentURI) {
//
//        Cursor cursor = mContext.getContentResolver().query(contentURI, null, null, null, null);
//        if (cursor == null) { // Source is Dropbox or other similar local file path
//            return contentURI.getPath();
//        } else {
//
//            cursor.moveToFirst();
//            //int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//            int idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
//            return cursor.getString(idx);
//        }
//    }


    public static String getRealPathFromURI(Activity context, Uri contentURI) {
        String[] projection = {MediaStore.Images.Media.DATA};
        @SuppressWarnings("deprecation")
        Cursor cursor = context.managedQuery(contentURI, projection, null,
                null, null);
        if (cursor == null)
            return null;
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        if (cursor.moveToFirst()) {
            String s = cursor.getString(column_index);
            // cursor.close();
            return s;
        }
        // cursor.close();
        return null;
    }

}
