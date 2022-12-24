package com.ConnectionProject.connection.SFTP;

import android.content.Context;

import com.ConnectionProject.connection.Controller.ImageController;
import com.ConnectionProject.connection.Database.Database;
import com.ConnectionProject.connection.TCP_Connection.Encryption;
import com.ConnectionProject.connection.View.Connection;
import com.ConnectionProject.connection.View.MapFragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.InvalidKeyException;

public class ServerPI implements Runnable {
    private Socket clientSocket;
    private BufferedReader in;
    private PrintWriter out;
    private Database database;
    private Encryption encryption;
    private Context context;

    public ServerPI(Socket incoming, Database database, Encryption encryption, Context context) throws IOException {
        this.clientSocket = incoming;
        in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
        out = new PrintWriter(this.clientSocket.getOutputStream(), true);
        this.database = database;
        this.encryption = encryption;
        this.context = context;
    }

    private void readCommandLoop() throws IOException, InvalidKeyException {
        String line = null;
        String imageToBeDecrypted = "", imageClear = "";
        String userId = "";
        String msg = "";
        String extension = "";
        while (true) {
            msg += in.readLine();
            if(msg.endsWith("£€END"))break;
        }
        System.out.println("MESSAGGIO: "+ msg);
        userId = msg.split("£€")[0];
        extension = msg.split("£€")[1];
        imageToBeDecrypted = msg.split("£€")[2];
        imageClear = encryption.decryptAES(imageToBeDecrypted,
        encryption.convertStringToSecretKey(database.getSymmetricKey(userId)));
        if(!ImageController.decodeImage(imageClear, context, userId, extension).isEmpty()) {
            if (Connection.fragmentName.equals("MAP")) {
                MapFragment mapFragment = MapFragment.getIstance();
                mapFragment.graphicRefresh();
            }
        }

    }

    public int reply(int statusCode, String statusMessage) {
        //RESPONSE TO THE CLIENT
        return statusCode;
    }

    @Override
    public void run() {
        try {
            this.readCommandLoop();
        } catch (IOException | InvalidKeyException e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                    in = null;
                }
                if (out != null) {
                    out.close();
                    out = null;
                }
                if (clientSocket != null) {
                    clientSocket.close();
                    clientSocket = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}