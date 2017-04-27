package com.virtualdusk.zezgn.api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import com.virtualdusk.zezgn.InterfaceClasses.ParseStyleInterface;
import com.virtualdusk.zezgn.Model.ProductStyleColor;
import com.virtualdusk.zezgn.Model.Style;

/**
 * Created by bhart.gupta on 03-Nov-16.
 */

public class parseStyleApiResponse {

    // This class is parsing response of style Api

    private static final String TAG = "parseStyleApiResponse";
    private Context mContext;
    private String strMessage= "";
    private ArrayList<Style> productStyleList = new ArrayList<>();
    private ArrayList<ProductStyleColor> productStyleColorList = new ArrayList<>();
    private ParseStyleInterface mParseStyleInterface;

    public parseStyleApiResponse(Context mContext, final String response, final ParseStyleInterface mParseStyleInterface){

        this.mContext = mContext;
        this.mParseStyleInterface = mParseStyleInterface;

        new AsyncTask() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Object doInBackground(Object[] objects) {
                parseResponse(response);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                Log.d(TAG,"strMessage " + strMessage );

                if(strMessage.equalsIgnoreCase("ok")){
                    mParseStyleInterface.onStyleResponseSuccess(productStyleList,"ok");
                }else{
                    mParseStyleInterface.onStyleResponseError(strMessage);
                }
            }
        }.execute();



    }

    private  void parseResponse(String response) {

        Log.d(TAG,"s " + response);

        try {

            JSONObject jsonobj = new JSONObject(response);
            JSONObject jsnon = jsonobj.getJSONObject("response");

            if(jsnon != null){

                if(jsnon.has("message") && jsnon.getString("message") != null){
                    strMessage = jsnon.getString("message").toString().trim();
                }

                if(jsnon.has("code") && jsnon.getString("code") != null
                        && jsnon.getString("code").toString().equalsIgnoreCase("200")){

                    strMessage = "ok";

                    JSONArray mDataArr = jsnon.getJSONArray("data");
                    if(mDataArr.length() > 0){
                        for(int i=0; i<mDataArr.length();i++){
                            Style mStyle = new Style();
                            JSONObject mDataObj = mDataArr.getJSONObject(i);
                            if(mDataObj != null){

                                if(mDataObj.has("id") && mDataObj.getString("id") != null){
                                    mStyle.setId(mDataObj.getString("id").toString().trim());
                                }else{
                                    mStyle.setId("");
                                }

                                if(mDataObj.has("user_id") && mDataObj.getString("user_id") != null){
                                    mStyle.setUserId(mDataObj.getString("user_id").toString().trim());
                                }else{
                                    mStyle.setUserId("");
                                }
                                if(mDataObj.has("product_id") && mDataObj.getString("product_id") != null){
                                    mStyle.setProductId(mDataObj.getString("product_id").toString().trim());
                                }else{
                                    mStyle.setProductId("");
                                }
                                if(mDataObj.has("style_name") && mDataObj.getString("style_name") != null){
                                    mStyle.setStyleName(mDataObj.getString("style_name").toString().trim());
                                }else{
                                    mStyle.setStyleName("");
                                }
                                if(mDataObj.has("style_img") && mDataObj.getString("style_img") != null){
                                    mStyle.setStyleImage(mDataObj.getString("style_img").toString().trim());
                                }else{
                                    mStyle.setStyleImage("");
                                }
                                if(mDataObj.has("price") && mDataObj.getString("price") != null){
                                    mStyle.setPrice(mDataObj.getString("price").toString().trim());
                                }else{
                                    mStyle.setPrice("");
                                }
                                if(mDataObj.has("status") && mDataObj.getString("status") != null){
                                    mStyle.setStatus(mDataObj.getString("status").toString().trim());
                                }else{
                                    mStyle.setStatus("");
                                }
                                if(mDataObj.has("display_order") && mDataObj.getString("display_order") != null){
                                    mStyle.setDisplayorder(mDataObj.getString("display_order").toString().trim());
                                }else{
                                    mStyle.setDisplayorder("");
                                }
                                if(mDataObj.has("display_order") && mDataObj.getString("display_order") != null){
                                    mStyle.setDisplayorder(mDataObj.getString("display_order").toString().trim());
                                }else{
                                    mStyle.setDisplayorder("");
                                }
                                if(mDataObj.has("img_printable_area")){
                                    JSONObject mPrintAreaObj = mDataObj.getJSONObject("img_printable_area");
                                    if(mPrintAreaObj != null){

                                        if(mPrintAreaObj.has("x1") && mPrintAreaObj.getString("x1") != null){
                                            mStyle.setX1(mPrintAreaObj.getString("x1").toString().trim());
                                        }else{
                                            mStyle.setX1("");
                                        }
                                        if(mPrintAreaObj.has("x2") && mPrintAreaObj.getString("x2") != null){
                                            mStyle.setX2(mPrintAreaObj.getString("x2").toString().trim());
                                        }else{
                                            mStyle.setX2("");
                                        }

                                        if(mPrintAreaObj.has("y1") && mPrintAreaObj.getString("y1") != null){
                                            mStyle.setY1(mPrintAreaObj.getString("y1").toString().trim());
                                        }else{
                                            mStyle.setY1("");
                                        }

                                        if(mPrintAreaObj.has("y2") && mPrintAreaObj.getString("y2") != null){
                                            mStyle.setY2(mPrintAreaObj.getString("y2").toString().trim());
                                        }else{
                                            mStyle.setY2("");
                                        }
                                        if(mPrintAreaObj.has("w") && mPrintAreaObj.getString("w") != null){
                                            mStyle.setW(mPrintAreaObj.getString("w").toString().trim());
                                        }else{
                                            mStyle.setW("");
                                        }
                                        if(mPrintAreaObj.has("h") && mPrintAreaObj.getString("h") != null){
                                            mStyle.setH(mPrintAreaObj.getString("h").toString().trim());
                                        }else{
                                            mStyle.setH("");
                                        }
                                    }

                                }

                                if(mDataObj.has("product_style_colors")){

                                    productStyleColorList = new ArrayList<>();

                                    JSONArray mStyleColorArr = mDataObj.getJSONArray("product_style_colors");

                                    for(int j=0;j<mStyleColorArr.length();j++){

                                        ProductStyleColor mProductStyleColor = new ProductStyleColor();

                                        JSONObject mStyleColorobj = mStyleColorArr.getJSONObject(j);
                                        if(mStyleColorobj != null){

                                            if(mStyleColorobj.has("id") && mStyleColorobj.getString("id") != null){
                                                mProductStyleColor.setId(mStyleColorobj.getString("id").toString().trim());
                                            }else{
                                                mProductStyleColor.setId("");
                                            }
                                            if(mStyleColorobj.has("product_style_color") && mStyleColorobj.getString("product_style_color") != null){
                                                mProductStyleColor.setProductStyleColor(mStyleColorobj.getString("product_style_color").toString().trim());
                                            }else{
                                                mProductStyleColor.setProductStyleColor("");
                                            }
                                            if(mStyleColorobj.has("product_style_color_name") && mStyleColorobj.getString("product_style_color_name") != null){
                                                mProductStyleColor.setProductStyleColorName(mStyleColorobj.getString("product_style_color_name").toString().trim());
                                            }else{
                                                mProductStyleColor.setProductStyleColorName("");
                                            }
                                            if(mStyleColorobj.has("product_style_id") && mStyleColorobj.getString("product_style_id") != null){
                                                mProductStyleColor.setProductStyleID(mStyleColorobj.getString("product_style_id").toString().trim());
                                            }else{
                                                mProductStyleColor.setProductStyleID("");
                                            }

                                        }
                                        productStyleColorList.add(mProductStyleColor);
                                    }

                                    mStyle.setProductStyleColor(productStyleColorList);

                                }
                            }
                            productStyleList.add(mStyle);
                        }
                    }

                } else {
                    //showToast(strMessage);

                }
            }



        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
