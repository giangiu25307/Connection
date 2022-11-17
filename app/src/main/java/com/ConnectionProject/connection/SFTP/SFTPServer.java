package com.ConnectionProject.connection.SFTP;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.ConnectionProject.connection.Database.Database;
import com.ConnectionProject.connection.Model.UtilsObject;
import com.ConnectionProject.connection.TCP_Connection.Encryption;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SFTPServer {

    public SFTPServer(UtilsObject utilsObject) {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        ServerSocket s = null;
        Socket incoming = null;
        try {
            s = new ServerSocket(41000, 0, InetAddress.getByName("::"));
            System.out.println("[FTP-SERVER] server started listening");
            while (true) {
                incoming = s.accept();
                executor.execute(new ServerPI(incoming, utilsObject.getDatabase(),
                        utilsObject.getEncryption(), utilsObject.getContext().getApplicationContext()));
            }
        } catch (Exception e) {
            System.out.println("[FTP-SERVER] ERROR: " + e);
        } finally {
            try {
                if (incoming != null) incoming.close();
            } catch (IOException ignore) {
                //ignore
            }

            try {
                if (s != null) {
                    System.out.println("[FTP-SERVER] server closed");
                    s.close();
                }
            } catch (IOException ignore) {
                //ignore
            }
        }
    }
}
