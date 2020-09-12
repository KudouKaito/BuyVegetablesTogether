package com.example.buyvegetablestogether.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class GoodsDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_GOODS = "create table goods("
            + "id integer primary key autoincrement, "
            + "goods_name text, "
            + "price real, "
            + "shop_name text, "
            + "image_path text, "
            + "detail text)";


    public GoodsDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_GOODS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
