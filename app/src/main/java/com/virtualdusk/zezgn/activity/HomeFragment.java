package com.virtualdusk.zezgn.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.virtualdusk.zezgn.InterfaceClasses.FavoriteInterface;
import com.virtualdusk.zezgn.InterfaceClasses.onDesignClickListner;
import com.virtualdusk.zezgn.Model.CategoryList;
import com.virtualdusk.zezgn.Model.DesignsList;
import com.virtualdusk.zezgn.MyApplication;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Constant;
import com.virtualdusk.zezgn.activity.CategoryScreen.CategoryAdapter;
import com.virtualdusk.zezgn.activity.CategoryScreen.DesignDetailScreen;
import com.virtualdusk.zezgn.activity.HomeScreen.DesignsAdapter;
import com.virtualdusk.zezgn.api.DesignListApi;
import com.virtualdusk.zezgn.utils.ConnectionDetector;
import com.virtualdusk.zezgn.utils.ValidationMethod;

/**
 * Created by Amit Sharma on 10/4/2016.
 */
public class HomeFragment extends Fragment implements onDesignClickListner,FavoriteInterface , SearchView.OnQueryTextListener,
        SwipeRefreshLayout.OnRefreshListener {

    private String TAG = HomeFragment.class.getSimpleName().toString();
    View v;
    Context mContext;
    private ArrayList<DesignsList> arrDesigns;
    private String user_id, mId;
    private RecyclerView rView;
    private GridLayoutManager lLayout;
    private onDesignClickListner monDesignClickListner;
    private FavoriteInterface mFavoriteInterface;
    private DesignsAdapter rcAdapter;
    private SearchView mSearchView;
    private String strTitleText = "";
    private TextView tvNoData;
    //public static boolean isCallApi = true;
    private SwipeRefreshLayout swipe_refresh;
    private boolean showSwipeToRefresh = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        v = inflater.inflate(R.layout.fragment_home, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        lLayout = new GridLayoutManager(getActivity(), 2);
        arrDesigns = new ArrayList<>();
        rView = (RecyclerView) v.findViewById(R.id.card_recycler_view);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);
        monDesignClickListner = this;
        mFavoriteInterface = this;

        boolean iss = isTablet(getActivity());

        Log.e("ISTQABLE..",""+iss);

        tvNoData = (TextView)v.findViewById(R.id.tvNoData);

        swipe_refresh = (SwipeRefreshLayout)v.findViewById(R.id.swipe_refresh);
        swipe_refresh.setOnRefreshListener(this);

        mSearchView = (SearchView) v.findViewById(R.id.searchView1);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryHint(Html.fromHtml("<font font size=\"10\" color = #A3A3A3>" + getResources().getString(R.string.hintSearchMess) + "</font>"));
        mSearchView.setIconifiedByDefault(false);
        mSearchView.clearFocus();
        user_id = Home.sharedPreferences.getString(getString(R.string.key_user_id), null);

        try {
            if( getArguments() != null){
                mId = getArguments().getString("id");
                strTitleText = getArguments().getString(Constant.TITLE_TEXT);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        mContext = getActivity();
//        ((Home) mContext).initMenuBar();

        return v;

    }
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    private void getDesignListViaRetrofit() {
        //Creating an object of our api interface
        Retrofit restAdapter = DesignListApi.retrofit;
        DesignListApi registerApi = restAdapter.create(DesignListApi.class);

        Call<JsonElement> call = registerApi.getDesignsViaJSON(getSignInJSON());

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.e(TAG, "onResponse: " + response.body());

                try {
                    Home.ivSetting.setVisibility(View.GONE);
                    JSONObject jsnon = new JSONObject(response.body().toString());
                    String response1 = jsnon.getString("response");
                    JSONObject jsonObject = new JSONObject(response1);
                    String status = jsonObject.getString("code");
                    if (status.equals("200")) {
                        JSONArray countrylist = jsonObject.getJSONArray("data");
                        if(countrylist.length() > 0)
                        {
                            for (int i = 0; i < countrylist.length(); i++) {
                                JSONObject data = countrylist.getJSONObject(i);
                                DesignsList designsList = new DesignsList();
                                designsList.setId(data.getString("id"));
                                designsList.setTitle(data.getString("title"));
                                designsList.setDesign_img(data.getString("design_img"));
                                designsList.setAuthor_name(data.getString("author_name"));
                                designsList.setNumber_of_colors(data.getString("number_of_colors"));
                                designsList.setDesign_text(data.getString("design_text"));
                                String createdDate = ValidationMethod.parseDateToSimpleFormat(data.getString("created"));
                                designsList.setCreated(createdDate);
                                designsList.setDesign_category(data.getString("design_category"));
                                designsList.setIs_favorite(data.getString("is_favorite"));

                                JSONObject jsonObject_Design = new JSONObject(data.getString("design_category"));
                                designsList.setName(jsonObject_Design.getString("name"));
                                arrDesigns.add(designsList);

                            }

                            setAdapter();
                        }
                        else
                        {
                            tvNoData.setVisibility(View.VISIBLE);
                        }

                    }
                    else
                    {
                        tvNoData.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonElement> result, Throwable t) {
                Log.e(TAG, "onFailure: " + result.toString());
                Home.ivSetting.setVisibility(View.GONE);
            }
        });

    }

    private void setAdapter() {
        rcAdapter = new DesignsAdapter(getActivity(), arrDesigns,monDesignClickListner,mFavoriteInterface);
        rView.setAdapter(rcAdapter);

        tvNoData.setVisibility(View.GONE);
        rView.setVisibility(View.VISIBLE);

        if(showSwipeToRefresh){
            showSwipeToRefresh = false;
            swipe_refresh.setRefreshing(false);
            Toast.makeText(getActivity(),"Your data is up to date",Toast.LENGTH_SHORT).show();
        }



        //Working with whole list
        rView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Home.fab.hide();

                Log.e(TAG, "onClick: " + view.getId());
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


    }

    private CategoryList getSignInJSON() {
        Log.e(TAG, "getSignInJSON:... " + mId);
        if (mId != null) {

        } else {
            mId = "0";
        }
        CategoryList categoryList = new CategoryList();

        categoryList.setUser_id(user_id);
        categoryList.setCategory_id(mId);

        return categoryList;
    }


    @Override
    public void onDesignClick(Bundle bundle) {


        if (new ConnectionDetector(getActivity()).isConnectingToInternet()) {

            Home.tvTitle.setText("Description");
            if(Home.strKey.equalsIgnoreCase("cat")){
                Home.strKey = "cat";
            }else{
                Home.strKey = "des";
            }

            DesignDetailScreen designDetailScreen = new DesignDetailScreen();
            designDetailScreen.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.frame, designDetailScreen).addToBackStack(null).commit();

        } else {
            Toast.makeText(getActivity(),getString(R.string.no_internet),Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

       // Home.rlSearch.setVisibility(View.VISIBLE);
        if(strTitleText == null || strTitleText.isEmpty()){
            Home.tvTitle.setText("All Designs");
        }else{
            Home.tvTitle.setText(strTitleText+"");
        }
        if (new ConnectionDetector(getActivity()).isConnectingToInternet()) {
            getDesignListViaRetrofit();
        } else {
            Toast.makeText(getActivity(),getString(R.string.no_internet),Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFavClick(int position, String isFav) {

        if(arrDesigns != null && rcAdapter != null)
        {
            arrDesigns.get(position).setIs_favorite(isFav);
            rcAdapter.notifyDataSetChanged();
        }


    }

    @Override
    public boolean onQueryTextSubmit(String s) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {

        if(!query.isEmpty()){
            query = query.toString().toLowerCase();

            final  ArrayList<DesignsList> filteredList = new ArrayList<>();

            for (int i = 0; i < arrDesigns.size(); i++) {

                final String text = arrDesigns.get(i).getTitle().toLowerCase();
                final String strAuthorName = arrDesigns.get(i).getAuthor_name().toLowerCase();
                if (text.contains(query)) {

                    filteredList.add(arrDesigns.get(i));
                }else
                if(strAuthorName.contains(query)){
                    filteredList.add(arrDesigns.get(i));
                }
            }

            if(filteredList.size() > 0){
                rcAdapter = new DesignsAdapter(getActivity(), filteredList,monDesignClickListner,mFavoriteInterface);
                rView.setAdapter(rcAdapter);
                rcAdapter.notifyDataSetChanged();  // data set changed
                tvNoData.setVisibility(View.GONE);
                rView.setVisibility(View.VISIBLE);
            }else{
                tvNoData.setVisibility(View.VISIBLE);
                rView.setVisibility(View.GONE);
            }


        }else{
            MyApplication.closeSoftKeyBoard(getActivity(),tvNoData);
            tvNoData.setVisibility(View.GONE);
            rView.setVisibility(View.VISIBLE);
            rcAdapter = new DesignsAdapter(getActivity(), arrDesigns,monDesignClickListner,mFavoriteInterface);
            rView.setAdapter(rcAdapter);

            rcAdapter.notifyDataSetChanged();  // data set changed
        }
        return false;
    }

    @Override
    public void onRefresh() {
        showSwipeToRefresh = true;
        arrDesigns = new ArrayList<>();
        getDesignListViaRetrofit();
    }
}
