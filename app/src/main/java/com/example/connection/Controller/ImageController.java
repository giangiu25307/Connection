package com.example.connection.Controller;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;

import org.junit.runner.manipulation.Ordering;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageController {


    public String decodeImage(String base64, Context context, String userId) {
        String path = "";
        byte[] imgBytesData = android.util.Base64.decode(base64, android.util.Base64.DEFAULT);
        try {
            File file = File.createTempFile(userId, null, context.getCacheDir());
            path = file.getPath();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            bufferedOutputStream.write(imgBytesData);
            bufferedOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }

}
