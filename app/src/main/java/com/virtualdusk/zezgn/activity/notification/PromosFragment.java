package com.virtualdusk.zezgn.activity.notification;

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
import com.virtualdusk.zezgn.Model.NewDesign;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.activity.Adapter.NewDesignNotificationAdapter;
import com.virtualdusk.zezgn.activity.Adapter.PromosNotificationAdapter;
import com.virtualdusk.zezgn.activity.Home;
import com.virtualdusk.zezgn.api.DesignNotificationApi;
import com.virtualdusk.zezgn.api.PromoNotificationApi;
import com.virtualdusk.zezgn.utils.ValidationMethod;

/**
 * Created by Amit Sharma on 10/14/2016.
 */
public class PromosFragment extends Fragment {
    private String TAG = PromosFragment.class.getSimpleName().toString();
    private ListView mListView;
    private Handler handler;
    private Runnable runnable;
    private TextView lblNoRecord;
    SwipeRefreshLayout mSwipeRefreshLayout;
    int page = 1;

    private int cpage = 1, lpage = 1;
    private String user_id, mId;
    private PromosNotificationAdapter adapter;
    private ArrayList<Coupon> newDesignArrayList;
    private boolean showSwipeToRefresh = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_promo, container, false);
        user_id = Home.sharedPreferences.getString(getString(R.string.key_user_id), null);
        newDesignArrayList = new ArrayList<>();
        findRes(v);

        return v;
    }

    private void findRes(View v) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.activity_main_swipe_refresh_layout);
        mListView = (ListView) v.findViewById(R.id.list);
        lblNoRecord = (TextView) v.findViewById(R.id.lblNoRecord);

     //   mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
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
////
//        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//                try {
//
//
//                    if (cpage < lpage) {
////                        new DashBoardDataTask(getActivity()).execute();
//
//
//                    } else {
//                        upDatePull();
//                    }
//                } catch (Exception e) {
//                }
//            }
//
//            @Override
//            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//                try {
//
//                    if (cpage < lpage) {
////                        new DashBoardDataTask(getActivity()).execute();
//
//
//                    } else {
//                        upDatePull();
//                    }
//                } catch (Exception e) {
//                }
//            }
//        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                try {

                    showSwipeToRefresh = true;
                    newDesignArrayList = new ArrayList<>();
                    getDesignNotificationListViaRetrofit();

                } catch (Exception e) {
                }

            }
        });
    }
//
//    private void upDatePull() {
////        adapter_Dash.notifyDataSetChanged();
//        handler = new Handler();
//        runnable = new Runnable() {
//            public void run() {
//                mListView.onRefreshComplete();
//                if (mListView.isRefreshing()) {
//                    handler.postDelayed(this, 1000);
//                }
//            }
//        };
//        handler.postDelayed(runnable, 1000);
//
//    }

    private void refreshContent() {
        handler = new Handler();
        runnable = new Runnable() {
            public void run() {

                mSwipeRefreshLayout.setRefreshing(false);
            }
        };
        handler.postDelayed(runnable, 1000);
    }

    private void getDesignNotificationListViaRetrofit() {
        //Creating an object of our api interface
        Retrofit restAdapter = PromoNotificationApi.retrofit;
        PromoNotificationApi registerApi = restAdapter.create(PromoNotificationApi.class);

        Call<JsonElement> call = registerApi.getNewDesignNotificationViaJSON(getParams());

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.e(TAG, "onResponse: " + response.body());

                try {
                    JSONObject jsnon = new JSONObject(response.body().toString());
                    String response1 = jsnon.getString("response");
                    JSONObject jsonObject = new JSONObject(response1);
                    String status = jsonObject.getString("code");
                    String data = jsonObject.getString("data");

                    if (status.equals("200")) {
                        JSONObject jsonObject_Designs = new JSONObject(data);
                        String designs = jsonObject_Designs.getString("coupons");

                        JSONObject json = new JSONObject(designs);

                        String design_notification = json.getString("coupon_notification");
                        Log.e(TAG, "onResponse:design_notification: " + design_notification);
                        JSONArray jsonArray = new JSONArray(design_notification);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject designs_json = jsonArray.getJSONObject(i);
                            Coupon newDesign = new Coupon();

                            newDesign.setId(designs_json.getString("id"));
                            newDesign.setType(designs_json.getString("type"));
                            newDesign.setDesign_id(designs_json.getString("design_id"));
                            newDesign.setOrder_id(designs_json.getString("order_id"));
                            newDesign.setCoupon_id(designs_json.getString("coupon_id"));
                            newDesign.setIs_read(designs_json.getString("is_read"));

                            String createDate = designs_json.getString("created");
                            createDate = ValidationMethod.parseDateToSimpleFormat(createDate);
                            newDesign.setCreated(createDate);
                            //                            newDesign.setCreated(designs_json.getString("created"));
                            Log.d(TAG, "onResponse here: " + designs_json.getString("coupon"));
                            if (designs_json.getString("coupon") != null &&
                                    !designs_json.getString("coupon").equalsIgnoreCase("null")) {


                                newDesign.setCoupon(designs_json.getString("coupon"));
                                JSONObject json_D = new JSONObject(designs_json.getString("coupon"));


                                newDesign.setDesign_id(json_D.getString("id"));

                                newDesign.setCoupon_code(json_D.getString("code"));
                                newDesign.setCoupon_discount_type(json_D.getString("discount_type"));
                                newDesign.setCoupon_amount_or_percentage(json_D.getString("amount_or_percentage"));
                                newDesign.setCoupon_applied_on(json_D.getString("applied_on"));
                                newDesign.setCoupon_start_date(json_D.getString("start_date"));
                                newDesign.setCoupon_end_date(json_D.getString("end_date"));
//                            newDesign.setCoupon_created(json_D.getString("created"));

                                String couponDate = json_D.getString("created");
                                couponDate = ValidationMethod.parseDateToSimpleFormat(couponDate);
                                newDesign.setCoupon_created(couponDate);

                                newDesign.setCoupon_modified(json_D.getString("modified"));
                                newDesignArrayList.add(newDesign);
                            }



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

        if(showSwipeToRefresh){
            showSwipeToRefresh = false;
            mSwipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(),"Your data is up to date",Toast.LENGTH_SHORT).show();
        }

        if (newDesignArrayList.size() > 0) {
            lblNoRecord.setVisibility(View.GONE);
            adapter = new PromosNotificationAdapter(getActivity(), newDesignArrayList);
            mListView.setAdapter(adapter);
        } else {
            lblNoRecord.setVisibility(View.VISIBLE);
        }


    }

    private Coupon getParams() {
        Log.e(TAG, "getSignInJSON:... " + mId);

        Coupon categoryList = new Coupon();

        categoryList.setUser_id(user_id);


        return categoryList;
    }

}
