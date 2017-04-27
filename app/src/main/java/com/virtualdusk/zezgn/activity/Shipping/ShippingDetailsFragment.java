package com.virtualdusk.zezgn.activity.Shipping;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.virtualdusk.zezgn.Model.Address;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.activity.Home;
import com.virtualdusk.zezgn.activity.PayInvoiceScreens.AddAddress;
import com.virtualdusk.zezgn.activity.PayInvoiceScreens.PayInvoiceActivity;
import com.virtualdusk.zezgn.activity.PayInvoiceScreens.PayInvoiceFragment;
import com.virtualdusk.zezgn.api.ShippingAddressApi;
import com.virtualdusk.zezgn.utils.ConnectionDetector;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShippingDetailsFragment extends Fragment {


    private String mUserId;
    private ListView listView;
    private String TAG = ShippingDetailsScreen.class.getSimpleName().toString();
    private ArrayList<Address> arrShippingAddrss;
    View view;
    private ImageView ivBack;
    private Button btnNext, btnAddNew;
    private int selectedAddressPosition = -1;
    public static String    fname ,lname,email,contact,address,city,state,countryId,zipcode;
    private TextView TvAddNewAddress;
    private ProgressDialog progress_dialog;
    View header;

    public ShippingDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         view = inflater.inflate(R.layout.shipping_details, container, false);
        header = inflater.inflate(R.layout.footer, listView, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findRes(view);
        mUserId = Home.sharedPreferences.getString(getString(R.string.key_user_id), "");
    }



    @Override
    public void onResume() {
        super.onResume();

        if (new ConnectionDetector(getActivity()).isConnectingToInternet()) {

            getShippingAddressTask();
        } else {
            Toast.makeText(getActivity(),getString(R.string.no_internet),Toast.LENGTH_LONG).show();
        }

    }

    private void findRes(View v) {

        progress_dialog = new ProgressDialog(getActivity(), R.style.MyTheme);
        progress_dialog.setCancelable(false);
        progress_dialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.my_progress_indeterminate));

        TvAddNewAddress = (TextView)v.findViewById(R.id.tvAddNewAddress);
        ivBack = (ImageView)v.findViewById(R.id.ivBack);
        listView = (ListView)v.findViewById(R.id.listView);



        btnNext = (Button)header. findViewById(R.id.btnNext);
        btnAddNew = (Button)header. findViewById(R.id.btnAddNew);

        listView.addFooterView(header, null, false);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(selectedAddressPosition == -1){
                    Toast.makeText(getActivity(),"Please select address",Toast.LENGTH_LONG).show();

                }else{
                    fname = arrShippingAddrss.get(selectedAddressPosition).getFname();
                    lname = arrShippingAddrss.get(selectedAddressPosition).getLname();
                    email = arrShippingAddrss.get(selectedAddressPosition).getEmail();
                    contact = arrShippingAddrss.get(selectedAddressPosition).getContact_no();
                    address = arrShippingAddrss.get(selectedAddressPosition).getAddress();
                    city = arrShippingAddrss.get(selectedAddressPosition).getCity();
                    state = arrShippingAddrss.get(selectedAddressPosition).getState();
                    countryId = arrShippingAddrss.get(selectedAddressPosition).getCountry_id();
                    zipcode = arrShippingAddrss.get(selectedAddressPosition).getZipcode();

//                    Intent i = new Intent(getActivity(), PayInvoiceActivity.class);
//                    startActivity(i);

                    getFragmentManager().beginTransaction().replace(R.id.frame, new PayInvoiceFragment()).addToBackStack(null).commit();
                }

            }
        });
        btnAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // startActivity(new Intent(getActivity(), AddAddress.class));
                Bundle mBundle = new Bundle();
                mBundle.putBoolean("hide_topbar",false);

                AddAddressFragment mAddAddressFragment = new AddAddressFragment();
                mAddAddressFragment.setArguments(mBundle);

                getFragmentManager().beginTransaction().replace(R.id.frame, mAddAddressFragment).addToBackStack(null).commit();
            }
        });

