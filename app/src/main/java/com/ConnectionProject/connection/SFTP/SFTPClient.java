package com.ConnectionProject.connection.SFTP;

import android.os.AsyncTask;

import com.ConnectionProject.connection.Controller.ConnectionController;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class SFTPClient extends AsyncTask<String, Void, Integer> {


    protected Integer doInBackground(String... params) {
        //CONNECTION TO SERVER,ip = params[0] | SEND STRING @ID = params[1] | @IMAGE64 = params[2] CRIPTATO CON CHIAVE SIMMETRICA
        System.out.println(params[0] + " " + params[1] + " " + params[2]);
        try {
            Socket socket = new Socket(params[0], 41000);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            out.println(params[1] + "£E" + params[2]);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    protected void onPostExecute(SFTPClient result) {
        System.out.println("[SFTP-CLIENT] Connection finished with result: " + result);
        //Where ftpClient is a instance variable in the main activity
    }
}
