package com.example.buyvegetablestogether.addgoods;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Picture;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.buyvegetablestogether.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.sdx.statusbar.statusbar.StatusBarUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class AddGoods extends AppCompatActivity {
    public static final int TAKE_PHOTO = 101;
    public static final int CHOOSE_PHOTO = 102;
    private ImageView imageView;
    private Uri imageUri;
    private static final String TAG = "AddGoods";
    private int goods_index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goods);
        StatusBarUtil.setRootViewFitsSystemWindows(this,true);
        StatusBarUtil.setStatusBarDarkTheme(this, true);

        final Intent intent = getIntent();
        goods_index = intent.getIntExtra("goods_index", 1);
        Button buttonGetImageFromCamera = findViewById(R.id.button_get_image_from_camera);
        Button buttonGetImageFromAlbum = findViewById(R.id.button_get_image_from_album);
        imageView = findViewById(R.id.image_view_goods_add);
        buttonGetImageFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File outputImage = new File(getFilesDir(),goods_index+".jpg");
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
                intent_camera.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                if(intent_camera.resolveActivity(getPackageManager())!=null){//这句作用是如果没有相机则该应用不会闪退，要是不加这句则当系统没有相机应用的时候该应用会闪退
                    startActivityForResult(intent_camera,TAKE_PHOTO);//启动相机
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
        intent.putExtra("goods_index",goods_index);
        context.startActivity(intent);
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
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    handleImage(data);
                }
                break;
            default:
                break;
        }
    }

    private void handleImage(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if (null != uri && "com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if (null != uri && "com.android.providers.download.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.
                        parse("content://downloads/public_downloads"), Long.parseLong(docId));
                imagePath = getImagePath(contentUri, null);
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {
                imagePath = getImagePath(uri, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {
                imagePath = uri.getPath();
            }

            displayImage(imagePath);
        }
    }

    private void displayImage(String imagePath) {
        if (null != imagePath) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            File outputImage = new File(getFilesDir(),goods_index+".jpg");

            try {

                if (outputImage.exists()) {
                    Log.d(TAG, "displayImage: delete"+outputImage.delete());
                }
                FileOutputStream fos = new FileOutputStream(outputImage);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,fos);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(bitmap);
        } else {
            Snackbar.make(imageView, "获取图片失败", Snackbar.LENGTH_LONG).show();
        }
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (null != cursor) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
}