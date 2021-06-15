package com.example.buyvegetablestogether.addgoods;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.buyvegetablestogether.LoginActivity;
import com.example.buyvegetablestogether.R;
import com.example.buyvegetablestogether.db.GoodsDatabaseHelper;
import com.example.buyvegetablestogether.utils.MyFileUtils;
import com.example.buyvegetablestogether.utils.ImageProcessor;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.sdx.statusbar.statusbar.StatusBarUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AddGoods extends AppCompatActivity {
    public static final int TAKE_PHOTO = 101;
    public static final int CHOOSE_PHOTO = 102;
    private ImageView imageView;
    private Uri imageUri;
    private GoodsDatabaseHelper dbHelperGoods = new GoodsDatabaseHelper(this, "GoodsDatabase.db", null, 1);;
    private static final String TAG = "AddGoods";
    private int goods_index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goods);
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        StatusBarUtil.setStatusBarDarkTheme(this, true);

        final Intent intent = getIntent();
        // 返回按钮
        Toolbar toolbar = findViewById(R.id.toolbar_with_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        goods_index = intent.getIntExtra("goods_index", 1);
        Button buttonGetImageFromCamera = findViewById(R.id.button_get_image_from_camera);
        Button buttonGetImageFromAlbum = findViewById(R.id.button_get_image_from_album);
        Button buttonPutAway = findViewById(R.id.button_put_away);
        imageView = findViewById(R.id.image_view_goods_add);
        final TextInputEditText textInputEditTextGoodsName = findViewById(R.id.text_input_goods_name);
        final TextInputEditText textInputEditTextPrice = findViewById(R.id.text_input_price);
        final TextInputEditText textInputEditTextDetail = findViewById(R.id.text_input_detail);

        buttonGetImageFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File outputImage = new File(getFilesDir(), goods_index + ".jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (Build.VERSION.SDK_INT >= 24) {
                    imageUri = FileProvider.getUriForFile(AddGoods.this, "com.example.buyvegetablestogether.fileprovider", outputImage);
                } else {
                    imageUri = Uri.fromFile(outputImage);
                }
                Intent intent_camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent_camera.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                if (intent_camera.resolveActivity(getPackageManager()) != null) {//这句作用是如果没有相机则该应用不会闪退，要是不加这句则当系统没有相机应用的时候该应用会闪退
                    startActivityForResult(intent_camera, TAKE_PHOTO);//启动相机
                }
            }
        });
        buttonGetImageFromAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(AddGoods.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.
                        PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddGoods.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    openAlbum();
                }
            }
        });
        buttonPutAway.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SQLiteDatabase dbGoods = dbHelperGoods.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("goods_name", textInputEditTextGoodsName.getText().toString());
                values.put("price", Double.valueOf(textInputEditTextPrice.getText().
                        toString().equals("") ? "0" : textInputEditTextPrice.getText().toString()));
                values.put("detail", textInputEditTextDetail.getText().toString());
                if (imageView.getTag().equals("select")) {
                    values.put("has_image", 1);
                } else {
                    values.put("has_image", 0);
                }
                values.put("shop_name", LoginActivity.currentUserName);
                if (-1 != dbGoods.insert("goods", null, values)) {
                    Toast.makeText(AddGoods.this, "上架成功!",
                            Toast.LENGTH_LONG).show();
                    Intent intentReturn = getIntent();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AddGoods.this, "上架失败, 原因未知, 请联系开发者",
                            Toast.LENGTH_LONG).show();
                }

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
                    Snackbar.make(imageView, "权限申请被拒绝,无法打开相册", Snackbar.LENGTH_LONG).show();
                }
                break;
            default:
                break;

        }
    }

    public static void actionStart(Context context, int goods_index) {
        Intent intent = new Intent(context, AddGoods.class);
        intent.putExtra("goods_index", goods_index);
        context.startActivity(intent);
    }

    public static void actionStartForResult(Context context, int goods_index, int requestCode) {
        Intent intent = new Intent(context, AddGoods.class);
        intent.putExtra("goods_index", goods_index);
        ((AppCompatActivity) context).startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        imageView.setImageBitmap(bitmap);
                        imageView.setTag("select");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                     displayImage(ImageProcessor.handleIntentImageToPath(this,data));
                }
                break;
            default:
                break;
        }
    }



    private void displayImage(String imagePath) {
        // 复制图片到数据里
        if (null != imagePath) {
            MyFileUtils.copyFileForce(imagePath,getFilesDir()+"/"+goods_index + ".jpg");
            // 读图片
            Bitmap bitmap = ImageProcessor.readImageResizeToDp(this, imagePath, 112, 112);
            imageView.setImageBitmap(bitmap);
            imageView.setTag("select");
        } else {
            Snackbar.make(imageView, "获取图片失败", Snackbar.LENGTH_LONG).show();
        }
    }

}