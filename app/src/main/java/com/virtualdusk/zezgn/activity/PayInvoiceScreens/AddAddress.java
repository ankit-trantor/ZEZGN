package com.virtualdusk.zezgn.activity.PayInvoiceScreens;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Field;
import com.virtualdusk.zezgn.Model.CartInfo;
import com.virtualdusk.zezgn.Model.CountryItem;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Utilities;
import com.virtualdusk.zezgn.activity.AbstractActivity;
import com.virtualdusk.zezgn.activity.AddPhoto;
import com.virtualdusk.zezgn.activity.Home;
import com.virtualdusk.zezgn.api.AddAddressApi;
import com.virtualdusk.zezgn.api.CountryListApi;
import com.virtualdusk.zezgn.api.GetCartApi;
import com.virtualdusk.zezgn.utils.ConnectionDetector;

/**
 * Created by Amit Sharma on 11/11/2016.
 */
public class AddAddress extends AbstractActivity {

    private EditText edAddressType, edAddress, edCity, edState, edCountry, edPostolCode;
    private ProgressDialog progress_dialog;

    private Button btnAddAddress;
    private String mAddressType, mAddress, mCity, mState, mCountry, mPostolCode, mCountryId, user_id;

    private ArrayList<CountryItem> countrydata;
    private static String[] myStrings;
    private String TAG = AddAddress.class.getSimpleName().toString();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_address);
        countrydata=new ArrayList<>();
        findRes();
        user_id = Home.sharedPreferences.getString(getString(R.string.key_user_id), null);
        if (new ConnectionDetector(this).isConnectingToInternet()) {

            getCountryList();
        } else {
            Toast.makeText(this,getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }


        progress_dialog = new ProgressDialog(AddAddress.this, R.style.MyTheme);
        progress_dialog.setCancelable(false);
        progress_dialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.my_progress_indeterminate));


    }


    private void getCountryList() {
        countrydata=new ArrayList<>();
        //Creating an object of our api interface
        Retrofit restAdapter = CountryListApi.retrofit;
        CountryListApi registerApi = restAdapter.create(CountryListApi.class);

        Call<JsonElement> call = registerApi.getCountryViaJSON();

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.e(TAG, "onResponse: " + response.body());

                try {
                    JSONObject jsnon = new JSONObject(response.body().toString());
                    String response1 = jsnon.getString("response");
                    JSONObject jsonObject = new JSONObject(response1);
                    String status = jsonObject.getString("code");
                    if (status.equals("200")) {
                        JSONArray countrylist = jsonObject.getJSONArray("data");
                        myStrings = new String[countrylist.length()];
                        for (int i = 0; i < countrylist.length(); i++) {
                            JSONObject data = countrylist.getJSONObject(i);
                            CountryItem country = new CountryItem();
                            country.setId(data.getString("id"));
                            country.setIso(data.getString("iso"));
                            country.setName(data.getString("name"));
                            country.setPrintable_name(data.getString("printable_name"));
                            country.setIso3(data.getString("iso3"));
                            country.setNumcode(data.getString("numcode"));
                            countrydata.add(country);
                            myStrings[i] = data.getString("name");


                        }
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<JsonElement> result, Throwable t) {
                Log.e(TAG, "onFailure: " + result.toString());
            }
        });


    }

    public void countryListClick(View view) {
        showCountryList();
    }

    private void setAddressTask() {
        progress_dialog.show();
        //Creating an object of our api interface
        Retrofit restAdapter = AddAddressApi.retrofit;
        AddAddressApi addAddressApi = restAdapter.create(AddAddressApi.class);

        Call<JsonElement> call = addAddressApi.setAddressViaJSON(user_id, mAddressType, mAddress, mCity, mState, mCountryId, mPostolCode);

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.e(TAG, "onResponse: " + response.body());
                progress_dialog.dismiss();
                parseResponse(response.body().toString());
            }

            @Override
            public void onFailure(Call<JsonElement> result, Throwable t) {
                progress_dialog.dismiss();

                Log.e(TAG, "onFailure: " + result.toString());
            }
        });


    }

    private void parseResponse(String response) {
        try {
            JSONObject jsnon = new JSONObject(response);
            String response1 = jsnon.getString("response");
            JSONObject jsonObject = new JSONObject(response1);
            String status = jsonObject.getString("code");
            String message = jsonObject.getString("message");
            if (status.equals("200")) {
                 show(message);
                AddAddress.this.finish();
            } else {
                show(message);

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void onSubmission(View view) {

        mAddressType = edAddressType.getText().toString();
        mAddress = edAddress.getText().toString();
        mCity = edCity.getText().toString();
        mState = edState.getText().toString();
        mCountry = edCountry.getText().toString();
        mPostolCode = edPostolCode.getText().toString();

        if (mAddressType.length() == 0) {
            show("Please enter Address Type");
        } else if (mAddress.length() == 0) {
            show("Please enter Address");

        } else if (mCity.length() == 0) {
            show("Please enter City");

        } else if (mState.length() == 0) {
            show("Please enter State");

        } else if (mCountry.length() == 0) {
            show("Please enter Country");

        } else if (mPostolCode.length() == 0) {
            show("Please enter Postol Code");

        } else {

            if (new ConnectionDetector(AddAddress.this).isConnectingToInternet()) {
                progress_dialog.show();
                setAddressTask();
            } else {
                show(getString(R.string.no_internet));
            }
        }
    }

    private void showCountryList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AddAddress.this);
        builder.setTitle(getString(R.string.select_country));
        builder.setItems(myStrings,
                new DialogInterface.OnClickListener() {
                    @SuppressWarnings("deprecation")

                    public void onClick(DialogInterface dialog, int position) {
                        String accountName = countrydata.get(position).getName();
                        edCountry.setText(accountName);
                        mCountryId = countrydata.get(position).getId();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

private ImageView ivBack;
    private void findRes() {

        ivBack=(ImageView)findViewById(R.id.ivBack);
        btnAddAddress = (Button) findViewById(R.id.btnAddAddress);
        edAddressType = (EditText) findViewById(R.id.edAddressType);
        edAddress = (EditText) findViewById(R.id.edAddress);
        edCity = (EditText) findViewById(R.id.edAddressCity);
        edState = (EditText) findViewById(R.id.edState);
        edCountry = (EditText) findViewById(R.id.edCountry);
        edPostolCode = (EditText) findViewById(R.id.edPostolCode);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             onBackPressed();
            }
        });

    }
}
