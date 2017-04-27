package com.virtualdusk.zezgn.activity.PayInvoiceScreens;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.virtualdusk.zezgn.InterfaceClasses.addOrderInterface;
import com.virtualdusk.zezgn.Utilities.Constant;
import com.virtualdusk.zezgn.Utilities.Utilities;

/**
 * Created by bhart.gupta on 22-Nov-16.
 */

public class AddOrderAsync extends AsyncTask<Void,Void,String> {

    private String strActualJson,strUserId;
    private String TAG = "AddOrderAsync",strUrl;
    private addOrderInterface maddOrderInterface;
    private Context mContext;
    private Utilities mUtilities;

    public AddOrderAsync(Context mContext,String strActualJson, addOrderInterface maddOrderInterface, String strUserId){
        this.strActualJson = strActualJson;
        this.maddOrderInterface = maddOrderInterface;
        this.mContext = mContext;
        this.strUserId = strUserId;
        mUtilities = new Utilities();
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(TAG, "strActualJson: " + strActualJson);
        mUtilities.showProgressDialog(mContext,"Placing your order...");
        if(strUserId.equalsIgnoreCase(mUtilities.getDeviceId(mContext))){
            strUrl = Constant.domain_ + "orders/add-guest-order.json";
        }else{
            strUrl = Constant.domain_ + "orders/add-order.json";

        }
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            HttpResponse response;
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(strUrl);

            httpPost.setEntity(new StringEntity(strActualJson, "UTF-8"));
            httpPost.setHeader("Content-Type", "application/json");
            httpPost.setHeader("Accept-Encoding", "application/json");
            httpPost.setHeader("Accept-Language", "en-US");
            response = httpClient.execute(httpPost);
            //String sresponse = response.getEntity().toString();
            String sresponse = EntityUtils.toString(response.getEntity());
            Log.w("QueingSystem", sresponse);
            //Log.w("QueingSystem", EntityUtils.toString(response.getEntity()));
            return sresponse;
        }
        catch (Exception e) {
            e.printStackTrace();
            Log.d("InputStream", e.getLocalizedMessage());

        }

        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.d(TAG, "onPostExecute: result " + result);
        mUtilities.hideProgressDialog();
        maddOrderInterface.onAddOrderCompleted(result);
    }
}
