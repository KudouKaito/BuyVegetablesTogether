package com.example.buyvegetablestogether.recycleview;

public class Goods {
    private String nameGoods;
    private String nameShop;
    private boolean hasImage;
    private int price;
    private String detail;

    Goods(String nameGoods,String nameShop,boolean hasImage,String detail,int price) {
        this.nameGoods = nameGoods;
        this.nameShop = nameShop;
        this.price = price;
        this.hasImage = hasImage;
        this.detail = detail;
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
    public int getPrice() {
        return price;
    }
    public String getDetail() {
        return detail;
    }
}
