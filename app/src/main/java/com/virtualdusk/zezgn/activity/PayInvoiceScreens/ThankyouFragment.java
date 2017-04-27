package com.virtualdusk.zezgn.activity.PayInvoiceScreens;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.Utilities.SaveRecords;
import com.virtualdusk.zezgn.activity.Home;
import com.virtualdusk.zezgn.activity.ThankyouActivity;
import com.virtualdusk.zezgn.utils.helper.AppUtils;

import static com.virtualdusk.zezgn.activity.Home.sharedPreferences;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThankyouFragment extends Fragment implements View.OnClickListener {

    private Button BtnContinue, BtnMyorders,BtnGuestContinue;
    private String strUserId;

    public ThankyouFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.thank_you, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BtnContinue = (Button)view.findViewById(R.id.btnContinue);
        BtnGuestContinue = (Button)view.findViewById(R.id.btnGuestContinue);
        BtnMyorders = (Button)view.findViewById(R.id.btnMyOrders);
        BtnContinue.setOnClickListener(this);
        BtnMyorders.setOnClickListener(this);
        BtnGuestContinue.setOnClickListener(this);

        sharedPreferences = getActivity().getSharedPreferences(AppUtils.PREF_NAME, Context.MODE_PRIVATE);
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
        SaveRecords.saveIntegerToPreference(getString(R.string.key_cartcount),0,getActivity());
        Home.RL_TopBar.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View view) {
        if(view == BtnContinue || view == BtnGuestContinue){

            Intent intent = new Intent(getActivity(), Home.class);
            intent.putExtra("move","");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        }

        if(view == BtnMyorders){
            Intent intent = new Intent(getActivity(), Home.class);
            intent.putExtra("move","order");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            getActivity().finish();
            startActivity(intent);
        }

    }


}
