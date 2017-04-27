package com.virtualdusk.zezgn.activity.MyAccount;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import com.google.gson.JsonElement;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.virtualdusk.zezgn.InterfaceClasses.onDesignClickListner;
import com.virtualdusk.zezgn.Model.MyCreations;
import com.virtualdusk.zezgn.Model.OrderClass;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Constant;
import com.virtualdusk.zezgn.Utilities.Utilities;
import com.virtualdusk.zezgn.activity.ClickListener;
import com.virtualdusk.zezgn.activity.CustomDesignActivity;
import com.virtualdusk.zezgn.activity.Home;
import com.virtualdusk.zezgn.activity.RecyclerTouchListener;
import com.virtualdusk.zezgn.api.MyCreationsApi;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyCreationsListFragment extends Fragment implements onDesignClickListner {

    private String TAG = MyCreationsListFragment.class.getSimpleName().toString();
    private RecyclerView RV_MyCreations;
    private GridLayoutManager lLayout;
    private ArrayList<MyCreations> myCreationsList = new ArrayList<>();
    private String strUserId = "";
    private TextView TV_lblNoRecord;
    private onDesignClickListner monDesignClickListner;


    public MyCreationsListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_creations_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
    }

    private void initViews(View view) {

        monDesignClickListner = this;

        lLayout = new GridLayoutManager(getActivity(), 2);
        strUserId = Home.sharedPreferences.getString(getString(R.string.key_user_id), null);
        Log.d(TAG, "strUserId " + strUserId);

        RV_MyCreations = (RecyclerView) view.findViewById(R.id.myCreationsRecyclerView);
        TV_lblNoRecord = (TextView) view.findViewById(R.id.lblNoRecord);
        RV_MyCreations.setHasFixedSize(true);
        RV_MyCreations.setLayoutManager(lLayout);

        getCreationsListViaRetrofit();
        //TV_lblNoRecord.setVisibility(View.VISIBLE);

        RV_MyCreations.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), RV_MyCreations, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    private void getCreationsListViaRetrofit() {

        final Utilities mUtilities = new Utilities();
        mUtilities.showProgressDialog(getActivity(), "Please wait, fetching your creations");

        //Creating an object of our api interface
        Retrofit restAdapter = MyCreationsApi.retrofit;
        MyCreationsApi creationsApi = restAdapter.create(MyCreationsApi.class);

        Call<JsonElement> call = creationsApi.getCreationsListViaJSON(getParams());

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.d(TAG, "onResponse: " + response.body());

                try {
                    mUtilities.hideProgressDialog();
                    ParseResponse(response.body().toString());

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> result, Throwable t) {
                Log.d(TAG, "onFailure: ");
                mUtilities.hideProgressDialog();
            }
        });
    }

    private void ParseResponse(final String response) {


        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                Log.e("Response..",response);
                ParseData(response);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);

                if (myCreationsList.size() > 0) {
                    setAdapter();
                } else {
                    TV_lblNoRecord.setVisibility(View.VISIBLE);
                }


            }
        }.execute();

    }

    private void setAdapter() {

        MyCreationsDesignsAdapter rcAdapter = new MyCreationsDesignsAdapter(getActivity(), myCreationsList, monDesignClickListner);
        RV_MyCreations.setAdapter(rcAdapter);
        rcAdapter.notifyDataSetChanged();

        //Working with whole list
        RV_MyCreations.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), RV_MyCreations, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    private void ParseData(String response) {

        try {

            if (response != null) {
                JSONObject jsnon = new JSONObject(response);
                String response1 = jsnon.getString("response");
                JSONObject jsonObject = new JSONObject(response1);

                if (jsonObject != null && jsonObject.has("code")) {

                    String status = jsonObject.getString("code");
                    if (status != null && status.equalsIgnoreCase("200")) {

                        String data = jsonObject.getString("data");
                        JSONArray jsonArray = new JSONArray(data);

                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject designs_json = jsonArray.getJSONObject(i);

                                MyCreations myCreations = new MyCreations();
                                if (designs_json != null) {

                                    if (designs_json.has("design_id") && designs_json.getString("design_id") != null) {

                                        myCreations.setDesignId(designs_json.getString("design_id").toString());
                                    } else {
                                        myCreations.setDesignId("");
                                    }
                                    if (designs_json.has("device_type") && designs_json.getString("device_type") != null) {

                                        myCreations.setDevice_type(designs_json.getString("device_type").toString());
                                    } else {
                                        myCreations.setDevice_type("");
                                    }
                                    if (designs_json.has("id") && designs_json.getString("id") != null) {

                                        myCreations.setId(designs_json.getString("id").toString());
                                    } else {
                                        myCreations.setId("");
                                    }
                                    if (designs_json.has("product_id") && designs_json.getString("product_id") != null) {

                                        myCreations.setProductId(designs_json.getString("product_id").toString());
                                    } else {
                                        myCreations.setProductId("");
                                    }
                                    if (designs_json.has("is_uploaded_design") && designs_json.getString("is_uploaded_design") != null) {

                                        if(designs_json.getString("is_uploaded_design").toString().equalsIgnoreCase("true")){
                                            myCreations.setIsUploadedDesign("1");
                                        }else{
                                            myCreations.setIsUploadedDesign("0");
                                        }

                                    } else {
                                        myCreations.setIsUploadedDesign("");
                                    }
                                    if (designs_json.has("product_idx") && designs_json.getString("product_idx") != null) {

                                        myCreations.setProductIdX(designs_json.getString("product_idx").toString());
                                    } else {
                                        myCreations.setProductIdX("");
                                    }
                                    if (designs_json.has("product_image") && designs_json.getString("product_image") != null) {

                                        myCreations.setProductImage(designs_json.getString("product_image").toString());
                                        //   Log.d(TAG,"strProductImage get " + designs_json.getString("product_img").toString());
                                    } else {
                                        myCreations.setProductImage("");
                                        //  Log.d(TAG,"strProductImage get " + designs_json.getString("product_img").toString());
                                    }
                                    if (designs_json.has("product_style_id") && designs_json.getString("product_style_id") != null) {

                                        myCreations.setProductStyleId(designs_json.getString("product_style_id").toString());
                                    } else {
                                        myCreations.setProductStyleId("");
                                    }
                                    if (designs_json.has("product_view") && designs_json.getString("product_view") != null) {

                                        myCreations.setProductView(designs_json.getString("product_view").toString());
                                    } else {
                                        myCreations.setProductView("");
                                    }
                                    if (designs_json.has("modified") && designs_json.getString("modified") != null) {

                                        myCreations.setModifiedDate(designs_json.getString("modified").toString());
                                    } else {
                                        myCreations.setModifiedDate("");
                                    }
                                    if (designs_json.has("title") && designs_json.getString("title") != null) {

                                        myCreations.setTitle(designs_json.getString("title").toString());
                                    } else {
                                        myCreations.setTitle("");
                                    }

                                    if (designs_json.has("design")) {

                                        JSONObject designDetailsObj = designs_json.getJSONObject("design");
                                        if (designDetailsObj != null) {

                                            if (designDetailsObj.has("category_id") && designDetailsObj.getString("category_id") != null) {

                                                myCreations.setDesignCategoryId(designDetailsObj.getString("category_id").toString());

                                            } else {
                                                myCreations.setDesignCategoryId("");
                                            }
                                            if (designDetailsObj.has("design_img") && designDetailsObj.getString("design_img") != null) {

                                                myCreations.setDesignImage(designDetailsObj.getString("design_img").toString());

                                            } else {
                                                myCreations.setDesignImage("");
                                            }
                                            if (designDetailsObj.has("title") && designDetailsObj.getString("title") != null) {

                                                myCreations.setDesignTitle(designDetailsObj.getString("title").toString());

                                            } else {
                                                myCreations.setDesignTitle("");
                                            }
                                        }
                                    }
                                }

                                myCreationsList.add(myCreations);
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private OrderClass getParams() {
        Log.d(TAG, "ID:... " + strUserId);
        OrderClass categoryList = new OrderClass();
        categoryList.setUser_id(strUserId);

        return categoryList;
    }

    @Override
    public void onDesignClick(Bundle mBundle) {

//        boolean isEdit = mBundle.getBoolean(Constant.IS_EDIT, true);
//        String strId = mBundle.getString(Constant.STR_ID, "");
//        String strTitle = mBundle.getString(Constant.STR_TITLE, "");
//        String strProductView = mBundle.getString(Constant.STR_PRODUCT_VIEW, "");
//        String strProductId = mBundle.getString(Constant.STR_PRODUCT_ID, "");
//        String strDesignId = mBundle.getString(Constant.STR_DESIGN_ID, "");
//        String strprodutcIdX = mBundle.getString(Constant.STR_PRODUCT_ID_X, "");
//        String strProductStyleId = mBundle.getString(Constant.STR_PRODUCT_STYLE_ID, "");
//        String strUserId = mBundle.getString(Constant.STR_USER_ID, "");
//        String strDesignCategoryId = mBundle.getString(Constant.STR_DESIGN_CATEGORY_ID, "");
//        String strDesignTitle = mBundle.getString(Constant.STR_DESIGN_TITLE, "");
//        String strDesignImage = mBundle.getString(Constant.STR_DESIGN_IMAGE, "");
//        String strModifiedDate = mBundle.getString(Constant.STR_MODIFIED_DATE, "");
//
//        Intent mIntent = new Intent(getActivity(), CustomDesignActivity.class);
//        mIntent.putExtra(Constant.IS_EDIT, true);
//        mIntent.putExtra(Constant.STR_ID, strId);
//        mIntent.putExtra(Constant.STR_TITLE, strTitle);
//        mIntent.putExtra(Constant.STR_PRODUCT_VIEW, strProductView);
//        mIntent.putExtra(Constant.STR_PRODUCT_ID, strProductId);
//        mIntent.putExtra(Constant.STR_DESIGN_ID, strDesignId);
//        mIntent.putExtra(Constant.STR_PRODUCT_ID_X, strprodutcIdX);
//        mIntent.putExtra(Constant.STR_PRODUCT_STYLE_ID, strProductStyleId);
//        mIntent.putExtra(Constant.STR_USER_ID, strUserId);
//        mIntent.putExtra(Constant.STR_DESIGN_CATEGORY_ID, strDesignCategoryId);
//        mIntent.putExtra(Constant.STR_DESIGN_TITLE, strDesignTitle);
//        mIntent.putExtra(Constant.STR_DESIGN_IMAGE, strDesignImage);
//        mIntent.putExtra(Constant.STR_MODIFIED_DATE, strModifiedDate);
//
//        getActivity().startActivity(mIntent);

    }
}
