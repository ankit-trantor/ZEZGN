package com.virtualdusk.zezgn.activity.CategoryScreen;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.virtualdusk.zezgn.Model.CategoryList;
import com.virtualdusk.zezgn.MyApplication;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Constant;
import com.virtualdusk.zezgn.Utilities.Utilities;
import com.virtualdusk.zezgn.activity.ClickListener;
import com.virtualdusk.zezgn.activity.Home;
import com.virtualdusk.zezgn.activity.HomeFragment;
import com.virtualdusk.zezgn.activity.HomeScreen.DesignsAdapter;
import com.virtualdusk.zezgn.activity.RecyclerTouchListener;
import com.virtualdusk.zezgn.api.CategoryListApi;
import com.virtualdusk.zezgn.utils.ConnectionDetector;

/**
 * Created by Amit Sharma on 10/4/2016.
 */
public class CategoryFragment extends Fragment implements SearchView.OnQueryTextListener,  SwipeRefreshLayout.OnRefreshListener {

    private String TAG = CategoryFragment.class.getSimpleName().toString();
    View v;
    Context mContext;
    private GridLayoutManager lLayout;
    private ArrayList<CategoryList> arrDesigns;
    private ArrayList<CategoryList> filteredList = new ArrayList<>();
    private SearchView mSearchView;
    private RecyclerView rView;
    private CategoryAdapter rcAdapter;
    private TextView tvNoData;
    private Utilities mUtilities;
    private SwipeRefreshLayout swipe_refresh;
    private boolean showSwipeToRefresh = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        v = inflater.inflate(R.layout.fragment_category, container, false);
        arrDesigns = new ArrayList<>();

        mUtilities = new Utilities();

        mContext = getActivity();

        swipe_refresh = (SwipeRefreshLayout)v.findViewById(R.id.swipe_refresh);
        swipe_refresh.setOnRefreshListener(this);

        mSearchView = (SearchView) v.findViewById(R.id.searchView1);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryHint(Html.fromHtml("<font font size=\"10\" color = #A3A3A3>" + getResources().getString(R.string.hintSearchMess) + "</font>"));

        mSearchView.setIconifiedByDefault(false);
        mSearchView.clearFocus();

        tvNoData = (TextView) v.findViewById(R.id.tvNoData);


        if (new ConnectionDetector(getActivity()).isConnectingToInternet()) {
            getCategoryListViaRetrofit();
        } else {
            Toast.makeText(getActivity(), getString(R.string.no_internet), Toast.LENGTH_LONG).show();
        }
        return v;
    }

    private void getCategoryListViaRetrofit() {
        //Creating an object of our api interface
        arrDesigns = new ArrayList<>();
        Retrofit restAdapter = CategoryListApi.retrofit;
        CategoryListApi registerApi = restAdapter.create(CategoryListApi.class);

        Call<JsonElement> call = registerApi.getDesignsViaJSON();

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
                        for (int i = 0; i < countrylist.length(); i++) {
                            JSONObject data = countrylist.getJSONObject(i);
                            CategoryList designsList = new CategoryList();
                            JSONArray jsonArray = new JSONArray(data.getString("designs"));


                            if(jsonArray.length() > 0)
                            {
                                for (int j = 0; j < jsonArray.length(); j++) {
                                    JSONObject jsonObject1 = jsonArray.getJSONObject(j);

                                    int totalCounts = Integer.valueOf(jsonObject1.getString("total"));

                                    if (totalCounts > 0) {

                                        designsList.setId(data.getString("id"));
                                        designsList.setName(data.getString("name"));
                                        designsList.setIcon(data.getString("icon"));
                                        designsList.setStatus(data.getString("status"));
                                        designsList.setCreated(data.getString("created"));
                                        designsList.setModified(data.getString("modified"));
                                        designsList.setDesigns(data.getString("designs"));

                                        designsList.setTotal(jsonObject1.getString("total"));
                                        designsList.setCategory_id(jsonObject1.getString("category_id"));
                                        arrDesigns.add(designsList);

                                    } else {

                                    }
                                }
                            }
                            else
                            {
                                designsList.setId(data.getString("id"));
                                designsList.setName(data.getString("name"));
                                designsList.setIcon(data.getString("icon"));
                                designsList.setStatus(data.getString("status"));
                                designsList.setCreated(data.getString("created"));
                                designsList.setModified(data.getString("modified"));
                                designsList.setDesigns(data.getString("designs"));
                                arrDesigns.add(designsList);
                            }

                        }
                    }
                    setAdapter();
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
            swipe_refresh.setRefreshing(false);
            Toast.makeText(getActivity(),"Your data is up to date",Toast.LENGTH_SHORT).show();
        }

        rView = (RecyclerView) v.findViewById(R.id.card_recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rView.setLayoutManager(mLayoutManager);

        rcAdapter = new CategoryAdapter(getActivity(), arrDesigns);
        rView.setAdapter(rcAdapter);

        tvNoData.setVisibility(View.GONE);
        rView.setVisibility(View.VISIBLE);

        rView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                if (filteredList.size() > 0) {

                    bundle.putString("id", filteredList.get(position).getId());
                    bundle.putString(Constant.TITLE_TEXT, filteredList.get(position).getName());
                } else {

                    bundle.putString("id", arrDesigns.get(position).getId());
                    bundle.putString(Constant.TITLE_TEXT, arrDesigns.get(position).getName());
                }

                ((Home) mContext).setTitle("All DESIGNS");
                Log.d(TAG, "onClick: cat fragment " + arrDesigns.get(position).getName());

                HomeFragment designDetailScreen = new HomeFragment();
                designDetailScreen.setArguments(bundle);
                Home.strKey = "cat";
                getFragmentManager().beginTransaction().add(R.id.frame, designDetailScreen).addToBackStack(null).commit();

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {

        if (!query.isEmpty()) {
            query = query.toString().toLowerCase();

            filteredList.clear();

            for (int i = 0; i < arrDesigns.size(); i++) {

                final String text = arrDesigns.get(i).getName().toLowerCase();
                if (text.contains(query)) {

                    filteredList.add(arrDesigns.get(i));
                }
            }


            if (filteredList.size() > 0) {
                rcAdapter = new CategoryAdapter(getActivity(), filteredList);
                rView.setAdapter(rcAdapter);
                rcAdapter.notifyDataSetChanged();  // data set changed

                tvNoData.setVisibility(View.GONE);
                rView.setVisibility(View.VISIBLE);
            } else {
                tvNoData.setVisibility(View.VISIBLE);
                rView.setVisibility(View.GONE);
            }


        } else {
            MyApplication.closeSoftKeyBoard(getActivity(),tvNoData);
            tvNoData.setVisibility(View.GONE);
            rView.setVisibility(View.VISIBLE);
            filteredList.clear();
            rcAdapter = new CategoryAdapter(getActivity(), arrDesigns);
            rView.setAdapter(rcAdapter);

            rcAdapter.notifyDataSetChanged();  // data set changed
        }
        return false;
    }

    @Override
    public void onRefresh() {
        showSwipeToRefresh = true;
        getCategoryListViaRetrofit();
    }
}
