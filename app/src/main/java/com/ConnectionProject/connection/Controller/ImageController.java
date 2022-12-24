package com.ConnectionProject.connection.Controller;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class ImageController {

    public static String myImagePathToCopy;

    public static String decodeImage(String base64, Context context, String userId, String ex) {
        //ToDO /data/user/0/com.ConnectionProject.connection/files/DIRECT-CONNECTION0.jpg
        //TODO /data/user/0/com.ConnectionProject.connection/files/DIRECT-CONNECTION0
        String path = "";
        byte[] imgBytesData = android.util.Base64.decode(base64, android.util.Base64.DEFAULT);
        try {
            File file = new File(context.getFilesDir().getAbsolutePath(),  "/DIRECT-CONNECTION"+userId+ "." +ex);
            path = file.getPath();
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
            bufferedOutputStream.write(imgBytesData);
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "";
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(path);
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

}
