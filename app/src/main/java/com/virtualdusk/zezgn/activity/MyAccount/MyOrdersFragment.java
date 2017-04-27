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
import android.widget.Toast;

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
import com.virtualdusk.zezgn.Model.Coupon;
import com.virtualdusk.zezgn.Model.OrderClass;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.activity.Adapter.MyOrdersAdapter;
import com.virtualdusk.zezgn.activity.Adapter.PromosNotificationAdapter;
import com.virtualdusk.zezgn.activity.Home;
import com.virtualdusk.zezgn.api.GetOrderApi;
import com.virtualdusk.zezgn.api.PromoNotificationApi;

/**
 * Created by Amit Sharma on 10/14/2016.
 */
public class MyOrdersFragment extends Fragment {
    private String TAG = MyOrdersFragment.class.getSimpleName().toString();
    private PullToRefreshListView mListView;
    private Handler handler;
    private Runnable runnable;
    private TextView lblNoRecord;
//    SwipeRefreshLayout mSwipeRefreshLayout;
    int page = 1;

    private int cpage = 1, lpage = 1;
    private String user_id,mId;
    private MyOrdersAdapter adapter;
    private ArrayList<OrderClass> newDesignArrayList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        super.onCreateView(inflater, container, savedInstanceState);


        View  v = inflater.inflate(R.layout.new_design, container, false);
        user_id = Home.sharedPreferences.getString(getString(R.string.key_user_id), null);
        newDesignArrayList=new ArrayList<>();
        findRes(v);

        return v;
    }
    private void findRes(View v) {
//        mSwipeRefreshLayout = (SwipeRefreshLayout)v.findViewById(R.id.activity_main_swipe_refresh_layout);
        mListView = (PullToRefreshListView) v.findViewById(R.id.list);
        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        lblNoRecord = (TextView) v.findViewById(R.id.lblNoRecord);

        initiateObjects();

        getDesignNotificationListViaRetrofit();
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

        mListView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Toast.makeText(getActivity(), "vhjvhvvjhvh", Toast.LENGTH_SHORT).show();

            }
        });
//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(getActivity(), "vhjvhvvjhvh", Toast.LENGTH_SHORT).show();
//            }
//        });
//

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
                    JSONObject jsnon = new JSONObject(response.body().toString());
                    String response1 = jsnon.getString("response");
                    JSONObject jsonObject=new JSONObject(response1);
                    String status = jsonObject.getString("code");
                    String data = jsonObject.getString("data");

                    if (status.equals("200"))
                    {
//                        JSONObject jsonObject_Designs=new JSONObject(data);
//                        String designs=jsonObject_Designs.getString("coupons");
//
//                        JSONObject json=new JSONObject(designs);
//
//                        String design_notification=json.getString("coupon_notification");
//                        Log.e(TAG, "onResponse:design_notification: "+design_notification );
                        JSONArray jsonArray=new JSONArray(data);

                        for (int i = 0; i <jsonArray.length() ; i++) {
                            JSONObject designs_json=jsonArray.getJSONObject(i);
                            OrderClass newDesign=new OrderClass();

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
                            newDesign.setCreated(designs_json.getString("created"));
                            newDesign.setModified(designs_json.getString("modified"));
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
        if (newDesignArrayList.size()>0)
        {
            lblNoRecord.setVisibility(View.GONE);
            adapter=new MyOrdersAdapter(getActivity(),newDesignArrayList);
            mListView.setAdapter(adapter);
        }
        else
        {
            lblNoRecord.setVisibility(View.VISIBLE);
        }


    }

    private OrderClass getParams()
    {
        Log.e(TAG, "getSignInJSON:... "+mId );

        OrderClass categoryList=new OrderClass();

        categoryList.setUser_id(user_id);


        return categoryList;
    }

}
