//package com.example.buyvegetablestogether;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.content.ContentValues;
//import android.content.Intent;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.os.Bundle;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import com.example.buyvegetablestogether.db.GoodsDatabaseHelper;
//import com.example.buyvegetablestogether.db.UserDatabaseHelper;
//import com.example.buyvegetablestogether.recycleview.Goods;
//import com.google.android.material.textfield.TextInputEditText;
//
//public class AddCartDialog extends AppCompatActivity {
//    int id;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_cart_dialog);
//        Intent intent = getIntent();
//        id = intent.getIntExtra("id", -1);
//         //加载基本信息
//        UserDatabaseHelper dbHelperUser = new UserDatabaseHelper(this, "UserDatabase", null, 1);
//        SQLiteDatabase dbUser;
//        TextInputEditText textInputRealName = findViewById(R.id.text_input_real_name);
//        TextInputEditText textInputPhoneNumber = findViewById(R.id.text_input_phone_number);
//        TextInputEditText textInputAddress = findViewById(R.id.text_input_address);
//        dbUser = dbHelperUser.getWritableDatabase();
//        Cursor cursor = dbUser.query("BasicInfo", null, "user_name = ?", new String[]{LoginActivity.currentUserName}, null, null, null);
//        ContentValues values = new ContentValues();
//        if (0 == cursor.getCount()) {
//            values.put("user_name", LoginActivity.currentUserName);
//            values.put("real_name", "");
//            values.put("phone_number", "");
//            values.put("address", "");
//            dbUser.insert("BasicInfo", null, values);
//        } else {
//            cursor.moveToFirst();
//            String realName = cursor.getString(cursor.getColumnIndex("real_name"));
//            String phoneNumber = cursor.getString(cursor.getColumnIndex("phone_number"));
//            String address = cursor.getString(cursor.getColumnIndex("address"));
//            textInputRealName.setText(realName);
//            textInputPhoneNumber.setText(phoneNumber);
//            textInputAddress.setText(address);
//        }
//        cursor.close();
//
//        EditText editTextNumber = findViewById(R.id.text_input_number);
//        TextView textViewPrice = findViewById(R.id.text_view_price);
//        textViewPrice.setText((editTextNumber.getText().toString().equals("")?0:Integer.parseInt(editTextNumber.getText().toString()))*price);
//
//    }
//}