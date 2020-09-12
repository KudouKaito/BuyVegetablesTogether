package com.example.buyvegetablestogether.recycleview;

public class Goods {
    private String nameGoods;
    private String nameShop;
    private String imagePath;
    private int price;
    private String detail;

    Goods(String nameGoods,String nameShop,String imagePath,String detail,int price) {
        this.nameGoods = nameGoods;
        this.nameShop = nameShop;
        this.price = price;
        this.imagePath = imagePath;
        this.detail = detail;
    }

    public String getNameGoods() {
        return nameGoods;
    }
    public String getNameShop() {
        return nameShop;
    }
    public String getImagePath() {
        return imagePath;
    }
    public int getPrice() {
        return price;
    }
    public String getDetail() {
        return detail;
    }
}
