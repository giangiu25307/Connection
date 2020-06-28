package com.example.connection.TCP_Connection;

import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Database;
import com.example.connection.View.Connection;
import com.example.connection.localization.localizationController;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

class WorkerRunnable implements Runnable {

    protected Socket clientSocket = null;
    private Database database;
    SimpleDateFormat sdf;
    Connection connection;
    ConnectionController connectionController;
    TCP_Client tcp_client;
    localizationController localizationController;
    String idChat=null;

    public WorkerRunnable(Socket clientSocket, Database database, Connection connection,ConnectionController connectionController,localizationController localizationController) {
        this.connection = connection;
        this.clientSocket = clientSocket;
        this.database = database;
        this.connectionController = connectionController;
        this.localizationController = localizationController;
    }

    public void run() {
        try {
            String ip = clientSocket.getInetAddress().toString();
            InputStream input = clientSocket.getInputStream();
            DataInputStream dIn = new DataInputStream(clientSocket.getInputStream());
            int length = dIn.readInt();                    // read length of incoming message
            if (length > 0) {
                //reading if it's a image ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
                if (dIn.readInt() == 0xffd8ffe0) {
                    byte[] message = new byte[length];
                    dIn.readFully(message, 0, message.length);
                    sdf = new SimpleDateFormat("yyyy.MM.dd_HH:mm:ss");
                    String currentDateandTime = sdf.format(new Date());
                    File.createTempFile(currentDateandTime + ".jpeg", null, connection.getApplicationContext().getCacheDir());
                    FileOutputStream fos = new FileOutputStream(connection.getApplicationContext().getCacheDir() + currentDateandTime + ".jpeg");
                    fos.write(message);
                    fos.close();
                    idChat=database.findId_user(ip);
                    database.addMsg(connection.getApplicationContext().getCacheDir() + currentDateandTime + ".jpeg", idChat, idChat);
                } else {
                    byte[] message = new byte[length];
                    dIn.readFully(message, 0, message.length); // read the message
                    String msg=message.toString();
                    String[] splittedR =msg.split("£€");
                    if (splittedR[0].equals("sendInfo")){
                        //The group owner send all user information to the new user --------------------------------------------------------------------------------------------------------------------------------
                        String[] splitted =splittedR[1].split(",;");
                        for (int i=0;i<splitted.length;i++){
                            String[] user =splitted[i].split(",");
                            database.addUser(user[0],user[1],user[2],user[3],user[4],user[5],user[6],user[7],user[8],user[9],user[10]);
                        }
                    }else if(splittedR[0].equals("GO_LEAVES_BY")) {
                        //The group owner is leaving the group :( --------------------------------------------------------------------------------------------------------------------------------
                        database.deleteUser(database.findId_user("192.168.49.1"));
                        connectionController.disconnectToGroup();
                        connectionController.createGroup();
                    }else if(splittedR[0].equals("message")){
                        //Add the receive msg to the db --------------------------------------------------------------------------------------------------------------------------------
                        idChat=database.findId_user(ip);
                        database.addMsg(msg, idChat, idChat);
                    }else if(splittedR[0].equals("REQUEST-MEET")){
                        //bergo's stuff popup richiesta se vuoi incontrarmi return si/no
                        //database.setAccept(valore ritornato da bergo);
                        tcp_client.startConnection(clientSocket.getInetAddress().toString(),50000);
                        tcp_client.sendMessage("RESPONSE-MEET£€"+database.getMyInformation()[0]+"£€"/*+valore di bergo*/);

                    }
                    else if(splittedR[0].equals("RESPONSE-MEET")){
                        database.setAccept(splittedR[1],splittedR[2]);
                    }
                    else if(splittedR[0].equals("RESULT-MEET")){
                        if(database.getAccept(splittedR[1]).equals("yes")){
                            tcp_client.startConnection(clientSocket.getInetAddress().toString(),50000);
                            tcp_client.sendMessage("RESULT-MEET£€"+database.getMyInformation()[0]+splittedR[2]+"£€"+localizationController.gpsDirection(Double.parseDouble(splittedR[3]),Double.parseDouble(splittedR[4])));
                            //modifica gui luca

                        }
                       else{
                            tcp_client.startConnection(clientSocket.getInetAddress().toString(),50000);
                            tcp_client.sendMessage("RESPONSE-MEET£€"+database.getMyInformation()[0]+"£€"/*+valore di bergo*/);
                        }

                        
                    }
                }
            }

        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
    }

}

