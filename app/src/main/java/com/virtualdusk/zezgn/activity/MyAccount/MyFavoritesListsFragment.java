package com.virtualdusk.zezgn.activity.MyAccount;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.virtualdusk.zezgn.InterfaceClasses.DialogInterface;
import com.virtualdusk.zezgn.InterfaceClasses.FavoriteInterface;
import com.virtualdusk.zezgn.InterfaceClasses.onDesignClickListner;
import com.virtualdusk.zezgn.Model.CategoryList;
import com.virtualdusk.zezgn.Model.MyFavorites;
import com.virtualdusk.zezgn.Model.OrderClass;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Utilities;
import com.virtualdusk.zezgn.activity.Adapter.OrderListAdapter;
import com.virtualdusk.zezgn.activity.ClickListener;
import com.virtualdusk.zezgn.activity.Home;
import com.virtualdusk.zezgn.activity.Products.ProductFragment;
import com.virtualdusk.zezgn.activity.RecyclerTouchListener;
import com.virtualdusk.zezgn.api.FavoriteDesignApi;
import com.virtualdusk.zezgn.api.MyFaoritesApi;
import com.virtualdusk.zezgn.utils.ValidationMethod;

/**
 * Created by Amit Sharma on 10/14/2016.
 */
public class MyFavoritesListsFragment extends Fragment implements onDesignClickListner, FavoriteInterface, DialogInterface {
    private String TAG = MyFavoritesListsFragment.class.getSimpleName().toString();
    private Handler handler;
    private Runnable runnable;
    //    SwipeRefreshLayout mSwipeRefreshLayout;
    int page = 1;

