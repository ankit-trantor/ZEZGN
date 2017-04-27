package com.virtualdusk.zezgn.activity.PayInvoiceScreens;


import android.app.Activity;
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
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import retrofit2.Retrofit;
import com.virtualdusk.zezgn.InterfaceClasses.addOrderInterface;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Utilities;
import com.virtualdusk.zezgn.activity.Shipping.ShippingDetailsFragment;
import com.virtualdusk.zezgn.activity.ShoopingCart.ShoppingCartFragment;
import com.virtualdusk.zezgn.api.postOrderApi;
import com.virtualdusk.zezgn.utils.ConnectionDetector;
import com.virtualdusk.zezgn.utils.helper.AppUtils;

import static com.virtualdusk.zezgn.activity.Home.sharedPreferences;

/**
 * A simple {@link Fragment} subclass.
 */
public class PaypalFragment extends Fragment implements addOrderInterface {

    private ImageView ImgPaypal;
    private static final String TAG = "PaypalFragment";
    /**
     * - Set to PayPalConfiguration.ENVIRONMENT_PRODUCTION to move real money.
     *
     * - Set to PayPalConfiguration.ENVIRONMENT_SANDBOX to use your test credentials
     * from https://developer.paypal.com
     *
     * - Set to PayPalConfiguration.ENVIRONMENT_NO_NETWORK to kick the tires
     * without communicating to PayPal's servers.
     */
    private static final String CONFIG_ENVIRONMENT = PayPalConfiguration.ENVIRONMENT_SANDBOX;

    // note that these credentials will differ between live & sandbox environments.
    private static final String CONFIG_CLIENT_ID = "ATKTHWo0pz3htxb0mIc8xU9kzLoXN_xknu-yMpbgaAoHP9o6-dVcCfHRY7CjBVgOw0XiMKQAMf8Z2enA";

    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final int REQUEST_CODE_FUTURE_PAYMENT = 2;
    private static final int REQUEST_CODE_PROFILE_SHARING = 3;

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(CONFIG_ENVIRONMENT)
            .clientId(CONFIG_CLIENT_ID);
    private String mUserId;
    private Utilities mUtilities;
    private String strShippingPrice = "",strShippingAddress = "",strShippingDetails = "",strTotalShippingPrice = "",strMessage ="";
    private String strShippingPrices,strPayments_,strOrder,strActualJson;

    private addOrderInterface maddOrderInterface;


    public PaypalFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_paypal, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUtilities = new Utilities();
        maddOrderInterface = this;
        sharedPreferences = getActivity().getSharedPreferences(AppUtils.PREF_NAME, Context.MODE_PRIVATE);
        mUserId = sharedPreferences.getString(getString(R.string.key_user_id), "");
        if(mUserId.equalsIgnoreCase("-1")){
            mUserId = mUtilities.getDeviceId(getActivity());
        }

        ImgPaypal = (ImageView)view.findViewById(R.id.imgPaypal);
        ImgPaypal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (new ConnectionDetector(getActivity()).isConnectingToInternet()) {

                    doPayment();
                } else {
                    Toast.makeText(getActivity(),getString(R.string.no_internet),Toast.LENGTH_LONG).show();
                }


            }
        });
    }

    private void doPayment() {

        /*
         * PAYMENT_INTENT_SALE will cause the payment to complete immediately.
         * Change PAYMENT_INTENT_SALE to
         *   - PAYMENT_INTENT_AUTHORIZE to only authorize payment and capture funds later.
         *   - PAYMENT_INTENT_ORDER to create a payment for authorization and capture
         *     later via calls from your server.
         *
         * Also, to include additional payment details and an item list, see getStuffToBuy() below.
         */
        PayPalPayment thingToBuy = getThingToBuy(PayPalPayment.PAYMENT_INTENT_SALE);

        /*
         * See getStuffToBuy(..) for examples of some available payment options.
         */

        Intent intent = new Intent(getActivity(), PaymentActivity.class);

        // send the same configuration for restart resiliency
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);

        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, thingToBuy);

        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    private PayPalPayment getThingToBuy(String paymentIntent) {
        return new PayPalPayment(new BigDecimal(ShoppingCartFragment.TotalCartPrice), "USD", "Zezign",
                paymentIntent);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                PaymentConfirmation confirm =
                        data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if (confirm != null) {
                    try {
                        Log.i(TAG, confirm.toJSONObject().toString(4));
                        Log.i(TAG, confirm.getPayment().toJSONObject().toString(4));
                        /**
                         *  TODO: send 'confirm' (and possibly confirm.getPayment() to your server for verification
                         * or consent completion.
                         * See https://developer.paypal.com/webapps/developer/docs/integration/mobile/verify-mobile-payment/
                         * for more details.
                         *
                         * For sample mobile backend interactions, see
                         * https://github.com/paypal/rest-api-sdk-python/tree/master/samples/mobile_backend
                         */
                        // displayResultText("PaymentConfirmation info received from PayPal");
                        Toast.makeText(
                                getActivity(),
                                "Payment done successfully", Toast.LENGTH_LONG)
                                .show();
                        addOrder();

                    } catch (JSONException e) {
                        Log.e(TAG, "an extremely unlikely failure occurred: ", e);
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.i(TAG, "The user canceled.");
            } else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Log.i(
                        TAG,
                        "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
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
        Log.d(TAG,"total_price " + ShoppingCartFragment.TotalCartPrice+""  );
        Log.d(TAG,"coupon_code " + ShoppingCartFragment.strCouponCode+""  );
        Log.d(TAG,"coupon_price " + ShoppingCartFragment.couponAmount+""  );
        Log.d(TAG,"payment_method " + "2"  );
        Log.d(TAG,"est_delivery_date " + strexpectedDate  );
        Log.d(TAG,"txn_id " +  "TXN_TEST"  );
        Log.d(TAG,"payment_method " +  "2"  );
        Log.d(TAG,"status " +  "1"  );

        try{

            JSONArray mshippingArr = new JSONArray();String[] arr = new String[ShoppingCartFragment.mDotsCount];
            for(int i=0 ; i<ShoppingCartFragment.mDotsCount; i++){

                arr[i] = "20";
                mshippingArr.put("20");
            }
          //  int totalShippingPrice = ShoppingCartFragment.mDotsCount*20;

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
            orderObj.put("total_price",ShoppingCartFragment.TotalCartPrice+"");
            orderObj.put("coupon_code",ShoppingCartFragment.strCouponCode+"");
            orderObj.put("coupon_price",ShoppingCartFragment.couponAmount+"");
            orderObj.put("shipping_address",new JSONObject(shippingAddobj.toString()));
            orderObj.put("payment_method","2");
            orderObj.put("est_delivery_date",strexpectedDate);
            orderObj.put("shipping_details",new JSONObject(shippingAddobj.toString()));
            orderObj.put("shipping_price","18");

            Log.d(TAG,"add orderObj json " + orderObj.toString());

            JSONObject payments = new JSONObject();
            payments.put("txn_id","cod");
            payments.put("payment_data",new JSONObject(shippingAddobj.toString()));
            payments.put("payment_method","2");
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
            main.put("shipping_price","18");
            //main.put("shipping_prices",new JSONArray(arr));
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

                        //startActivity(new Intent(getActivity(), ThankyouActivity.class));
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
