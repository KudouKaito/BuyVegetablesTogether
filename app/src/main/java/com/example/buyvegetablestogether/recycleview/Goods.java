package com.example.buyvegetablestogether.recycleview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Goods {
    private int id;
    private Context context;
    private String nameGoods;
    private String nameShop;
    private boolean hasImage;
    private double price;
    private String detail;

    public Goods(Context context,int id, String nameGoods, String nameShop, boolean hasImage, String detail, double price) {
        this.id = id;
        this.context = context;
        this.nameGoods = nameGoods;
        this.nameShop = nameShop;
        this.price = price;
        this.hasImage = hasImage;
        this.detail = detail;
    }
    public Goods(Context context,int id,String nameGoods,String nameShop,boolean hasImage,double price) {
        this(context,id,nameGoods, nameShop, hasImage, null, price);
    }

    public void addDetail(String detail) {
        this.detail = detail;
    }
    public  int getId() {
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
        return detail;
    }
    public Bitmap getImage(int id) {
        return BitmapFactory.decodeFile(context.getFilesDir().getAbsolutePath() + "/" + id + ".jpg");
    }
}
