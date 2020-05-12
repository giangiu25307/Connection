package com.example.connection.TCP_Connection;

import com.example.connection.Controller.Database;
import com.example.connection.View.Connection;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

class WorkerRunnable implements Runnable {

    protected Socket clientSocket = null;
    private Database database;
    SimpleDateFormat sdf;
    Connection connection;
    public WorkerRunnable(Socket clientSocket, Database database, Connection connection) {
        this.connection=connection;
        this.clientSocket = clientSocket;
        this.database = database;
    }

    public void run() {
        try {
            String ip=clientSocket.getInetAddress().toString();
            InputStream input = clientSocket.getInputStream();
            DataInputStream dIn = new DataInputStream(clientSocket.getInputStream());
            int length = dIn.readInt();                    // read length of incoming message
            if (length > 0) {
                if (dIn.readInt() == 0xffd8ffe0) {
                    byte[] message = new byte[length];
                    dIn.readFully(message, 0, message.length);
                     sdf= new SimpleDateFormat("yyyy.MM.dd_HH:mm:ss");
                    String currentDateandTime = sdf.format(new Date());
                    File.createTempFile(currentDateandTime+".jpeg", null, connection.getApplicationContext().getCacheDir());
                    FileOutputStream fos=new FileOutputStream(connection.getApplicationContext().getCacheDir()+currentDateandTime+".jpeg");
                    fos.write(message);
                    fos.close();
                    database.addMsg(Paths.get(connection.getApplicationContext().getCacheDir()+currentDateandTime+".jpeg"),database.getMyInformation()[0],database.findId_user(ip));
                } else if(.equals(sendInfo£€)){
                    //vabbè faccio già il pull e continuo da me
                } else{
                    byte[] message = new byte[length];
                    dIn.readFully(message, 0, message.length); // read the message
                    database.addMsg(message.toString(),database.getMyInformation()[0],database.findId_user(ip));
                }
            }

        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }

}

