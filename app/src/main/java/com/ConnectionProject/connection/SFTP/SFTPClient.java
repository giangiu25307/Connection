package com.ConnectionProject.connection.SFTP;

import android.os.AsyncTask;

import com.ConnectionProject.connection.Controller.ConnectionController;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SFTPClient {

    public SFTPClient(String[] params){

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                //CONNECTION TO SERVER,ip = params[0] | SEND STRING @ID = params[1] | @IMAGE64 = params[2] CRIPTATO CON CHIAVE SIMMETRICA | params[3] = estensione file
                System.out.println(params[0] + " " + params[1] + " " + params[2]+ " "+ params[3]);
                try {
                    String imageString = "";
                    Socket socket = new Socket(params[0], 41000);
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    out.println(params[1] + "£€" + params[3] + "£€");
                    boolean needToReadImage = true;
                    int size = 800;
                    for (int i = 0; needToReadImage; i++) {
                        if ((i + 1) * size < params[2].length()) {
                            imageString = params[2].substring(i * size, (i + 1) * size);
                        } else {
                            imageString = params[2].substring(i * size);
                            needToReadImage = false;
                        }
                        out.println(imageString);
                    }
                    out.println("£€END");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
