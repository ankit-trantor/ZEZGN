package com.virtualdusk.zezgn.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.virtualdusk.zezgn.R;
import com.virtualdusk.zezgn.activity.Adapter.ProductStyleAdapter;

public class DesignStyleList extends AppCompatActivity {
    private ListView list;
    ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_design_style_list);


        init();

        ProductStyleAdapter mProductStyleAdapter = new ProductStyleAdapter(DesignStyleList.this, CustomDesignActivity.productStyleList1);
        list.setAdapter(mProductStyleAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                Intent i = new Intent(DesignStyleList.this, CustomDesignActivity.class);
                i.putExtra("pos", pos);
                setResult(RESULT_OK, i);
                finish();
            }
        });
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    private void init() {

        list = (ListView)findViewById(R.id.listView);
        iv_back = (ImageView)findViewById(R.id.iv_back);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
