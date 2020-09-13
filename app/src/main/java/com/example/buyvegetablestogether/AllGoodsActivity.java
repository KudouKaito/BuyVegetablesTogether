package com.example.buyvegetablestogether;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.buyvegetablestogether.addgoods.AddGoods;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.sdx.statusbar.statusbar.StatusBarUtil;

public class AllGoodsActivity extends AppCompatActivity {
    private static final String TAG = "AllGoodsActivity";
    private static final int REQUEST_CODE_ALL_GOODS_TO_ADD_GOODS_LOGIN = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_goods);
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
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
                LoginActivity.currentUserVerify(AllGoodsActivity.this);  // 进行先行登录帐号有效性验证
                if (LoginActivity.currentUserName.equals("")) {  // 验证没通过
                    Intent intent = new Intent(AllGoodsActivity.this, LoginActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_ALL_GOODS_TO_ADD_GOODS_LOGIN);
                } else {
                    AddGoods.actionStart(AllGoodsActivity.this, 1);
                }
            }
        });
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, AllGoodsActivity.class);
        context.startActivity(intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_ALL_GOODS_TO_ADD_GOODS_LOGIN:
                LoginActivity.currentUserVerify(this);
                if (LoginActivity.currentPassword.equals("")) {
                    Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                } else {
                    AddGoods.actionStart(AllGoodsActivity.this, 1);
                }
                break;
        }
    }
}

