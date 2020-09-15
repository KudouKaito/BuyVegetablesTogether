package com.example.buyvegetablestogether;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.buyvegetablestogether.db.UserDatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

public class ChangePasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chage_password);
        initChangePassword();
    }

    private void initChangePassword() {
        TextInputEditText textInputOldPassword = findViewById(R.id.text_input_old_password);
        TextInputEditText textInputNewPassword = findViewById(R.id.text_input_new_password);
        TextInputEditText textInputNewPasswordAgain = findViewById(R.id.text_input_new_password_again);
        MaterialButton buttonChangePassword = findViewById(R.id.button_change_password);
        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (null != textInputNewPassword.getText() && null != textInputNewPasswordAgain.getText() && textInputNewPassword.getText().toString().equals(textInputNewPasswordAgain.getText().toString())) {
                    if (textInputNewPassword.getText().toString().length() >= 6 && textInputNewPassword.getText().toString().length() <= 16) {
                        if (null != textInputOldPassword.getText() && LoginActivity.currentPassword.equals(textInputOldPassword.getText().toString())) {

                            SharedPreferences.Editor editor = getSharedPreferences("current_user", MODE_PRIVATE).edit();
                            LoginActivity.currentPassword = textInputNewPasswordAgain.getText().toString();
                            editor.putString("password", textInputNewPasswordAgain.getText().toString());
                            editor.apply();
                            editor = getSharedPreferences("remember_password", MODE_PRIVATE).edit();
                            editor.putString(LoginActivity.currentUserName, LoginActivity.currentPassword);
                            editor.apply();
                            UserDatabaseHelper dbHelperUser = new UserDatabaseHelper(ChangePasswordActivity.this, "UserDatabase.db", null, 1);
                            SQLiteDatabase dbUser = dbHelperUser.getWritableDatabase();
                            ContentValues values = new ContentValues();
                            values.put("password", LoginActivity.currentPassword);
                            dbUser.update("LoginInfo", values, "user_name = ?",
                                    new String[]{LoginActivity.currentUserName});
                            Toast.makeText(ChangePasswordActivity.this, "密码修改成功!", Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Snackbar.make(view, "两次输入新密码不一样, 请检查确认之后重新输入", Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        Snackbar.make(view, "密码字符数必须在6~16之间", Snackbar.LENGTH_LONG).show();
                        Log.d("123", "onClick: hhh"+textInputNewPassword.getText().toString().length());

                    }
                } else {
                    Snackbar.make(view, "旧密码输入错误, 请检查确认之后重新输入", Snackbar.LENGTH_LONG).show();
                }

            }
        });
    }
}