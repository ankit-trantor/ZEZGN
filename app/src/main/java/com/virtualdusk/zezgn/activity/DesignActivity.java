package com.virtualdusk.zezgn.activity;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.Constant;
import com.virtualdusk.zezgn.activity.CategoryScreen.DesignDetailScreen;
import com.virtualdusk.zezgn.activity.Products.ProductFragment;
import com.virtualdusk.zezgn.activity.ShoopingCart.ShoppingCartActivity;

public class DesignActivity extends AbstractActivity implements View.OnClickListener {

    public static TextView TvTitle;
    private TextView TvCart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_design);

        TvCart = (TextView)findViewById(R.id.tvCartCount);
        TvTitle = (TextView)findViewById(R.id.tvTitle);

        ProductFragment product=new ProductFragment();
        Bundle bundle = new Bundle();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            bundle.putString("id", extras.getString("id"));
            bundle.putString("design_img", extras.getString("design_img"));
        }
        product.setArguments(bundle);
        UpdateFragments.setFragment(getSupportFragmentManager().beginTransaction(),product, R.id.framelayout_designactivity);

       // Bundle bundle = new Bundle();

//        Bundle extras = getIntent().getExtras();
//        if (extras != null) {
//
//            bundle.putString("id", extras.getString("id"));
//            bundle.putString("title", extras.getString("title"));
//            bundle.putString("design_img", extras.getString("design_img"));
//            bundle.putString("author_name", extras.getString("author_name"));
//            bundle.putString("number_of_colors", extras.getString("number_of_colors"));
//            bundle.putString("design_text", extras.getString("design_text"));
//            bundle.putString("created", extras.getString("created"));
//            bundle.putString("design_category", extras.getString("design_category"));
//            bundle.putString("is_favorite", extras.getString("is_favorite"));
//
//            DesignDetailScreen designDetailScreen = new DesignDetailScreen();
//            designDetailScreen.setArguments(bundle);
//
//            getSupportFragmentManager().beginTransaction().add(R.id.framelayout_designactivity,designDetailScreen).addToBackStack(null).commit();
//
//            //UpdateFragments.setFragment(getSupportFragmentManager().beginTransaction(), designDetailScreen, R.id.framelayout_designactivity);
//        }

    }



    @Override
    public void onBackPressed() {

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.framelayout_designactivity);
        if (f instanceof DesignDetailScreen){
            Home.fab.show();
            startActivity(new Intent(DesignActivity.this,Home.class));
            DesignActivity.this.finish();

        }else{
            super.onBackPressed();
        }




    }

    @Override
    public void onClick(View view) {


        switch (view.getId()){

            case R.id.ivBack : onBackPressed(); break;
            case R.id.ivCart : startActivity(new Intent(DesignActivity.this, ShoppingCartActivity.class)); break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Home.CartCount != 0) {

            TvCart.setText(Home.CartCount + "");
            TvCart.setVisibility(View.VISIBLE);

        } else {
            TvCart.setVisibility(View.GONE);
        }

    }

    public void setTitle(String text){

        if(TvTitle != null){
            TvTitle.setText(text+"");
        }


    }
}
