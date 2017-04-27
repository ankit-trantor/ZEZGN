package com.virtualdusk.zezgn.activity.CategoryScreen;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.activity.Home;
import com.virtualdusk.zezgn.activity.HomeFragment;
import com.virtualdusk.zezgn.activity.UpdateFragments;

/**
 * Created by Amit Sharma on 10/5/2016.
 */
public class RecyclerViewCategoryHolders extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView tvDesignName,tvDesignNameBy;
    public ImageView ivDesignPic;
    public RelativeLayout rlParentLyt;

    public RecyclerViewCategoryHolders(View itemView) {
        super(itemView);
        tvDesignName = (TextView)itemView.findViewById(R.id.tvDesignName);
        tvDesignNameBy = (TextView)itemView.findViewById(R.id.tvDesignNameBy);
        ivDesignPic = (ImageView)itemView.findViewById(R.id.ivDesignPic);
        rlParentLyt = (RelativeLayout) itemView.findViewById(R.id.rlParentLyt);
    }

    @Override
    public void onClick(View view) {
//        Toast.makeText(view.getContext(), "Clicked tvDesignName Position = " + getPosition(), Toast.LENGTH_SHORT).show();

                DesignDetailScreen homeFragment=new DesignDetailScreen();

//        UpdateFragments.setFragment(view.getContext().getSupportFragmentManager().beginTransaction(),new DesignDetailScreen(), R.id.frame);

//        ((Home) view.getContext()).setFragment(homeFragment);

//        FragmentTransaction transaction =  ((Home)view.getContext()).getFragmentManager().beginTransaction();
//
//        FragmentTransaction fragmentTransaction=view.getContext().getSupportFragmentManager().beginTransaction();
//        HomeFragment homeFragment=new HomeFragment();xz
//        fragmentTransaction.add(R.id.frame,homeFragment);
//        fragmentTransaction.commit();

//        switch (view.getId())
//        {
//            case R.id.tvDesignName :
//                Toast.makeText(view.getContext(), "Clicked tvDesignName Position = " + getPosition(), Toast.LENGTH_SHORT).show();
//
//                break;
//            case R.id.tvDesignNameBy:
//                Toast.makeText(view.getContext(), "Clicked tvDesignNameBy Position = " + getPosition(), Toast.LENGTH_SHORT).show();
//
//                break;
//
//            case R.id.ivCross:
//                Toast.makeText(view.getContext(), "Clicked ivCross Position = " + getPosition(), Toast.LENGTH_SHORT).show();
//
//                break;
//        }

    }



//    public void setFragment(Fragment frag){
//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        transaction.replace(R.id.frame, frag);
//    }
}
