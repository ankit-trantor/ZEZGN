package com.virtualdusk.zezgn.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.SaveRecords;
import com.virtualdusk.zezgn.Utilities.Utilities;
import com.virtualdusk.zezgn.activity.ShoopingCart.ShoppingCartActivity;

import static com.virtualdusk.zezgn.activity.Home.sharedPreferences;

public class ThankyouActivity extends AbstractActivity implements View.OnClickListener {

    private Button BtnContinue, BtnMyorders,BtnGuestContinue;
    private String strUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.thank_you);

        BtnContinue = (Button)findViewById(R.id.btnContinue);
        BtnGuestContinue = (Button)findViewById(R.id.btnGuestContinue);
        BtnMyorders = (Button)findViewById(R.id.btnMyOrders);
        BtnContinue.setOnClickListener(this);
        BtnMyorders.setOnClickListener(this);
        BtnGuestContinue.setOnClickListener(this);

        sharedPreferences = this.getAppSharedPreferences();
        strUserId = sharedPreferences.getString(getString(R.string.key_user_id), "");

        if(strUserId.equalsIgnoreCase("-1")){
            BtnContinue.setVisibility(View.GONE);
            BtnMyorders.setVisibility(View.GONE);
            BtnGuestContinue.setVisibility(View.VISIBLE);
        }else{
            BtnContinue.setVisibility(View.VISIBLE);
            BtnMyorders.setVisibility(View.VISIBLE);
            BtnGuestContinue.setVisibility(View.GONE);
        }

        Home.CartCount = 0;
        SaveRecords.saveIntegerToPreference(getString(R.string.key_cartcount),0,ThankyouActivity.this);
    }

    @Override
    public void onClick(View view) {
        if(view == BtnContinue || view == BtnGuestContinue){

            Intent intent = new Intent(ThankyouActivity.this, Home.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        if(view == BtnMyorders){
            Intent intent = new Intent(ThankyouActivity.this, Home.class);
            intent.putExtra("move","order");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ThankyouActivity.this, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }
}
