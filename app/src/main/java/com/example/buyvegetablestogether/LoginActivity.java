package com.example.buyvegetablestogether;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.buyvegetablestogether.db.UserDatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.sdx.statusbar.statusbar.StatusBarUtil;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    public static String currentUserName;
    public static String currentPassword;
    private static final String TAG = "LoginActivity";
    private View mView;
    private EditText textInputUser;
    private EditText textInputPassword;
    private CheckBox checkBoxRememberPassword;
    // 读入数据库
    private SQLiteDatabase dbUser;
    boolean userExist; // 用于判断帐号是否存在
    String mUserName;
    String mPassword;
    String userName;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        StatusBarUtil.setStutatusBar(this, true, true, 1);
//        StatusBarUtil.setRootViewFitsSystemWindows(this,true);
//        StatusBarUtil.setStatusBarDarkTheme(this,true);
        init();
    }

    private void init() {
        // dim var
        final de.hdodenhof.circleimageview.CircleImageView circleImageView_login
                = findViewById(R.id.circle_image_view_login);
        final MaterialButton buttonSignInUp = findViewById(R.id.button_sign_in_up);
        final TextInputLayout textInputLayoutUser = findViewById(R.id.text_input_layout_user);
        final TextInputLayout textInputLayoutPassword = findViewById(R.id.text_input_layout_password);
        checkBoxRememberPassword = findViewById(R.id.checkbox_remember_password);
        textInputUser = findViewById(R.id.text_input_user);
        textInputPassword = findViewById(R.id.text_input_password);
        UserDatabaseHelper dbHelperUser = new UserDatabaseHelper(LoginActivity.this, "UserDatabase.db", null, 1);
        dbUser = dbHelperUser.getWritableDatabase();
        // set actions
        setCurrentUser();
        judgeButtonEnable();
        textInputLayoutUser.setCounterMaxLength(20);
        textInputLayoutPassword.setCounterEnabled(true);
        textInputLayoutPassword.setCounterMaxLength(16);
        // 用户名内容监测
        textInputUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setPasswordRemembered();
                textInputLayoutUser.setErrorEnabled(false);
                if (15 < textInputUser.length()) {
                    textInputLayoutUser.setCounterEnabled(true);
                } else {
                    textInputLayoutUser.setCounterEnabled(false);
                }
                judgeButtonEnable();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        // 密码内容监测
        textInputPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textInputLayoutPassword.setErrorEnabled(false);
                judgeButtonEnable();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        textInputPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    // 获得焦点
                    textInputLayoutPassword.setErrorEnabled(false);
                } else {
                    // 失去焦点
                    if (6 > textInputPassword.length()) {
                        textInputLayoutPassword.setErrorEnabled(true);
                        textInputLayoutPassword.setError("密码不能少于6位");
                    } else if (16 < textInputPassword.length()) {
                        textInputLayoutPassword.setErrorEnabled(true);
                        textInputLayoutPassword.setError("密码不能少于6位");
                    } else {
                        textInputLayoutPassword.setErrorEnabled(false);
                    }

                }
            }
        });
        // 登录按钮点击
        buttonSignInUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE); // 不知道怎么把progressBar放到最前面来
                mUserName = textInputUser.getText().toString();
                mPassword = textInputPassword.getText().toString();
                // 读取数据库
                Cursor cursor = dbUser.query("LoginInfo", null, null, null, null, null, null);

                if (cursor.moveToFirst()) {  // 这个判断...还有false的情况吗?
                    do {
                        // 遍历寻找帐号
                        userName = cursor.getString(cursor.getColumnIndex("user_name"));
                        if (mUserName.equals(userName)) {
                            password = cursor.getString(cursor.getColumnIndex("password"));
                            userExist = true;
                            break;
                        } else {
                            userExist = false;
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
                if (userExist) {
                    // 已注册,登录
                    if (mPassword.equals(password)) {
                        saveCurrentUser();
                        rememberPassword();
                        Toast.makeText(LoginActivity.this, "登录成功!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Snackbar.make(view, "密码错误,请重新输入", Snackbar.LENGTH_SHORT);
                    }
                } else {
                    // 未注册,询问是否注册
                    // 弹出对话框
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("该用户名不存在, 是否要进行自动注册?")
                            .setTitle("用户未注册");
                    builder.setPositiveButton("注册并登录", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // 注册
                            ContentValues values = new ContentValues();
                            values.put("user_name", mUserName);
                            values.put("password", mPassword);
                            dbUser.insert("LoginInfo", null, values);
                            values.clear();
                            saveCurrentUser();
                            rememberPassword();
                            Toast.makeText(LoginActivity.this, "注册并登录成功!", Toast.LENGTH_SHORT).show();
                            finish();
//                            Snackbar.make(textInputPassword, "注册并登录成功!",Snackbar.LENGTH_SHORT);
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Snackbar.make(findViewById(R.id.button_sign_in_up), "您已取消注册, 请核验用户名无误后重新输入", Snackbar.LENGTH_LONG).show();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                progressBar.setVisibility(View.GONE);
            }
        });

    }

    private void setCurrentUser() {
        SharedPreferences pref = getSharedPreferences("current_user", MODE_PRIVATE);
        textInputUser.setText(pref.getString("user_name",""));
        textInputPassword.setText(pref.getString("password",""));
        SharedPreferences preferences = getSharedPreferences("remember_password", MODE_PRIVATE);
        String passwordRemembered = preferences.getString(textInputUser.getText().toString(), "");
        if (null == passwordRemembered || passwordRemembered.equals("")) {
            checkBoxRememberPassword.setChecked(false);
        } else {
            checkBoxRememberPassword.setChecked(true);
        }
    }

    private void setPasswordRemembered() {
        SharedPreferences pref = getSharedPreferences("remember_password",MODE_PRIVATE);
        String passwordRemembered = pref.getString(textInputUser.getText().toString(), "");
        textInputPassword.setText(passwordRemembered);
        if (passwordRemembered != null) {
            if (passwordRemembered.equals("")) {
                checkBoxRememberPassword.setChecked(false);
            } else {
                checkBoxRememberPassword.setChecked(true);
            }
        }
    }

    private void saveCurrentUser() {
        SharedPreferences.Editor editor = getSharedPreferences("current_user", MODE_PRIVATE).edit();
        editor.putString("user_name", textInputUser.getText().toString());
        editor.putString("password", textInputPassword.getText().toString());
        editor.apply();
    }

    private void rememberPassword() {
        SharedPreferences.Editor editor = getSharedPreferences("remember_password", MODE_PRIVATE).edit();
        editor.putString(textInputUser.getText().toString(), checkBoxRememberPassword.isChecked() ? textInputPassword.getText().toString() : "");
        editor.apply();
    }


    private void judgeButtonEnable() {
        final MaterialButton buttonSignInUp = findViewById(R.id.button_sign_in_up);
        if (0 == textInputUser.length() || 20 < textInputUser.length() ||
                6 > textInputPassword.length() || 16 < textInputPassword.length()) {
            buttonSignInUp.setEnabled(false);
        } else {
            buttonSignInUp.setEnabled(true);
        }
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);

    }

    public static boolean currentUserVerify(@NonNull Context context) {
        SharedPreferences pref = context.getSharedPreferences("current_user", MODE_PRIVATE);
        String mUserName = pref.getString("user_name", "");
        String mPassword = pref.getString("password", "");
        UserDatabaseHelper userDatabaseHelper = new UserDatabaseHelper(context, "UserDatabase.db", null, 1);
        SQLiteDatabase userDatabase = userDatabaseHelper.getWritableDatabase();
        Cursor cursor = userDatabase.query("LoginInfo", null, null, null, null, null, null);
        if (cursor.moveToFirst()) {  // 这个判断...还有false的情况吗?
            do {
                // 遍历寻找帐号
                String userName = cursor.getString(cursor.getColumnIndex("user_name"));
                if (mUserName != null) {
                    if (mUserName.equals(userName)) {
                        String password = cursor.getString(cursor.getColumnIndex("password"));
                        if (password.equals(mPassword)) {
                            currentUserName = userName;
                            currentPassword = password;
                            return true;
                        } else {
                            currentUserName = "";
                            currentPassword = "";
                            return false;
                        }
                    }
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        currentUserName = "";
        currentPassword = "";
        return false;
    }

    @Override
    protected void onDestroy() {
        currentUserVerify(LoginActivity.this);
        setResult(0);
        super.onDestroy();


    }

    public static void currentUserVerifyAndLogin(@NonNull Context context) {
        if (!currentUserVerify(context)) {
            actionStart(context);
        }
    }
}


