package com.example.buyvegetablestogether.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MyFileUtils {
    public static void copyFileForce(String source,String target) {
        if (null != source) {
            try {
                FileInputStream fis = new FileInputStream(source);
                byte[] buffer = new byte[1024];
                int byteRead;
                File outputImage = new File(target);
                if (outputImage.exists()) {
                    outputImage.delete();
                }
                FileOutputStream fos = new FileOutputStream(outputImage);
                while (-1 != (byteRead = fis.read(buffer))) {
                    fos.write(buffer, 0, byteRead);
                }
                fis.close();
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
