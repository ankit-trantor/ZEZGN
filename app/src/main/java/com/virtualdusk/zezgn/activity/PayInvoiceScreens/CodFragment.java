package com.virtualdusk.zezgn.activity.PayInvoiceScreens;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Retrofit;
import com.virtualdusk.zezgn.InterfaceClasses.addOrderInterface;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Utilities;
import com.virtualdusk.zezgn.activity.Shipping.ShippingDetailsFragment;

import com.virtualdusk.zezgn.activity.ShoopingCart.ShoppingCartFragment;
import com.virtualdusk.zezgn.activity.ThankyouActivity;
import com.virtualdusk.zezgn.api.postOrderApi;
import com.virtualdusk.zezgn.utils.ConnectionDetector;
import com.virtualdusk.zezgn.utils.helper.AppUtils;

import static com.virtualdusk.zezgn.activity.Home.sharedPreferences;

/**
 * A simple {@link Fragment} subclass.
 */
public class CodFragment extends Fragment implements View.OnClickListener ,addOrderInterface {

    private ImageView ImgVerify;
    private boolean isChecked = false;
    private TextView TvConfirmOrder;
    private Utilities mUtilities;
    private String TAG = "CodFragment";
    private addOrderInterface maddOrderInterface;
    private String mUserId;
    private String strShippingPrice = "",strShippingAddress = "",strShippingDetails = "",strTotalShippingPrice = "",strMessage ="";
    private String strShippingPrices,strPayments_,strOrder,strActualJson;

    public CodFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cod, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        maddOrderInterface = this;

        ImgVerify = (ImageView)view.findViewById(R.id.imgVerify);
        ImgVerify.setOnClickListener(this);
        TvConfirmOrder = (TextView)view.findViewById(R.id.tvPaynow);
        TvConfirmOrder.setOnClickListener(this);

