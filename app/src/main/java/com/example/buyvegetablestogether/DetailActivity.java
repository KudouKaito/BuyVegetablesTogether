package com.example.buyvegetablestogether;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.buyvegetablestogether.R;
import com.example.buyvegetablestogether.db.CartDatabaseHelper;
import com.example.buyvegetablestogether.db.GoodsDatabaseHelper;
import com.example.buyvegetablestogether.recycleview.Goods;
import com.example.buyvegetablestogether.utils.ImageProcessor;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

public class DetailActivity extends AppCompatActivity {
    private int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Intent intent = getIntent();
        id = intent.getIntExtra("id",-1);
        String nameGoods;
        String nameShop;
        String detail;
        double price;
        if (-1 != id) {
            GoodsDatabaseHelper dbHelperGoods = new GoodsDatabaseHelper(this,"GoodsDatabase.db",null,1);
            SQLiteDatabase dbGoods = dbHelperGoods.getWritableDatabase();

            Cursor cursor = dbGoods.query("goods", null, "id = ?", new String[]{String.valueOf(id)}, null, null, null);
            if (cursor.moveToFirst()) {
                nameGoods = cursor.getString(cursor.getColumnIndex("goods_name"));
                nameShop = cursor.getString(cursor.getColumnIndex("shop_name"));
                detail = cursor.getString(cursor.getColumnIndex("detail"));
                price = cursor.getDouble(cursor.getColumnIndex("price"));

                ImageView imageView = findViewById(R.id.image_view_goods);
                Bitmap bitmap = ImageProcessor.readImageResizeToDp(this,
                        getFilesDir() + "/" + id + ".jpg", 300, 300);
                if (null != bitmap) {
                    imageView.setImageBitmap(bitmap);
                } else {
                    imageView.setImageResource(R.drawable.default_avatar);
                }
                TextView textViewNameGoods = findViewById(R.id.text_view_name_goods);
                TextView textViewNameShop = findViewById(R.id.text_view_name_shop);
                TextView textViewPrice = findViewById(R.id.text_view_price);
                TextView textViewDetail = findViewById(R.id.text_view_detail);
                textViewNameGoods.setText(nameGoods);
                textViewPrice.setText(String.valueOf(price));
                textViewNameShop.setText(nameShop);
                textViewDetail.setText(detail);
            }
        } else {
            Toast.makeText(this, "加载失败", Toast.LENGTH_SHORT).show();
            finish();
        }

        // 购物车按钮
        FloatingActionButton fabAddToCart = findViewById(R.id.fab_add_to_cart);
        fabAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intentPopAddCart = new Intent(DetailActivity.this, AddCartDialog.class);
//                intent.putExtra("id", id);
//                startActivity(intent);
                CartDatabaseHelper dbHelperCart = new CartDatabaseHelper(DetailActivity.this, "CartDatabase.db", null, 1);
                SQLiteDatabase dbCart = dbHelperCart.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("consumer", LoginActivity.currentUserName);
                values.put("goods_id", id);
                dbCart.insert("Cart", null, values);
                Snackbar.make(view, "成功加入购物车!", Snackbar.LENGTH_SHORT);
            }
        });

    }

    public static void actionStart(Context context, Goods goods) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("id",goods.getId());
        context.startActivity(intent);
    }
}