package com.ConnectionProject.connection.Model;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class FileHandle {

    public static void zipFolder(String srcFolder, String destZipFile, Context context)
            throws Exception {
        ZipOutputStream zip = null;
        FileOutputStream fileWriter = null;
        fileWriter = new FileOutputStream(context.getFilesDir().getAbsolutePath()+"/Images.zip");
        zip = new ZipOutputStream(fileWriter);
        addFolderToZip(context.getFilesDir().getAbsolutePath(), zip);
        zip.flush();
        zip.close();
    }

    private static void addFileToZip(String path, String srcFile,
                              ZipOutputStream zip) throws Exception {
        File folder = new File(srcFile);
        if (!folder.isDirectory()) {
            byte[] buf = new byte[1024];
            int len;
            FileInputStream in = new FileInputStream(srcFile);
            zip.putNextEntry(new ZipEntry(path + "/" + folder.getName()));
            while ((len = in.read(buf)) > 0) {
                zip.write(buf, 0, len);
            }
        }
    }

    private static void addFolderToZip(String srcFolder,
                                ZipOutputStream zip) throws Exception {
        File folder = new File(srcFolder);
        for (String fileName : folder.list()) {
            addFileToZip(folder.getName(), srcFolder + "/" + fileName, zip);
        }
    }

    public static void unzip(String src, String dest){

        final int BUFFER_SIZE = 4096;

        BufferedOutputStream bufferedOutputStream = null;
        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(src);
            ZipInputStream zipInputStream = new ZipInputStream(new BufferedInputStream(fileInputStream));
            ZipEntry zipEntry;

            while ((zipEntry = zipInputStream.getNextEntry()) != null){

                String zipEntryName = zipEntry.getName();

                File FileName = new File(dest);
                if (!FileName.isDirectory()) {
                    try {
                        if (FileName.mkdir()) {
                        } else {
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                File file = new File(dest+"/" +zipEntryName);

                if (file.exists()){

                } else {
                    if(zipEntry.isDirectory()){
                        file.mkdirs();
                    }else{
                        byte buffer[] = new byte[BUFFER_SIZE];
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        bufferedOutputStream = new BufferedOutputStream(fileOutputStream, BUFFER_SIZE);
                        int count;

                        while ((count = zipInputStream.read(buffer, 0, BUFFER_SIZE)) != -1) {
                            bufferedOutputStream.write(buffer, 0, count);
                        }

                        bufferedOutputStream.flush();
                        bufferedOutputStream.close();
                    }
                }
            }
            zipInputStream.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