        mUtilities = new Utilities();
        sharedPreferences = getActivity().getSharedPreferences(AppUtils.PREF_NAME, Context.MODE_PRIVATE);
        mUserId = sharedPreferences.getString(getString(R.string.key_user_id), "");
        if(mUserId.equalsIgnoreCase("-1")){
            mUserId = mUtilities.getDeviceId(getActivity());
        }

    }

    @Override
    public void onClick(View view) {

        if(view == TvConfirmOrder){

            if(isChecked){

                if (new ConnectionDetector(getActivity()).isConnectingToInternet()) {

                    addOrder();
                } else {
                    Toast.makeText(getActivity(),getString(R.string.no_internet),Toast.LENGTH_LONG).show();
                }


            }else{
                mUtilities.showDialog(getActivity(),"Please check verify order","OK");
            }

        }


        if(view == ImgVerify){

            if(isChecked){
                isChecked = false;
                ImgVerify.setImageResource(R.mipmap.right_h);
            }else{
                isChecked = true;

                mUtilities.showDialog(getActivity(),"4 AED will be added to total amount as additional charge for cash payment.","OK");
                ImgVerify.setImageResource(R.mipmap.right);
            }
        }

    }

    public void addOrder(){
        // this method will make json and call async class to make add-order api successfull
        String strexpectedDate = mUtilities.add15days();

        Log.d(TAG,"shipping_prices "  );
        Log.d(TAG,"shipping_address "  );
        Log.d(TAG,"shipping_details " + ""  );
        Log.d(TAG,"shipping_price " + ""  );
        Log.d(TAG,"payment_data " +  ""  );
        Log.d(TAG,"user_id " + mUserId  );
        Log.d(TAG,"total_price " + ShoppingCartFragment.totalPrice+""  );
        Log.d(TAG,"coupon_code " + ShoppingCartFragment.strCouponCode+""  );
        Log.d(TAG,"coupon_price " + ShoppingCartFragment.couponAmount+""  );
        Log.d(TAG,"payment_method " + "1"  );
        Log.d(TAG,"est_delivery_date " + strexpectedDate  );
        Log.d(TAG,"txn_id " +  "TXN_TEST"  );
        Log.d(TAG,"payment_method " +  "1"  );
        Log.d(TAG,"status " +  "1"  );

        try{

            JSONArray mshippingArr = new JSONArray();String[] arr = new String[ShoppingCartFragment.mDotsCount];
            for(int i=0 ; i<ShoppingCartFragment.mDotsCount; i++){

                arr[i] = "20";
                mshippingArr.put("20");
            }
         //   int totalShippingPrice = ShoppingCartFragment.mDotsCount*20;

            JSONObject shippingAddobj = new JSONObject();
            shippingAddobj.put("fname", ShippingDetailsFragment.fname);
            shippingAddobj.put("lname", ShippingDetailsFragment.lname);
            shippingAddobj.put("email", ShippingDetailsFragment.email);
            shippingAddobj.put("contact_no", ShippingDetailsFragment.contact);
            shippingAddobj.put("address", ShippingDetailsFragment.address);
            shippingAddobj.put("city", ShippingDetailsFragment.city);
            shippingAddobj.put("state", ShippingDetailsFragment.state);
            shippingAddobj.put("country_id", ShippingDetailsFragment.countryId);
            shippingAddobj.put("zipcode", ShippingDetailsFragment.zipcode);

            JSONObject orderObj = new JSONObject();
            orderObj.put("total_price",ShoppingCartFragment.totalPrice+"");
            orderObj.put("coupon_code",ShoppingCartFragment.strCouponCode+"");
            orderObj.put("coupon_price",ShoppingCartFragment.couponAmount+"");
            orderObj.put("shipping_address",new JSONObject(shippingAddobj.toString()));
            orderObj.put("payment_method","1");
            orderObj.put("est_delivery_date",strexpectedDate);
            orderObj.put("shipping_details",new JSONObject(shippingAddobj.toString()));
            orderObj.put("shipping_price","22");

            Log.d(TAG,"add orderObj json " + orderObj.toString());

            JSONObject payments = new JSONObject();
            payments.put("txn_id","cod");
            payments.put("payment_data",new JSONObject(shippingAddobj.toString()));
            payments.put("payment_method","1");
            payments.put("status","1");

            Log.d(TAG,"add payments json " + payments.toString());

//            StringBuilder mShippingArray = new StringBuilder();
//            mShippingArray.append("[");
//
//
//
//            mShippingArray.append("]");

            //  Log.d(TAG,"add mShippingArray  " + mShippingArray.toString());

            Retrofit restAdapter = postOrderApi.retrofit;
            postOrderApi addToCartApi = restAdapter.create(postOrderApi.class);


            // strShippingPrices=mShippingArray.toString();
            strPayments_=payments.toString();
            strOrder=orderObj.toString();

            Log.d(TAG, "strUserId: " + mUserId);
            Log.d(TAG, "strShippingPrices: " + strShippingPrices);
            Log.d(TAG, "strPayments_: " + strPayments_);
            Log.d(TAG, "strOrder: " + strOrder);

            JSONObject main = new JSONObject();
            main.put("user_id",mUserId);
           // main.put("shipping_prices",new JSONArray(arr));
            main.put("shipping_price","22");
            main.put("order",new JSONObject(strOrder));
            main.put("payments",new JSONObject(strPayments_));

            strActualJson = main.toString();

            AddOrderAsync mAddOrderAsync = new AddOrderAsync(getActivity(),strActualJson,maddOrderInterface,mUserId);
            mAddOrderAsync.execute();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onAddOrderCompleted(String response) {

        if (response != null) {

            try {
                JSONObject mJObj = new JSONObject(response);
                JSONObject mJsonObj = mJObj.getJSONObject("response");

                if (mJsonObj != null) {

                    if (mJsonObj.has("message") && mJsonObj.getString("message") != null) {
                        strMessage = mJsonObj.getString("message").toString().trim();
                    }

                    if (mJsonObj.has("code") && mJsonObj.getString("code") != null
                            && mJsonObj.getString("code").toString().equalsIgnoreCase("200")) {

                        //1startActivity(new Intent(getActivity(), ThankyouActivity.class));
                        getFragmentManager().beginTransaction().add(R.id.frame, new ThankyouFragment()).addToBackStack(null).commit();
                    }else{
                        Toast.makeText(getActivity(),strMessage,Toast.LENGTH_LONG).show();
                    }
                }
            }catch(Exception e){
                e.printStackTrace();
            }

        }

    }
}
