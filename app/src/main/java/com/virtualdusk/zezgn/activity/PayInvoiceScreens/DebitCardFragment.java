package com.virtualdusk.zezgn.activity.PayInvoiceScreens;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.virtualdusk.zezgn.InterfaceClasses.DialogInterface;
import com.virtualdusk.zezgn.InterfaceClasses.MakePaymentInterface;
import com.virtualdusk.zezgn.InterfaceClasses.addOrderInterface;
import com.virtualdusk.zezgn.Model.Payment;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Utilities;
import com.virtualdusk.zezgn.activity.Shipping.ShippingDetailsFragment;
import com.virtualdusk.zezgn.activity.ShoopingCart.ShoppingCartFragment;
import com.virtualdusk.zezgn.activity.ThankyouActivity;
import com.virtualdusk.zezgn.api.MakePaymentApi;
import com.virtualdusk.zezgn.api.postOrderApi;
import com.virtualdusk.zezgn.utils.ConnectionDetector;
import com.virtualdusk.zezgn.utils.helper.AppUtils;

import static com.virtualdusk.zezgn.activity.Home.sharedPreferences;

/**
 * A simple {@link Fragment} subclass.
 */
public class DebitCardFragment extends Fragment implements View.OnClickListener, DatePickerDialog.OnDateSetListener, MakePaymentInterface, DialogInterface,addOrderInterface {


    private static final String TAG = "CreditCard";
    private String strTotalAmount = "", strCardType = "", strCardNo = "", strExpMonth = "", strExpYear ="", strCvv = "",mUserId="";
    private EditText ET_CardType,ET_CardNumber,ET_Expiration,ET_Cvv;
    private TextView TV_BtnPayNow;

    private MakePaymentInterface mMakePaymentInterface;
    private addOrderInterface maddOrderInterface;
    private DialogInterface mDialogInterface;
    private Utilities mUtilities;

    private String [] cardTypeList = {"AmericanExpress","CarteAura","CarteAuroroe","Confinoga","Discover","JCB",
            "Maestro","MasterCard","SwitchMaestro","TarjetaAurora","Visa","4etoiles"};
    private String strShippingPrice = "",strShippingAddress = "",strShippingDetails = "",strTotalShippingPrice = "",strMessage ="";
    private String strShippingPrices,strPayments_,strOrder;
    private String strActualJson = "";

    public DebitCardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMakePaymentInterface = this;
        maddOrderInterface = this;
        mDialogInterface = this;
        mUtilities = new Utilities();

        sharedPreferences = getActivity().getSharedPreferences(AppUtils.PREF_NAME, Context.MODE_PRIVATE);
        mUserId = sharedPreferences.getString(getString(R.string.key_user_id), "");
        if(mUserId.equalsIgnoreCase("-1")){
            mUserId = mUtilities.getDeviceId(getActivity());
        }

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_debit_card, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ET_CardNumber = (EditText)view.findViewById(R.id.etCardnumber);
        ET_CardType = (EditText)view.findViewById(R.id.etCardtype);
        ET_Expiration = (EditText)view.findViewById(R.id.etExpiration);
        ET_Cvv = (EditText)view.findViewById(R.id.etSecuritycode);
        TV_BtnPayNow = (TextView)view.findViewById(R.id.tvPaynow);

