package com.virtualdusk.zezgn.utils;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import com.virtualdusk.zezgn.R;


public class IOUtils {
    public static String getPostURLResponse(ArrayList<NameValuePair> data, String url, Activity activity, final Context context) {
        HttpResponse response = null;
        String result = "";

        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

        for (int index = 0; index < data.size(); index++) {
            try {
                entity.addPart(data.get(index).getName(), new StringBody(data.get(index).getValue(), "null", Charset.forName("UTF-8")));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            Log.e(data.get(index).getName(), data
                    .get(index).getValue());
        }

        try {
            if (StoreInfo.isNetworkAvaliable(context)) {
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 60000);
                HttpConnectionParams.setSoTimeout(httpParameters, 60000);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                HttpPost httppost = new HttpPost(url);
                httppost.setEntity(entity);
                response = httpclient.execute(httppost);
                result = EntityUtils.toString(response.getEntity());
            } else {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, context.getString(R.string.socket_timeout), Toast.LENGTH_LONG).show();
                    }
                });

            }
            // Could do something better with response.
        } catch (SocketTimeoutException e) {
            try {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                       // StoreInfo.alertMessage(context, context.getString(R.string.socket_timeout));
                    }
                });
            } catch (Exception e1) {

            }


        } catch (ConnectTimeoutException e) {
            try {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        StoreInfo.alertMessage(context, context.getString(R.string.socket_timeout));
                    }
                });
            } catch (Exception e2) {

            }

        } catch (Exception e) {

        }
        return result;
    }

    public static String getPostURLResponse2(ArrayList<NameValuePair> data, String url, Activity activity, final Context context) {
        HttpResponse response = null;
        String result = "";

        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

        for (int index = 0; index < data.size(); index++) {
            try {
                entity.addPart(data.get(index).getName(), new StringBody(data.get(index).getValue(), "null", Charset.forName("UTF-8")));
            } catch (UnsupportedEncodingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        try {
            if (StoreInfo.isNetworkAvaliable(context)) {
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 60000);
                HttpConnectionParams.setSoTimeout(httpParameters, 60000);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                HttpPost httppost = new HttpPost(url);
                httppost.setEntity(entity);
                httppost.setHeader("Accept", "application/json");
             //   httppost.addHeader("Authorization",header);
                response = httpclient.execute(httppost);
                result = EntityUtils.toString(response.getEntity());
            } else {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, context.getString(R.string.socket_timeout), Toast.LENGTH_LONG).show();
                    }
                });

            }
            // Could do something better with response.
        } catch (SocketTimeoutException e) {
            try {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        StoreInfo.alertMessage(context, context.getString(R.string.socket_timeout));
                    }
                });
            } catch (Exception e1) {

            }


        } catch (ConnectTimeoutException e) {
            try {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        StoreInfo.alertMessage(context, context.getString(R.string.socket_timeout));
                    }
                });
            } catch (Exception e2) {

            }

        } catch (Exception e) {

        }
        return result;
    }

    public static String gerPostUrlResponse(ArrayList<NameValuePair> data, String url, byte[] img_byte, String image_name, Activity activity, final Context context, String picname) {
        HttpResponse response = null;
        String result = "";
        MultipartEntity entity = new MultipartEntity(
                HttpMultipartMode.BROWSER_COMPATIBLE);
        System.out.println("!!!!!!!!!!!!!!"+picname);

        for (int index = 0; index < data.size(); index++) {
            Log.e(data.get(index).getName(), data.get(index).getValue());
            try {

                entity.addPart(data.get(index).getName(), new StringBody(data
                        .get(index).getValue(), "null",
                        Charset.forName("UTF-8")));

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try {
            entity.addPart(picname, new ByteArrayBody(img_byte, image_name));
        } catch (Exception e) {
        }
        try {
            if (StoreInfo.isNetworkAvaliable(context)) {
                HttpParams httpParameters = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(httpParameters, 60000);
                HttpConnectionParams.setSoTimeout(httpParameters, 60000);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                HttpPost httppost = new HttpPost(url);
                // Add your data

                httppost.setEntity(entity);
                response = httpclient.execute(httppost);
                result = EntityUtils.toString(response.getEntity());
            } else

            {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, context.getString(R.string.socket_timeout), Toast.LENGTH_LONG).show();
                    }
                });

            }
            // Could do something better with response.
        } catch (SocketTimeoutException e) {
            try {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        StoreInfo.alertMessage(context, context.getString(R.string.socket_timeout));
                    }
                });
            } catch (Exception e1) {

            }


        } catch (ConnectTimeoutException e) {
            try {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        StoreInfo.alertMessage(context, context.getString(R.string.socket_timeout));
                    }
                });
            } catch (Exception e2) {

            }

        } catch (Exception e) {

        }
        return result;
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is), 8 * 1024);
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
        } finally {
            try {
                is.close();
            } catch (IOException e) {
            }
        }

        return sb.toString();

    }
}
