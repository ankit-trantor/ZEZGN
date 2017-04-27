package com.virtualdusk.zezgn.activity.HomeScreen;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.virtualdusk.zezgn.Model.CategoryList;
import com.virtualdusk.zezgn.Model.DesignsList;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.activity.Adapter.AllDesignsAdapter;
import com.virtualdusk.zezgn.activity.ClickListener;
import com.virtualdusk.zezgn.activity.Home;
import com.virtualdusk.zezgn.activity.RecyclerTouchListener;
import com.virtualdusk.zezgn.api.DesignListApi;
import com.virtualdusk.zezgn.utils.ValidationMethod;

/**
 * Created by Amit Sharma on 10/4/2016.
 */
public class AllDesignsFragment extends Fragment {

    private   String TAG = AllDesignsFragment.class.getSimpleName().toString();
    View v;
    Context mContext;
//    private GridLayoutManager lLayout;
private ArrayList<DesignsList> arrDesigns;


    private String user_id,mId;
    private GridView gridView;
//    private   RecyclerView rView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        v = inflater.inflate(R.layout.all_designs_screen, container, false);
      gridView=(GridView)v.findViewById(R.id.grid);

        arrDesigns=new ArrayList<>();


         user_id = Home.sharedPreferences.getString(getString(R.string.key_user_id), null);

try
{
    mId = getArguments().getString("id");

}
catch (Exception e)
{
    e.printStackTrace();
}



        mContext = getActivity();

        getDesignListViaRetrofit();
//        ((Home) mContext).initMenuBar();



        return v;


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
                    JSONObject jsnon = new JSONObject(response.body().toString());
                    String response1 = jsnon.getString("response");
                    JSONObject jsonObject=new JSONObject(response1);
                    String status = jsonObject.getString("code");
                    if (status.equals("200")) {
                        JSONArray countrylist = jsonObject.getJSONArray("data");
                        for (int i = 0; i < countrylist.length(); i++) {
                            JSONObject data = countrylist.getJSONObject(i);
                            DesignsList designsList = new DesignsList();
                            designsList.setId(data.getString("id"));
                            designsList.setTitle(data.getString("title"));
                            designsList.setDesign_img(data.getString("design_img"));
                            designsList.setAuthor_name(data.getString("author_name"));
                            designsList.setNumber_of_colors(data.getString("number_of_colors"));
                            designsList.setDesign_text(data.getString("design_text"));
                            String createdDate= ValidationMethod.parseDateToSimpleFormat(data.getString("created"));
                            designsList.setCreated(createdDate);
                            designsList.setDesign_category(data.getString("design_category"));
                            designsList.setIs_favorite(data.getString("is_favorite"));

                            JSONObject jsonObject_Design=new JSONObject(data.getString("design_category"));

                            designsList.setName(jsonObject_Design.getString("name"));


                            arrDesigns.add(designsList);


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
        AllDesignsAdapter rcAdapter = new AllDesignsAdapter(getActivity(), arrDesigns);
        gridView.setAdapter(rcAdapter);

        //Working with whole list


//        rView.addOnItemTouchListener(
//                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override public void onItemClick(View v, int position) {
//                        switch (v.getId())
//                        {
//                            case R.id.tvDesignName :
//                                Log.e(TAG, "onItemClicked: *******" );
//                                Toast.makeText(getActivity(), "Clicked tvDesignName Position = " + position, Toast.LENGTH_SHORT).show();
//
//                                break;
//                            case R.id.tvDesignNameBy:
//                                Log.e(TAG, "onItemClicked: *******" );
//
//                                Toast.makeText(getActivity(), "Clicked tvDesignNameBy Position = " + position, Toast.LENGTH_SHORT).show();
//
//                                break;
//
//                            case R.id.ivCross:
//                                Log.e(TAG, "onItemClicked: *******" );
//
//                                Toast.makeText(getActivity(), "Clicked ivCross Position = " + position, Toast.LENGTH_SHORT).show();
//
//                                break;
//                        }                    }
//                })
//        );


//        ItemClickSupport.addTo(rView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
//            @Override
//            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
//                // do it
//
//                switch (v.getId())
//                {
//                    case R.id.tvDesignName :
//                        Log.e(TAG, "onItemClicked: *******" );
//                        Toast.makeText(getActivity(), "Clicked tvDesignName Position = " + position, Toast.LENGTH_SHORT).show();
//
//                        break;
//                    case R.id.tvDesignNameBy:
//                        Log.e(TAG, "onItemClicked: *******" );
//
//                        Toast.makeText(getActivity(), "Clicked tvDesignNameBy Position = " + position, Toast.LENGTH_SHORT).show();
//
//                        break;
//
//                    case R.id.ivCross:
//                        Log.e(TAG, "onItemClicked: *******" );
//
//                        Toast.makeText(getActivity(), "Clicked ivCross Position = " + position, Toast.LENGTH_SHORT).show();
//
//                        break;
//                }
//            }
//        });

    }


    private CategoryList getSignInJSON()
    {
        Log.e(TAG, "getSignInJSON:... "+mId );
        if (mId!=null)
        {

        }
        else
        {
            mId="0";
        }
        CategoryList categoryList=new CategoryList();

        categoryList.setUser_id(user_id);
        categoryList.setCategory_id(mId);


        return categoryList;
    }



}
