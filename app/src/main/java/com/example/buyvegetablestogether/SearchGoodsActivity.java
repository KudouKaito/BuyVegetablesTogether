package com.example.buyvegetablestogether;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.buyvegetablestogether.db.GoodsDatabaseHelper;
import com.example.buyvegetablestogether.recycleview.Goods;
import com.example.buyvegetablestogether.recycleview.GoodsAdapter;
import com.sdx.statusbar.statusbar.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchGoodsActivity extends AppCompatActivity {
    private List<Goods> goodsList = new ArrayList<Goods>();
    private EditText editTextSearch;
    private static final String TAG = "SearchGoodsActivity";
    private GoodsDatabaseHelper dbHelperGoods = new GoodsDatabaseHelper(this, "GoodsDatabase.db", null, 1);;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_goods);
        StatusBarUtil.setRootViewFitsSystemWindows(this,true);
        StatusBarUtil.setStatusBarDarkTheme(this, true);
        initSearch();
    }

    private void initSearch() {
        editTextSearch = findViewById(R.id.edit_text_search);
        Toolbar toolbar = findViewById(R.id.toolbar_with_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // TODO: 添加购物车事件
        // 顶部搜索框
        Button buttonSearch = findViewById(R.id.button_search);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingGoods(editTextSearch.getText().toString());
//                SearchGoodsActivity.actionStart(SearchGoodsActivity.this, editTextSearch.getText().toString());
            }
        });
        Intent intent = getIntent();
        loadingGoods(intent.getStringExtra("search_content"));
    }

    private void loadingGoods(String searchContent) {
        findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            @Override
            public void run() {
                goodsList.clear();
                // 向列表中添加数据
                SQLiteDatabase dbGoods = dbHelperGoods.getWritableDatabase();
                editTextSearch.setText(searchContent);
                String sql  = "select * from goods " +
                        "where goods_name like ? or shop_name like ? " +
                        "or detail like ?";
                String [] selectionArgs  = new String[]{"%" + searchContent + "%",
                        "%" + searchContent + "%",
                        "%" + searchContent + "%"};
                Cursor cursor = dbGoods.rawQuery(sql, selectionArgs);
                if (cursor.moveToFirst()) {
                    do {
                        Log.d(TAG, "run: id: " + cursor.getInt(cursor.getColumnIndex("id")));
                        goodsList.add(new Goods(
                                SearchGoodsActivity.this,
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
                        LinearLayoutManager layoutManager = new LinearLayoutManager(SearchGoodsActivity.this);
                        recyclerView.setLayoutManager(layoutManager);
                        GoodsAdapter adapter = new GoodsAdapter(goodsList);
                        recyclerView.setAdapter(adapter);
                        findViewById(R.id.progress_bar).setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }

    public static void actionStart(Context context, String searchContent) {
        Intent intent = new Intent(context, SearchGoodsActivity.class);
        intent.putExtra("search_content", searchContent);
        context.startActivity(intent);
    }

}
