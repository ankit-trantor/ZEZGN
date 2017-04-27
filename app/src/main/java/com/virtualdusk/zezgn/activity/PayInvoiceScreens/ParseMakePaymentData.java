package com.virtualdusk.zezgn.activity.PayInvoiceScreens;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.virtualdusk.zezgn.InterfaceClasses.MakePaymentInterface;

/**
 * Created by bhart.gupta on 10-Nov-16.
 */

public class ParseMakePaymentData {

    private static String TAG = "ParseMakePaymentData";
    private String strMessage = "";
    private String strErrorMsg = "";
    private String strTransactionId = "";

    public void parseData(final String Response,final String classname, final MakePaymentInterface mMakePaymentInterface){

        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                parse(Response);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);

                if(strMessage.equalsIgnoreCase("Some thing went wrong") || strMessage.equalsIgnoreCase("error")){
                    mMakePaymentInterface.onPaymentError(classname,strErrorMsg);
                }else if(strMessage.equalsIgnoreCase("ok")){
                    mMakePaymentInterface.onPaymentSuccess(classname,strTransactionId,"");
                }
            }
        }.execute();

    }

    private void parse(String response) {

        if(response != null){

            try {
                JSONObject mResponseObj = new JSONObject(response);
                JSONObject mJsonObj = mResponseObj.getJSONObject("response");

                if(mJsonObj != null){

                    if(mJsonObj.has("code") && mJsonObj.getString("code") != null && mJsonObj.getString("code").equalsIgnoreCase("200")){

                        if(mJsonObj.has("data")){

                            JSONObject mDataObj = mJsonObj.getJSONObject("data");
                            if(mDataObj != null){
                                if(mDataObj.has("ACK") && mDataObj.getString("ACK") != null){
                                    if(mDataObj.getString("ACK").equalsIgnoreCase("Failure")){

                                        if(mDataObj.has("ERRORS")){
                                            Log.d(TAG,"error"+"");
                                            strMessage = "error";
                                            JSONArray mErrorArray = mDataObj.getJSONArray("ERRORS");
                                            for(int i=0; i<mErrorArray.length(); i++){
                                                JSONObject mErrorObj = mErrorArray.getJSONObject(i);
                                                if(mErrorObj != null){
                                                    if(mErrorObj.has("L_LONGMESSAGE") && mErrorObj.getString("L_LONGMESSAGE") != null){
                                                        strErrorMsg = mErrorObj.getString("L_LONGMESSAGE").toString();
                                                    }
                                                }
                                            }
                                        }
                                    }else if(mDataObj.getString("ACK").equalsIgnoreCase("Success")){
                                        strMessage = "ok";
                                        Log.d(TAG,"success"+"");
                                        if(mDataObj.has("TRANSACTIONID")){
                                            strTransactionId = mDataObj.getString("TRANSACTIONID").toString();
                                        }


                                    }
                                }
                            }
                        }



                    }else{

                        strMessage = "Some thing went wrong";
                        strErrorMsg = "Some thing went wrong";
                    }

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
