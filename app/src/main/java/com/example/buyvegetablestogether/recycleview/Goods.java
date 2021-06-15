package com.example.buyvegetablestogether.recycleview;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.buyvegetablestogether.db.GoodsDatabaseHelper;
import com.example.buyvegetablestogether.utils.ImageProcessor;

import java.io.Serializable;

public class Goods {
    private int id;
    private Context context;
    private String nameGoods;
    private String nameShop;
    private boolean hasImage;
    private double price;
    private String detail;

    public Goods(Context context, int id, String nameGoods, String nameShop, boolean hasImage, String detail, double price) {
        this.id = id;
        this.context = context;
        this.nameGoods = nameGoods;
        this.nameShop = nameShop;
        this.price = price;
        this.hasImage = hasImage;
        this.detail = detail;
    }

    public Goods(Context context, int id, String nameGoods, String nameShop, boolean hasImage, double price) {
        this(context, id, nameGoods, nameShop, hasImage, null, price);
    }

    public int getId() {
        return id;
    }

    public String getNameGoods() {
        return nameGoods;
    }

    public String getNameShop() {
        return nameShop;
    }

    public boolean judgeHasImage() {
        return hasImage;
    }

    public double getPrice() {
        return price;
    }

    public String getDetail() {
        GoodsDatabaseHelper dbHelperGoods = new GoodsDatabaseHelper(context, "GoodsDatabase.db", null, 1);
        SQLiteDatabase dbGoods = dbHelperGoods.getWritableDatabase();
        dbGoods.query("goods", new String[]{"detail"}, "id = ?", new String[]{String.valueOf(getId())}, null, null, null);
        return detail;
    }

    public Bitmap getImage(int id, int dpW, int dpH) {
        return ImageProcessor.readImageResizeToDp(context, context.getFilesDir().getAbsolutePath() + "/" + id + ".jpg", dpW, dpH);
    }
}
