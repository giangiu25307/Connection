package com.example.connection.Controller;

import android.content.Context;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ImageController {

    public static String myImagePathToCopy;

    public static String decodeImage(String base64, Context context, String userId) {
        String path = "";
        byte[] imgBytesData = android.util.Base64.decode(base64, android.util.Base64.DEFAULT);
        try {
            File file = File.createTempFile("DIRECT-CONNECTION"+userId, null, context.getFilesDir());
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

    public static void copy(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }
    }

    public static ArrayList<Integer> check_Image(HashMap<Integer, String> hashMap) {
        Map<Integer, String> sortedMap = new TreeMap<>(hashMap);
        int i = 0;
        ArrayList<Integer> value = new ArrayList<>();
        while (i <= sortedMap.size()) {
            if (!sortedMap.containsKey(i)) {
                value.add(i);
            }
        }
        if (!value.isEmpty()) {
            return value;
        } else {
            return null;
        }
    }

    public static String storage_Image(HashMap<Integer, String> hashMap, Context context, String userId) {
        String concat = String.join("", hashMap.values());
        String path = "";
        byte[] imgBytesData = concat.getBytes();
        try {
            File file = File.createTempFile("DIRECT-CONNECTION" + userId, null, context.getFilesDir());
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
