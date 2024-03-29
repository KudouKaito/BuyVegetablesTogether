package com.example.buyvegetablestogether;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.buyvegetablestogether.addgoods.AddGoods;
import com.example.buyvegetablestogether.db.GoodsDatabaseHelper;
import com.example.buyvegetablestogether.recycleview.Goods;
import com.example.buyvegetablestogether.recycleview.GoodsAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sdx.statusbar.statusbar.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

public class AllGoodsActivity extends AppCompatActivity {
    private static final String TAG = "AllGoodsActivity";
    private static final int REQUEST_CODE_ALL_GOODS_TO_ADD_GOODS_LOGIN = 4;
    public static final int REQUEST_CODE_ALL_GOOD_TO_ADD_GOODS = 5;
    private List<Goods> goodsList = new ArrayList<Goods>();
    private GoodsDatabaseHelper dbHelperGoods = new GoodsDatabaseHelper(this, "GoodsDatabase.db", null, 1);;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_goods);
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        StatusBarUtil.setStatusBarDarkTheme(this, true);
        initAllGoods();

    }

    private void initAllGoods() {
        // init top search bar
        Button buttonSearch = findViewById(R.id.button_search);
        EditText editTextSearch = findViewById(R.id.edit_text_search);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchGoodsActivity.actionStart(AllGoodsActivity.this, editTextSearch.getText().toString());
            }
        });
        // init var
        Toolbar toolbar = findViewById(R.id.toolbar_with_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        FloatingActionButton fab = findViewById(R.id.fab_add_goods);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity.currentUserVerify(AllGoodsActivity.this);  // 进行先行登录帐号有效性验证
                if (LoginActivity.currentUserName.equals("")) {  // 验证没通过
                    Intent intent = new Intent(AllGoodsActivity.this, LoginActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_ALL_GOODS_TO_ADD_GOODS_LOGIN);
                } else {
                    AddGoods.actionStartForResult(AllGoodsActivity.this,
                            goodsList.size() + 1, REQUEST_CODE_ALL_GOOD_TO_ADD_GOODS);
                }
            }
        });
        loadingGoods();
    }

    private void loadingGoods() {
        // 转圈圈
        findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                goodsList.clear();
                // 向列表中添加数据
                SQLiteDatabase dbGoods = dbHelperGoods.getWritableDatabase();

                Cursor cursor = dbGoods.query("goods", new String[]{"id","goods_name", "price", "shop_name", "has_image"}, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    do {
                        goodsList.add(new Goods(
                                AllGoodsActivity.this,
                                cursor.getInt(cursor.getColumnIndex("id")),
                                cursor.getString(cursor.getColumnIndex("goods_name")),
                                cursor.getString(cursor.getColumnIndex("shop_name")),
                                1 == cursor.getInt(cursor.getColumnIndex("has_image")),
                                cursor.getDouble(cursor.getColumnIndex("price"))
                        ));

                    } while (cursor.moveToNext());
                }
                cursor.close();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 摆放到recyclerView上
                        RecyclerView recyclerView = findViewById(R.id.recyclerview_search_goods);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(AllGoodsActivity.this);
                        recyclerView.setLayoutManager(layoutManager);
                        GoodsAdapter adapter = new GoodsAdapter(goodsList);
                        recyclerView.setAdapter(adapter);
                        findViewById(R.id.progress_bar).setVisibility(View.GONE);
                    }
                });
            }
        }).start();
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
                    AddGoods.actionStartForResult(AllGoodsActivity.this,
                            goodsList.size()+1,REQUEST_CODE_ALL_GOOD_TO_ADD_GOODS);
                }
                break;
            case REQUEST_CODE_ALL_GOOD_TO_ADD_GOODS:
                if (RESULT_OK == resultCode) {
                    initAllGoods();
                }
                break;
        }
    }
}

