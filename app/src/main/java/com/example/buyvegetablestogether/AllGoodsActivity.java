package com.example.buyvegetablestogether;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.buyvegetablestogether.addgoods.AddGoods;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sdx.statusbar.statusbar.StatusBarUtil;

public class AllGoodsActivity extends AppCompatActivity {
    private static final String TAG = "AllGoodsActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_goods);
        StatusBarUtil.setRootViewFitsSystemWindows(this,true);
        StatusBarUtil.setStatusBarDarkTheme(this, true);
        initAllGoods();
    }

    private void initAllGoods() {
        // init var
        Toolbar toolbar = findViewById(R.id.toolbar_with_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddGoods.actionStart(AllGoodsActivity.this,1);
            }
        });
    }

    public static void actionStart(Context context) {
        Intent intent=new Intent(context,AllGoodsActivity.class);
        context.startActivity(intent);

    }
}

