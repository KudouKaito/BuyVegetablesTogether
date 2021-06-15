package com.example.buyvegetablestogether.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.View;

import androidx.annotation.Nullable;

public class UserDatabaseHelper extends SQLiteOpenHelper {
    public static final String CREATE_USER_LOGIN_INFO = "create table LoginInfo ("
            + "id integer primary key autoincrement, "
            + "user_name text, "
            + "password text)";
    public static final String CREATE_USER_BASIC_INFO = "create table BasicInfo ("
            + "user_name text primary key, "
            //+ "shop_name text)";
            + "real_name text, "
            + "phone_number text, "
            + "address text)";
    private View mView;
    private Context mContext;
    public UserDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_LOGIN_INFO);
        db.execSQL(CREATE_USER_BASIC_INFO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("drop table if exists UserLoginInfo");
//        onCreate(db);

    }
}
