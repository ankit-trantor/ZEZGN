package com.virtualdusk.zezgn.activity.Shipping;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.virtualdusk.zezgn.Model.CountryItem;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.activity.Home;
import com.virtualdusk.zezgn.activity.PayInvoiceScreens.AddAddress;
import com.virtualdusk.zezgn.api.AddAddressApi;
import com.virtualdusk.zezgn.api.CountryListApi;
import com.virtualdusk.zezgn.utils.ConnectionDetector;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddAddressFragment extends Fragment {

    private EditText edAddressType, edAddress, edCity, edState, edCountry, edPostolCode;
    private ProgressDialog progress_dialog;

    private Button btnAddAddress;
    private String mAddressType, mAddress, mCity, mState, mCountry, mPostolCode, mCountryId, user_id;

    private ArrayList<CountryItem> countrydata;
    private static String[] myStrings;
    private String TAG = AddAddressFragment.class.getSimpleName().toString();
    private boolean isHidetopBar = false;
    private RelativeLayout Rl_Topbar;

    public AddAddressFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = this.getArguments();
        if (b != null) {
            isHidetopBar = b.getBoolean("hide_topbar", false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_address, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        countrydata = new ArrayList<>();
        findRes(view);
        user_id = Home.sharedPreferences.getString(getString(R.string.key_user_id), null);
        getCountryList();
        progress_dialog = new ProgressDialog(getActivity(), R.style.MyTheme);
        progress_dialog.setCancelable(false);
        progress_dialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.my_progress_indeterminate));

        if(isHidetopBar){
            Rl_Topbar.setVisibility(View.GONE);
        }else{
            Rl_Topbar.setVisibility(View.VISIBLE);
        }

    }

    private void getCountryList() {
        countrydata = new ArrayList<>();
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

    public void countryListClick() {
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
                getFragmentManager().popBackStack();
            } else {
                show(message);

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void onSubmission() {

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

            if (new ConnectionDetector(getActivity()).isConnectingToInternet()) {
                progress_dialog.show();
                setAddressTask();
            } else {
                show(getString(R.string.no_internet));
            }
        }
    }

    private void showCountryList() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

    private void findRes(View view) {

        Rl_Topbar = (RelativeLayout) view.findViewById(R.id.rlTopBar);

        ivBack = (ImageView) view.findViewById(R.id.ivBack);
        btnAddAddress = (Button) view.findViewById(R.id.btnAddAddress);
        edAddressType = (EditText) view.findViewById(R.id.edAddressType);
        edAddress = (EditText) view.findViewById(R.id.edAddress);
        edCity = (EditText) view.findViewById(R.id.edAddressCity);
        edState = (EditText) view.findViewById(R.id.edState);
        edCountry = (EditText) view.findViewById(R.id.edCountry);
        edPostolCode = (EditText) view.findViewById(R.id.edPostolCode);
        btnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSubmission();
            }
        });

        edCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                countryListClick();
            }
        });

    }

    public void show(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

}

