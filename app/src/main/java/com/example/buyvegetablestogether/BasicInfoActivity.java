package com.example.buyvegetablestogether;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.buyvegetablestogether.addgoods.AddGoods;
import com.example.buyvegetablestogether.db.UserDatabaseHelper;
import com.example.buyvegetablestogether.utils.ImageProcessor;
import com.example.buyvegetablestogether.utils.MyFileUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class BasicInfoActivity extends AppCompatActivity {
    private Uri imageUri;
    private CircleImageView circleImageView;
    private final int TAKE_PHOTO = 101;
    private final int CHOOSE_PHOTO = 102;
    private UserDatabaseHelper dbHelperUser = new UserDatabaseHelper(BasicInfoActivity.this, "UserDatabase.db", null, 1);
    private SQLiteDatabase dbUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_info);
        // 加载基本信息
        TextInputEditText textInputRealName = findViewById(R.id.text_input_real_name);
        TextInputEditText textInputPhoneNumber = findViewById(R.id.text_input_phone_number);
        TextInputEditText textInputAddress = findViewById(R.id.text_input_address);
        dbUser = dbHelperUser.getWritableDatabase();
        Cursor cursor = dbUser.query("BasicInfo", null, "user_name = ?", new String[]{LoginActivity.currentUserName}, null, null, null);
        ContentValues values = new ContentValues();
        if (0 == cursor.getCount()) {
            values.put("user_name", LoginActivity.currentUserName);
            values.put("real_name", "");
            values.put("phone_number", "");
            values.put("address", "");
            dbUser.insert("BasicInfo", null, values);
        } else {
            cursor.moveToFirst();
            String realName = cursor.getString(cursor.getColumnIndex("real_name"));
            String phoneNumber = cursor.getString(cursor.getColumnIndex("phone_number"));
            String address = cursor.getString(cursor.getColumnIndex("address"));
            textInputRealName.setText(realName);
            textInputPhoneNumber.setText(phoneNumber);
            textInputAddress.setText(address);
        }

        // TODO: 加载头像
        circleImageView = findViewById(R.id.circle_image_view_photo);
        Bitmap photo = ImageProcessor.readImageResizeToDp(this, getFilesDir() + "/user_"
                        + LoginActivity.currentUserName + ".jpg", 112, 112);
        if (null != photo) {
            circleImageView.setImageBitmap(photo);
        } else {
            circleImageView.setImageResource(R.drawable.default_avatar);
            circleImageView.setTag(false);
        }
        // 从摄像头获取
        MaterialButton buttonGetImageFromCamera= findViewById(R.id.button_get_image_from_camera);
        buttonGetImageFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File outputImage = new File(getFilesDir(), "user_"+LoginActivity.currentUserName + ".jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(BasicInfoActivity.this, "com.example.buyvegetablestogether.fileprovider", outputImage);
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                if (null != intentCamera.resolveActivity(getPackageManager())) {
                    startActivityForResult(intentCamera, TAKE_PHOTO);
                }
            }
        });

        // 相册获取头像
        MaterialButton buttonChooseImageFromAlbum = findViewById(R.id.button_choose_image_from_album);
        buttonChooseImageFromAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(BasicInfoActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.
                        PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(BasicInfoActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
            }
        });
        // 修改按钮被点击
        MaterialButton buttonChange = findViewById(R.id.button_change);
        buttonChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values = new ContentValues();
                values.put("real_name", textInputRealName.getText().toString());
                values.put("phone_number", textInputPhoneNumber.getText().toString());
                values.put("address", textInputAddress.getText().toString());
                dbUser.update("BasicInfo", values,"user_name = ?", new String[]{LoginActivity.currentUserName});
                cursor.close();
                view.setTag(true);
                Toast.makeText(BasicInfoActivity.this, "修改成功!", Toast.LENGTH_SHORT);
                finish();
            }
        });


    }
    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Snackbar.make(circleImageView, "权限申请被拒绝,无法打开相册", Snackbar.LENGTH_LONG).show();
                }
                break;
            default:
                break;

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (RESULT_OK == resultCode) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        circleImageView.setImageBitmap(bitmap);
                        circleImageView.setTag(true);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (RESULT_OK == resultCode) {
                    String imagePath = ImageProcessor.handleIntentImageToPath(this, data);
                    MyFileUtils.copyFileForce(imagePath, getFilesDir() + "/user_" + LoginActivity.currentUserName + ".jpg");
                    circleImageView.setImageBitmap(ImageProcessor.readImageResize(BasicInfoActivity.this, imagePath, 112, 112));
                    circleImageView.setTag(true);
                }
                break;
            default:
        }
    }

    @Override
    protected void onDestroy() {
//        if (circleImageView.getTag().equals(true)) {
//             如果没有提交, 那么删除那张图片
//            if (!findViewById(R.id.button_change).getTag().equals(true)) {
//                File file = new File(getFilesDir(), "user_" + LoginActivity.currentUserName + ".jpg");
//                if (file.exists()) {
//                    file.delete();
//                }
//            }
//        }
        super.onDestroy();
    }
}

