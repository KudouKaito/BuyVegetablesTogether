package com.example.buyvegetablestogether.utils;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;

import com.sdx.statusbar.statusbar.StatusBarUtil;

public class ImageProcessor {
    public static Bitmap readImageResize(Context context,String imagePath, int rqsW, int rqsH) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > rqsH || width > rqsW) {
            int heightRatio = Math.round((float) height / (float) rqsH);
            int widthRatio = Math.round((float) width / (float) rqsW);
            inSampleSize = Math.min(heightRatio, widthRatio);
            options.inSampleSize = inSampleSize;
            options.inJustDecodeBounds = false;
            if (inSampleSize <= 0) {
                inSampleSize = 1;
            }
        }else if (rqsW == 0 || rqsH == 0) {
            options.inSampleSize = 1;
        }
        return BitmapFactory.decodeFile(imagePath, options);
    }    
    public static Bitmap readImageResizeToDp(Context context,String imagePath, int dpWidth, int dpHeight) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        if (null != context.getDisplay()) {
            context.getDisplay().getRealMetrics(displayMetrics);
        }
        int density = Math.round(displayMetrics.density);
        int rqsW = dpWidth * density;
        int rqsH = dpHeight * density;
        return readImageResize(context,imagePath,rqsW, rqsH);
    }
    private static String getImagePath(Context context,Uri uri, String selection) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (null != cursor) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    public static String handleIntentImageToPath(Context context, Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
//        Environment.getExternalStorageDirectory();
        if (DocumentsContract.isDocumentUri(context, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            String authority = uri.getAuthority();
            if ("com.android.providers.media.documents".equals(authority)) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(context, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);

            } else if ("com.android.providers.download.documents".equals(authority)) {
                Uri contentUri = ContentUris.withAppendedId(Uri.
                        parse("content://downloads/public_downloads"), Long.parseLong(docId));
                imagePath = ImageProcessor.getImagePath(context, contentUri, null);
            } else if ("com.android.externalstorage.documents".equals(authority)) {
                docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    imagePath = Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = ImageProcessor.getImagePath(context, uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }
        return imagePath;
    }
}
