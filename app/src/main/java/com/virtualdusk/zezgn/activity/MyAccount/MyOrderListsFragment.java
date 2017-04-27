package com.virtualdusk.zezgn.activity.MyAccount;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonElement;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.virtualdusk.zezgn.Model.NewDesign;
import com.virtualdusk.zezgn.Model.OrderClass;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.activity.Adapter.MyOrdersAdapter;
import com.virtualdusk.zezgn.activity.Adapter.NewDesignNotificationAdapter;
import com.virtualdusk.zezgn.activity.Adapter.OrderListAdapter;
import com.virtualdusk.zezgn.activity.Home;
import com.virtualdusk.zezgn.api.DesignNotificationApi;
import com.virtualdusk.zezgn.api.GetOrderApi;
import com.virtualdusk.zezgn.utils.ValidationMethod;

/**
 * Created by Amit Sharma on 10/14/2016.
 */
public class MyOrderListsFragment extends Fragment {
    private String TAG = MyOrderListsFragment.class.getSimpleName().toString();
    private PullToRefreshListView mListView;
    private Handler handler;
    private Runnable runnable;
    private TextView lblNoRecord;
    //    SwipeRefreshLayout mSwipeRefreshLayout;
    int page = 1;

    private int cpage = 1, lpage = 1;
    private String user_id, mId;
    private OrderListAdapter adapter;
    private ArrayList<OrderClass> newDesignArrayList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_my_orderlist, container, false);
        user_id = Home.sharedPreferences.getString(getString(R.string.key_user_id), null);
        newDesignArrayList = new ArrayList<>();
        findRes(v);


        return v;
    }

    private void findRes(View v) {
//        mSwipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.activity_main_swipe_refresh_layout);
        mListView = (PullToRefreshListView) v.findViewById(R.id.list);
        lblNoRecord = (TextView) v.findViewById(R.id.lblNoRecord);


        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        initiateObjects();

        getDesignNotificationListViaRetrofit();
        // lblNoRecord.setVisibility(View.VISIBLE);
    }

    private void initiateObjects() {

//        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//
//            }
//
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                if (firstVisibleItem == 0) {
//                    mSwipeRefreshLayout.setEnabled(true);
//                } else mSwipeRefreshLayout.setEnabled(false);
//            }
//        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Home.ivSetting.setVisibility(View.GONE);
                Home.tvTitle.setText("ORDER DETAIL");

                Home.strKey = "fav";

                Bundle bundle = new Bundle();
                bundle.putString("KEY_ORDER_ID", newDesignArrayList.get(i-1).getId());

                OrderSummaryFragment mOrderSummaryFragment = new OrderSummaryFragment();
                mOrderSummaryFragment.setArguments(bundle);

                getFragmentManager().beginTransaction().add(R.id.frame, mOrderSummaryFragment).addToBackStack(null).commit();
            }
        });
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                try {
                    if (cpage < lpage) {
                        getDesignNotificationListViaRetrofit();
//                        new DashBoardDataTask(getActivity()).execute();


                    } else {
                        upDatePull();
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                try {

                    if (cpage < lpage) {
                        getDesignNotificationListViaRetrofit();


                    } else {
                        upDatePull();
                    }
                } catch (Exception e) {
                }
            }
        });


    }

    private void upDatePull() {
//        adapter_Dash.notifyDataSetChanged();
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                mListView.onRefreshComplete();
                if (mListView.isRefreshing()) {
                    handler.postDelayed(this, 1000);
                }
            }
        };
        handler.postDelayed(runnable, 1000);

    }

//    private void refreshContent(){
//        handler = new Handler();
//        runnable = new Runnable() {
//            public void run() {
//
//                mSwipeRefreshLayout.setRefreshing(false);
//            }
//        };
//        handler.postDelayed(runnable, 1000);
//    }


    private void getDesignNotificationListViaRetrofit() {
        //Creating an object of our api interface
        Retrofit restAdapter = GetOrderApi.retrofit;
        GetOrderApi registerApi = restAdapter.create(GetOrderApi.class);

        Call<JsonElement> call = registerApi.getDataViaJSON(getParams());

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.e(TAG, "getDesignNotificationListViaRetrofit:onResponse: " + response.body());

                try {
                    Log.e("DESIGN RESPONSE..",response.body().toString());
                    JSONObject jsnon = new JSONObject(response.body().toString());
                    String response1 = jsnon.getString("response");
                    JSONObject jsonObject = new JSONObject(response1);
                    String status = jsonObject.getString("code");
                    String data = jsonObject.getString("data");

                    if (status.equals("200")) {
                        JSONArray jsonArray = new JSONArray(data);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject designs_json = jsonArray.getJSONObject(i);
                            OrderClass newDesign = new OrderClass();

                            newDesign.setId(designs_json.getString("id"));
                            newDesign.setUnique_id(designs_json.getString("unique_id"));
                            newDesign.setUser_id(designs_json.getString("user_id"));
                            newDesign.setTotal_price(designs_json.getString("total_price"));
                            newDesign.setCoupon_code(designs_json.getString("coupon_code"));
                            newDesign.setCoupon_price(designs_json.getString("coupon_price"));
                            newDesign.setShipping_address(designs_json.getString("shipping_address"));
                            newDesign.setBilling_address(designs_json.getString("billing_address"));
                            newDesign.setPayment_method(designs_json.getString("payment_method"));
                            newDesign.setStatus(designs_json.getString("status"));
                            newDesign.setEst_delivery_date(designs_json.getString("est_delivery_date"));
                            newDesign.setShipping_details(designs_json.getString("shipping_details"));
                            newDesign.setShipping_price(designs_json.getString("shipping_price"));


                            String dateCreated = ValidationMethod.parseDateToSimpleFormat(designs_json.getString("created"));
                            String dateModified = ValidationMethod.parseDateToSimpleFormat(designs_json.getString("modified"));

                            newDesign.setModified(dateModified);
                            newDesign.setCreated(dateCreated);
//                            newDesign.setCreated(designs_json.getString("created"));
//                            newDesign.setModified(designs_json.getString("modified"));
                            newDesign.setDeleted(designs_json.getString("deleted"));


                            newDesignArrayList.add(newDesign);
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
            }
        });


    }

    private void setAdapter() {
        if (newDesignArrayList.size() > 0) {
            lblNoRecord.setVisibility(View.GONE);
            adapter = new OrderListAdapter(getActivity(), newDesignArrayList);
            mListView.setAdapter(adapter);
        } else {
            lblNoRecord.setVisibility(View.VISIBLE);
        }
    }

    private OrderClass getParams() {
        Log.e(TAG, "getSignInJSON:... " + mId);

        OrderClass categoryList = new OrderClass();
        categoryList.setUser_id(user_id);
        return categoryList;
    }


}