//        ivBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                onBackPressed();
//            }
//        });


    }


    private void getShippingAddressTask() {

        progress_dialog.show();

        arrShippingAddrss = new ArrayList<>();
        //Creating an object of our api interface
        Retrofit restAdapter = ShippingAddressApi.retrofit;
        ShippingAddressApi shippingApi = restAdapter.create(ShippingAddressApi.class);

        Call<JsonElement> call = shippingApi.getAddressViaJSON(getParms());

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.e(TAG, "onResponse: " + response.body());
                progress_dialog.dismiss();

                try {
                    JSONObject jsnon = new JSONObject(response.body().toString());
                    String response1 = jsnon.getString("response");
                    JSONObject jsonObject = new JSONObject(response1);
                    String status = jsonObject.getString("code");
                    if (status.equals("200")) {
                        JSONArray countrylist = jsonObject.getJSONArray("data");
                        for (int i = 0; i < countrylist.length(); i++) {
                            JSONObject data = countrylist.getJSONObject(i);
                            Address address = new Address();
//
//                            "id":17,
//                                    "address_type":"office",
//                                    "user_id":89,
//                                    "address":"64,65 ad nagar,swejfarm",
//                                    "city":"jaipur",
//                                    "state":"rajasthan",
//                                    "country_id":101,
//                                    "zipcode":"302018",
//                                    "created":"2016-09-21T12:36:08+0530",
//                                    "updated":null,
//                                    "country":{},
//                            "fname":"H",
//                                    "lname":"go",
//                                    "email":"kk@kk.com",
//                                    "contact_no":"1254125412"

                            address.setId(data.getString("id"));
                            address.setAddress_type(data.getString("address_type"));
                            address.setUser_id(data.getString("user_id"));
                            address.setAddress(data.getString("address"));
                            address.setCity(data.getString("city"));
                            address.setState(data.getString("state"));
                            address.setCountry_id(data.getString("country_id"));
                            address.setZipcode(data.getString("zipcode"));
                            address.setCreated(data.getString("created"));
                            address.setUpdated(data.getString("updated"));
                            address.setCountry(data.getString("country"));
                            address.setFname(data.getString("fname"));
                            address.setLname(data.getString("lname"));
                            address.setEmail(data.getString("email"));
                            address.setContact_no(data.getString("contact_no"));


                            JSONObject jsonObject_Design = new JSONObject(data.getString("country"));
                            address.setCountry_iso(jsonObject_Design.getString("iso"));
                            address.setCountry_name(jsonObject_Design.getString("name"));
                            address.setCountry_iso3(jsonObject_Design.getString("iso3"));
                            address.setCountry_numcode(jsonObject_Design.getString("numcode"));
                            address.setCountry_printable_name(jsonObject_Design.getString("printable_name"));

                            arrShippingAddrss.add(address);


                        }

                        setAdapter();
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<JsonElement> result, Throwable t) {
                Log.e(TAG, "onFailure: " + result.toString());
                progress_dialog.dismiss();
            }
        });


    }

    private ShippingAddressAdapter addressAdapter;

    private void setAdapter() {

        addressAdapter = new ShippingAddressAdapter(getActivity(), arrShippingAddrss);
        listView.setAdapter(addressAdapter);

        if(arrShippingAddrss.size() == 0){
            TvAddNewAddress.setVisibility(View.VISIBLE);
        }else{
            TvAddNewAddress.setVisibility(View.GONE);
        }
    }

    private Address getParms() {
        Address address = new Address();
        address.setUser_id(mUserId);
        return address;
    }

    public class ShippingAddressAdapter extends BaseAdapter {

        Context c;

        private LayoutInflater inflater = null;

        private ArrayList<Address> arrayList;
        private DisplayImageOptions options;
        private ImageLoader imageLoader = null;// ImageLoader.getInstance();

        public ShippingAddressAdapter(Context context, ArrayList<Address> arrayList) {
            c = context;
            inflater = (LayoutInflater) c
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            this.arrayList = arrayList;
            imageLoader = ImageLoader.getInstance();
            options = new DisplayImageOptions.Builder()
                    .showStubImage(R.mipmap.logo)
                    .showImageForEmptyUri(R.mipmap.logo)
                    .showImageOnFail(R.mipmap.logo)
                    .resetViewBeforeLoading()
                    .delayBeforeLoading(1000)
                    .cacheInMemory()
                    .cacheOnDisc()
                    .build();

        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            View vi = view;
            if (view == null)

                vi = inflater.inflate(R.layout.list_row_shipping, null);


            TextView tvAddressTitle = (TextView) vi.findViewById(R.id.tvOfficeAddress);
            final TextView tvAddress = (TextView) vi.findViewById(R.id.tvAddressOffice);
            TextView tvPinCode = (TextView) vi.findViewById(R.id.tvOfficePin);
            final ImageView ivOfficeCheck = (ImageView) vi.findViewById(R.id.ivOfficeCheck);

            tvAddressTitle.setText(arrayList.get(i).getAddress_type());

            ivOfficeCheck.setTag(i);
            if(selectedAddressPosition == -1){
                ivOfficeCheck.setImageResource(R.mipmap.right_h);
            }else if(selectedAddressPosition > -1){
                if (i == selectedAddressPosition) {
                    ivOfficeCheck.setImageResource(R.mipmap.right);
                }else{
                    ivOfficeCheck.setImageResource(R.mipmap.right_h);
                }
            }

            ivOfficeCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    int tag = (Integer)view.getTag();
                    selectedAddressPosition = tag;
                    Log.d(TAG, "onClick: tag " + tag);


                    notifyDataSetChanged();




                }
            });
            tvAddress.setText(arrayList.get(i).getAddress() + "\n" + arrayList.get(i).getCity() + "," + arrayList.get(i).getState() + "," + arrayList.get(i).getCountry_name());


            return vi;
        }

        @Override
        public long getItemId(int i) {
            return i;
        }


    }


}