        ET_CardType.setOnClickListener(this);
        ET_Expiration.setOnClickListener(this);
        TV_BtnPayNow.setOnClickListener(this);
    }

    private void showCountryList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.select_card_type));

        builder.setItems(cardTypeList,
                new android.content.DialogInterface.OnClickListener() {
                    @Override
                    @SuppressWarnings("deprecation")
                    public void onClick(android.content.DialogInterface dialogInterface, int position) {
                        strCardType = cardTypeList[position];
                        ET_CardType.setText(strCardType);
                    }

                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.tvPaynow : validate(); break;
            case R.id.etCardtype :showCountryList(); break;
            case R.id.etExpiration :
                DatePicker pd = new DatePicker();
                pd.setListener(this);
                pd.show(getActivity().getFragmentManager(),"MonthYearPickerDialog");
                break;
        }
    }

    private void validate() {

        strCardNo = ET_CardNumber.getText().toString().trim();
        strCardType = ET_CardType.getText().toString().trim();
        strCvv = ET_Cvv.getText().toString().trim();

        if(strCardType.isEmpty()){

            mUtilities.showDialog(getActivity(),"Please select card type","OK");
            return;
        }

        if(strCardNo.isEmpty()){

            mUtilities.showDialog(getActivity(),"Please enter Card number","OK");
            return;
        }
        if(strExpMonth.isEmpty()){

            mUtilities.showDialog(getActivity(),"Please select expiry date","OK");
            return;
        }

        if(strCvv.isEmpty()){

            mUtilities.showDialog(getActivity(),"Please enter CVV Number","OK");
            return;
        }

        if (new ConnectionDetector(getActivity()).isConnectingToInternet()) {

            makePaymentViaRetrofit();
        } else {
            Toast.makeText(getActivity(),getString(R.string.no_internet),Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public void onDateSet(android.widget.DatePicker datePicker, int year, int month, int i2) {

        strExpMonth = month < 10 ? "0" + month : Integer.toString(month);
        strExpYear = year+"";

        Log.d(TAG,"year " + year);
        Log.d(TAG,"month " + month);
        ET_Expiration.setText(strExpMonth + "/" + strExpYear);

    }

    private void makePaymentViaRetrofit() {

        mUtilities.showProgressDialog(getActivity(),"Making Payment...");

        //request { total_amount : 100 , card_type : Visa, card_no : 4311197036085330 , exp_month : 04 , exp_year : 2021, cvv : 000}

        //Creating an object of our api interface
        Retrofit restAdapter = MakePaymentApi.retrofit;
        MakePaymentApi paymentApi = restAdapter.create(MakePaymentApi.class);

        Call<JsonElement> call = paymentApi.makePaymentViaJSON(ShoppingCartFragment.TotalCartPrice+"",strCardType,strCardNo,strExpMonth,strExpYear,strCvv);


        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                mUtilities.hideProgressDialog();
                ParseMakePaymentData mParser = new ParseMakePaymentData();
                mParser.parseData(response.body().toString(),"DebitCard",mMakePaymentInterface);
                Log.e(TAG, "onResponse: " + response.body());
            }

            @Override
            public void onFailure(Call<JsonElement> result, Throwable t) {
                mUtilities.hideProgressDialog();
                Log.e(TAG, "onFailure: " + result.toString());
            }
        });

    }


    private Payment getPaymentJSON()
    {

        Log.d(TAG, "strCardNo: " +strCardNo);
        Log.d(TAG, "strCardType: " +strCardType);
        Log.d(TAG, "strCvv: " +strCvv);
        Log.d(TAG, "strExpMonth: " +strExpMonth);
        Log.d(TAG, "strExpYear: " +strExpYear);

        Payment mPayment = new Payment();
        mPayment.setCard_no(strCardNo);
        mPayment.setCard_type(strCardType);
        mPayment.setCvv(strCvv);
        mPayment.setExp_month(strExpMonth);
        mPayment.setExp_year(strExpYear);
        mPayment.setTotal_amount(ShoppingCartFragment.TotalCartPrice+"");

        return mPayment;
    }


    @Override
    public void onPaymentSuccess(String ClassName, String TxnId, String paymentData) {
        addOrder();
    }

    @Override
    public void onPaymentError(String ClassName, String errorMsg) {

        mUtilities.showDialog(getActivity(),errorMsg,"OK");
    }

    @Override
    public void onCancelButtonClick(String DialogName, String Message) {

    }

    @Override
    public void onOkButtonClick(String DialogName, String Message) {

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
            orderObj.put("total_price",ShoppingCartFragment.TotalCartPrice+"");
            orderObj.put("coupon_code",ShoppingCartFragment.strCouponCode+"");
            orderObj.put("coupon_price",ShoppingCartFragment.couponAmount+"");
            orderObj.put("shipping_address",new JSONObject(shippingAddobj.toString()));
            orderObj.put("payment_method","3");
            orderObj.put("est_delivery_date",strexpectedDate);
            orderObj.put("shipping_details",new JSONObject(shippingAddobj.toString()));
            orderObj.put("shipping_price","18");

            Log.d(TAG,"add orderObj json " + orderObj.toString());

            JSONObject payments = new JSONObject();
            payments.put("txn_id","TXN_TEST");
            payments.put("payment_data",new JSONObject(shippingAddobj.toString()));
            payments.put("payment_method","3");
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

                       // startActivity(new Intent(getActivity(), ThankyouActivity.class));
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