    private int cpage = 1, lpage = 1;
    private String user_id, mId;
    private OrderListAdapter adapter;
    private ArrayList<MyFavorites> newDesignArrayList;
    private TextView TV_lblNoRecord;
    private onDesignClickListner monDesignClickListner;
    private FavoriteInterface mFavoriteInterface;
    private DialogInterface mDialogInterface;
    private int favPosition;
    private Utilities mUtilities;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.fragment_my_favorites_lists, container, false);
        user_id = Home.sharedPreferences.getString(getString(R.string.key_user_id), null);
        newDesignArrayList = new ArrayList<>();
        findRes(v);
        monDesignClickListner = this;
        mFavoriteInterface = this;
        mDialogInterface = this;
        mUtilities = new Utilities();
        return v;
    }

    private RecyclerView rView;
    private GridLayoutManager lLayout;
    private ArrayList<MyFavorites> arrDesigns;

    private void findRes(View v) {

        TV_lblNoRecord = (TextView) v.findViewById(R.id.lblNoRecord);
        lLayout = new GridLayoutManager(getActivity(), 2);
        arrDesigns = new ArrayList<>();
        rView = (RecyclerView) v.findViewById(R.id.card_recycler_view);
        rView.setHasFixedSize(true);
        rView.setLayoutManager(lLayout);

        initiateObjects();
        getFavoritesDesignListViaRetrofit();
        //TV_lblNoRecord.setVisibility(View.VISIBLE);
    }

    private void initiateObjects() {

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


    private void getFavoritesDesignListViaRetrofit() {
        //  Toast.makeText(getActivity(), "Working", Toast.LENGTH_SHORT).show();
        //Creating an object of our api interface
        arrDesigns.clear();

        Retrofit restAdapter = MyFaoritesApi.retrofit;
        MyFaoritesApi registerApi = restAdapter.create(MyFaoritesApi.class);

        Call<JsonElement> call = registerApi.getDesignListViaJSON(getParams());

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.e("Favorite", "onResponse: " + response.body());

                try {
                    mUtilities.hideProgressDialog();
                    JSONObject jsnon = new JSONObject(response.body().toString());
                    String response1 = jsnon.getString("response");
                    JSONObject jsonObject = new JSONObject(response1);
                    String status = jsonObject.getString("code");
                    String paging = jsonObject.getString("paging");

                    if (status.equals("200")) {
                        String data = jsonObject.getString("data");

                        JSONArray jsonArray = new JSONArray(data);

                        if (jsonArray.length() > 0) {

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject designs_json = jsonArray.getJSONObject(i);
                                MyFavorites myFavorites = new MyFavorites();

                                myFavorites.setId(designs_json.getString("id"));
                                myFavorites.setDesign_id(designs_json.getString("design_id"));
                                myFavorites.setUser_id(designs_json.getString("user_id"));
                                myFavorites.setCreated(designs_json.getString("created"));
                                myFavorites.setModified(designs_json.getString("modified"));
                                myFavorites.setDesign(designs_json.getString("design"));

                                JSONObject json_DESIGN = new JSONObject(designs_json.getString("design"));

                                myFavorites.setDesign_id(json_DESIGN.getString("id"));
                                myFavorites.setDesign_category_id(json_DESIGN.getString("category_id"));
                                myFavorites.setDesign_title(json_DESIGN.getString("title"));
                                myFavorites.setDesign_design_img(json_DESIGN.getString("design_img"));
                                myFavorites.setDesign_author_id(json_DESIGN.getString("author_id"));
                                myFavorites.setDesign_author_name(json_DESIGN.getString("author_name"));

                                myFavorites.setDesign_number_of_colors(json_DESIGN.getString("number_of_colors"));
                                myFavorites.setDesign_design_text(json_DESIGN.getString("design_text"));
                                String design_created = ValidationMethod.parseDateToSimpleFormat(json_DESIGN.getString("created"));

                                myFavorites.setDesign_created(design_created);
                                myFavorites.setDesign_modified(json_DESIGN.getString("modified"));
                                myFavorites.setDesign_design_category(json_DESIGN.getString("design_category"));

                                JSONObject json_category = new JSONObject(json_DESIGN.getString("design_category"));

                                myFavorites.setDesign_category_name(json_category.getString("name"));
                                myFavorites.setDesign_category_icon(json_category.getString("icon"));

                                arrDesigns.add(myFavorites);
                            }

                            setAdapter();
                        } else {
                            TV_lblNoRecord.setVisibility(View.VISIBLE);
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
                mUtilities.hideProgressDialog();
            }
        });
    }

    private void setAdapter() {
        MyFavoritesDesignsAdapter rcAdapter = new MyFavoritesDesignsAdapter(getActivity(), arrDesigns,monDesignClickListner,mFavoriteInterface);
        rView.setAdapter(rcAdapter);

        //Working with whole list
        rView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


    }

    private OrderClass getParams() {
        Log.e(TAG, "ID:... " + user_id);

        OrderClass categoryList = new OrderClass();
        categoryList.setUser_id(user_id);

        return categoryList;
    }


    @Override
    public void onDesignClick(Bundle bundle) {

        Home.ivSetting.setVisibility(View.GONE);
        Home.tvTitle.setText("PRODUCTS");

        Home.strKey = "fav";
        ProductFragment product = new ProductFragment();
        product.setArguments(bundle);
        getFragmentManager().beginTransaction().add(R.id.frame, product).addToBackStack(null).commit();

    }

    @Override
    public void onFavClick(int position, String isFav) {


        mUtilities.showDialogWithInterface(getActivity(),"Do you want to remove this product?","Remove",mDialogInterface,"","remove fav");

        favPosition = position;
    }

    private void setFavoriteDesign(int position) {

        mUtilities.showProgressDialog(getActivity(),"Removing...");
        //Creating an object of our api interface
        Retrofit restAdapter = FavoriteDesignApi.retrofit;
        FavoriteDesignApi registerApi = restAdapter.create(FavoriteDesignApi.class);

        Call<JsonElement> call = registerApi.getDesignsViaJSON(getFavParams(position));

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.e("RESPONSE:", "onResponse: " + response.body());
                mUtilities.hideProgressDialog();

                try {
                    JSONObject jsnon = new JSONObject(response.body().toString());
                    String response1 = jsnon.getString("response");
                    JSONObject jsonObject=new JSONObject(response1);
                    String status = jsonObject.getString("code");
                    String message = jsonObject.getString("message");
                    if (status.equals("200")) {
                        arrDesigns.clear();
                        setAdapter();
                        mUtilities.showProgressDialog(getActivity(),"Please wait...");
                        getFavoritesDesignListViaRetrofit();
                    }
                    else
                    {
                        Toast.makeText(getActivity(),message+"",Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> result, Throwable t) {
                mUtilities.hideProgressDialog();
            }
        });

    }

    private CategoryList getFavParams(int position)
    {

        CategoryList categoryList=new CategoryList();

        categoryList.setUser_id(Home.sharedPreferences.getString("user_id", null));
        categoryList.setDesign_id(arrDesigns.get(position).getDesign_id());
        categoryList.setIs_fav("0");


        return categoryList;
    }

    @Override
    public void onCancelButtonClick(String DialogName, String Message) {

    }

    @Override
    public void onOkButtonClick(String DialogName, String Message) {
        setFavoriteDesign(favPosition);
    }
}
