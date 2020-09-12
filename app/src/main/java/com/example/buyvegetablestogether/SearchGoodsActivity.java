package com.example.buyvegetablestogether;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.sdx.statusbar.statusbar.StatusBarUtil;

public class SearchGoodsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_goods);
        StatusBarUtil.setRootViewFitsSystemWindows(this,true);
        StatusBarUtil.setStatusBarDarkTheme(this, true);
    }
}