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
import com.virtualdusk.zezgn.Model.NewDesign;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Utilities;
import com.virtualdusk.zezgn.activity.Adapter.NewDesignNotificationAdapter;
import com.virtualdusk.zezgn.activity.Home;
import com.virtualdusk.zezgn.api.DesignNotificationApi;
import com.virtualdusk.zezgn.utils.ConnectionDetector;
import com.virtualdusk.zezgn.utils.ValidationMethod;

/**
 * Created by Amit Sharma on 10/14/2016.
 */
public class NewDesignFragment extends Fragment {
    private String TAG = NewDesignFragment.class.getSimpleName().toString();
    private ListView mListView;
    private Handler handler;
    private Runnable runnable;
    private TextView lblNoRecord;
    SwipeRefreshLayout mSwipeRefreshLayout;
    int page = 1;

    private int cpage = 1, lpage = 1;
    private String user_id, mId;
    private NewDesignNotificationAdapter adapter;
    private ArrayList<NewDesign> newDesignArrayList;
    private Utilities mUtilities;
    private boolean showSwipeToRefresh = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_new_design, container, false);
        user_id = Home.sharedPreferences.getString(getString(R.string.key_user_id), null);
        newDesignArrayList = new ArrayList<>();
        findRes(v);
        return v;
    }

    private void findRes(View v) {
        mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.activity_main_swipe_refresh_layout);
        mListView = (ListView) v.findViewById(R.id.list);
        lblNoRecord = (TextView) v.findViewById(R.id.lblNoRecord);

      //  mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
        initiateObjects();
        mUtilities = new Utilities();
        if (new ConnectionDetector(getActivity()).isConnectingToInternet()) {

            getDesignNotificationListViaRetrofit();
        } else {
            Toast.makeText(getActivity(),getString(R.string.no_internet),Toast.LENGTH_LONG).show();
        }


        lblNoRecord.setVisibility(View.VISIBLE);
       // Home.rlSearch.setVisibility(View.GONE);
    }

    private void initiateObjects() {

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == 0) {
                    mSwipeRefreshLayout.setEnabled(true);
                } else mSwipeRefreshLayout.setEnabled(false);
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        });
//        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
//            @Override
//            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
//                try {
//
//
//                    if (cpage < lpage) {
//
//                        getDesignNotificationListViaRetrofit();
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
//                       getDesignNotificationListViaRetrofit();
//
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

                    //refreshContent();
                    showSwipeToRefresh = true;
                    newDesignArrayList = new ArrayList<>();
                    getDesignNotificationListViaRetrofit();


                } catch (Exception e) {
                }

            }
        });


    }

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

        mUtilities.showProgressDialog(getActivity(),"Getting Notifications...");
        Retrofit restAdapter = DesignNotificationApi.retrofit;
        DesignNotificationApi registerApi = restAdapter.create(DesignNotificationApi.class);

        Call<JsonElement> call = registerApi.getNewDesignNotificationViaJSON(getParams());

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.e(TAG, "onResponse: " + response.body());

                try {
                    mUtilities.hideProgressDialog();
                    JSONObject jsnon = new JSONObject(response.body().toString());
                    String response1 = jsnon.getString("response");
                    JSONObject jsonObject = new JSONObject(response1);
                    String status = jsonObject.getString("code");
                    String data = jsonObject.getString("data");

                    if (status.equals("200")) {
                        JSONObject jsonObject_Designs = new JSONObject(data);
                        String designs = jsonObject_Designs.getString("designs");

                        JSONObject json = new JSONObject(designs);

                        String design_notification = json.getString("design_notification");
                        Log.e(TAG, "onResponse:design_notification: " + design_notification);
                        JSONArray jsonArray = new JSONArray(design_notification);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject designs_json = jsonArray.getJSONObject(i);
                            NewDesign newDesign = new NewDesign();
                            newDesign.setId(designs_json.getString("id"));
                            newDesign.setType(designs_json.getString("type"));
                            newDesign.setDesign_id(designs_json.getString("design_id"));
                            newDesign.setOrder_id(designs_json.getString("order_id"));
                            newDesign.setCoupon_id(designs_json.getString("coupon_id"));
                            newDesign.setIs_read(designs_json.getString("is_read"));
                            newDesign.setCreated(designs_json.getString("created"));


                            try{
                                newDesign.setDesign(designs_json.getString("design"));
                                JSONObject json_D = new JSONObject(designs_json.getString("design"));
                                newDesign.setDesign_id(json_D.getString("id"));
                                newDesign.setTitle_design(json_D.getString("title"));
                                newDesign.setDesign_img_design(json_D.getString("design_img"));
                                newDesign.setAuthor_id_design(json_D.getString("author_id"));
                                newDesign.setAuthor_name_design(json_D.getString("author_name"));
                                newDesign.setNumber_of_colors(json_D.getString("number_of_colors"));
                                newDesign.setDesign_text(json_D.getString("design_text"));

                                String createDate = json_D.getString("created");
                                createDate = ValidationMethod.parseDateToSimpleFormat(createDate);
                                newDesign.setDesign_created(createDate);

//                            newDesign.setDesign_created(json_D.getString("created"));
                                newDesign.setModified(json_D.getString("modified"));
                                newDesign.setDesign_category(json_D.getString("design_category"));
                            }catch (Exception e){
                                e.printStackTrace();
                            }

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
                mUtilities.hideProgressDialog();
            }
        });


    }

    private void setAdapter() {

        if(showSwipeToRefresh){
            showSwipeToRefresh = false;
            mSwipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getActivity(),"Your data is up to date",Toast.LENGTH_SHORT).show();
        }

        Log.d(TAG, "onFailure: " + newDesignArrayList.size());
        if (newDesignArrayList.size() > 0) {
            lblNoRecord.setVisibility(View.GONE);
            adapter = new NewDesignNotificationAdapter(getActivity(), newDesignArrayList);
            mListView.setAdapter(adapter);
        } else {
            lblNoRecord.setVisibility(View.VISIBLE);
        }


    }

    private NewDesign getParams() {
        Log.e(TAG, "getSignInJSON:... " + mId);

        NewDesign categoryList = new NewDesign();
        categoryList.setUser_id(user_id);

        return categoryList;
    }

}
