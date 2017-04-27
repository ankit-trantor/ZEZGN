package com.virtualdusk.zezgn.activity.MyAccount;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonElement;

import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import com.virtualdusk.zezgn.InterfaceClasses.DeleteItemNo;
import com.virtualdusk.zezgn.InterfaceClasses.DialogInterface;
import com.virtualdusk.zezgn.Model.CategoryList;
import com.virtualdusk.zezgn.Model.MyCreations;
import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Utilities;
import com.virtualdusk.zezgn.activity.Home;
import com.virtualdusk.zezgn.api.DeleteDesignApi;
import com.virtualdusk.zezgn.api.FavoriteDesignApi;

/**
 * Created by Amit Sharma on 10/5/2016.
 */
public class MyCreationsRecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener, DialogInterface{

    public TextView tvDesignName,tvModifiedDate;
    public ImageView ivDesignPic, ivCross;
    private ArrayList<MyCreations> arrayList;
    private Context context;
    private DeleteItemNo mDeleteItemNo;
    private int position;
    private DialogInterface mDialogInterface;
    private Utilities mUtilities;

    public MyCreationsRecyclerViewHolders(Context context,View itemView, ArrayList<MyCreations> itemList,DeleteItemNo mDeleteItemNo) {
        super(itemView);
        this.arrayList = itemList;
        this.context=context;
        this.mDeleteItemNo = mDeleteItemNo;
        mDialogInterface = this;

        mUtilities = new Utilities();

        itemView.setOnClickListener(this);
        this.mDeleteItemNo = mDeleteItemNo;
        tvDesignName = (TextView)itemView.findViewById(R.id.tvDesignName);
        tvModifiedDate = (TextView)itemView.findViewById(R.id.tvDate);
        ivCross = (ImageView)itemView.findViewById(R.id.ivCross);
        ivDesignPic = (ImageView)itemView.findViewById(R.id.ivDesignPic);
        ivDesignPic.setOnClickListener(this);
        ivCross.setOnClickListener(this);
    }
    private String mID="0";

    @Override
    public void onClick(View view) {

        switch (view.getId())
        {
            case R.id.ivCross:

                position = getPosition();
                mID = arrayList.get(getPosition()).getId();
                mUtilities.showCancelableDialog(context,"Do you want to delete this product?","Delete",mDialogInterface,"","delete");
                break;

            case R.id.ivDesignPic:



                break;

            default:

                break;
        }
    }




    private void deleteDesign() {
        //Creating an object of our api interface
        Retrofit restAdapter = DeleteDesignApi.retrofit;
        DeleteDesignApi deleteApi = restAdapter.create(DeleteDesignApi.class);

        Log.d("MyCreationsViewHolders", "mId : " + mID);

        RequestBody creationId = RequestBody.create(MediaType.parse("text/plain"), mID);
        Call<JsonElement> call = deleteApi.deleteCreationViaJSON(creationId);

        call.enqueue(new Callback<JsonElement>() {

            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                Log.e("RESPONSE:", "onResponse: " + response.body());

                try {
                    JSONObject jsnon = new JSONObject(response.body().toString());
                    String response1 = jsnon.getString("response");
                    JSONObject jsonObject=new JSONObject(response1);
                    String status = jsonObject.getString("code");
                    String message = jsonObject.getString("message");
                    if (status.equals("200")) {

                        mDeleteItemNo.deleteItemNo(position);
                    }
                    else
                    {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<JsonElement> result, Throwable t) {
                Log.e("onFailure:", "onFailure: ");
            }
        });

    }

    @Override
    public void onCancelButtonClick(String DialogName, String Message) {

    }

    @Override
    public void onOkButtonClick(String DialogName, String Message) {
        deleteDesign();

    }
}
