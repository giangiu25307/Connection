package com.example.connection.TCP_Connection;

import android.database.Cursor;
import android.widget.Switch;

import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Database;
import com.example.connection.Controller.Task;
import com.example.connection.UDP_Connection.MyNetworkInterface;
import com.example.connection.View.Connection;
import com.example.connection.localization.LocalizationController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

class WorkerRunnable implements Runnable {

    protected Socket clientSocket = null;
    private Database database;
    SimpleDateFormat sdf;
    Connection connection;
    ConnectionController connectionController;
    TCP_Client tcp_client;
    LocalizationController localizationController;
    String idChat = null;

    public WorkerRunnable(Socket clientSocket, Database database, Connection connection, ConnectionController connectionController, LocalizationController localizationController) {
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
            BufferedReader dIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
            String a = "";
            if (dIn.ready()) {
                String msg = "";
                msg = dIn.readLine();
                String[] splittedR = msg.split("£€");
                switch (splittedR[0]) {
                    case "routingMessage":
                        if(database.findIp(splittedR[1]).equals("192.168.49.1")){
                            tcp_client.changeNetworkInterface(MyNetworkInterface.getMyP2pNetworkInterface("Wlan0"));
                            tcp_client.startConnection(database.findIp(splittedR[1]),50000);
                            tcp_client.sendMessageNoKey(msg);
                        }else{
                            splittedR[0]="message";
                            msg=splittedR[0]+"£€"+splittedR[2];
                            tcp_client.startConnection(database.findIp(splittedR[1]),50000);
                            tcp_client.sendMessageNoKey(msg);
                        }
                        break;
                    case "image":
                        String message = dIn.readLine();
                        sdf = new SimpleDateFormat("yyyy.MM.dd_HH:mm:ss");
                        String currentDateandTime = sdf.format(new Date());
                        File.createTempFile(currentDateandTime + ".jpeg", null, connection.getApplicationContext().getCacheDir());
                        FileOutputStream fos = new FileOutputStream(connection.getApplicationContext().getCacheDir() + currentDateandTime + ".jpeg");
                        fos.write(message.getBytes());
                        fos.close();
                        idChat = database.findId_user(ip);
                        database.addMsg(connection.getApplicationContext().getCacheDir() + currentDateandTime + ".jpeg", idChat, idChat);
                        break;
                    case "sendInfo":
                        //The group owner send all user information to the new user --------------------------------------------------------------------------------------------------------------------------------
                        String[] splitted = splittedR[1].split(",;");

                        for (int i = 0; i < splitted.length; i++) {
                            String[] user = splitted[i].split(",");
                            database.addUser(user[0], user[1], user[2], user[3], user[4], user[5], user[6], user[7], user[8], user[9], user[10],user[11]);
                        }
                        break;
                    case "GO_LEAVES_BY":
                        //The group owner is leaving the group :( --------------------------------------------------------------------------------------------------------------------------------
                        database.deleteUser(database.findId_user("192.168.49.1"));
                        connectionController.broadcastNewGroupOwnerId();
                        connectionController.disconnectToGroup();
                        connectionController.createGroup();
                        break;
                    case "message":
                        //Add the receive msg to the db --------------------------------------------------------------------------------------------------------------------------------
                        msg = splittedR[1];
                        Date date = new Date();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                        Cursor dateDB = database.getLastMessageChat(idChat);
                        String datetime = dateDB.getString(dateDB.getColumnIndex(Task.TaskEntry.DATETIME));
                        try {
                            date = format.parse(datetime);
                            if (date.compareTo(format.parse(String.valueOf(LocalDateTime.now()))) < 0) {
                                database.addMsg("date£€" + date, idChat, idChat);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        idChat = database.findId_user(ip);
                        database.addMsg(msg, idChat, idChat);
                        break;
                    case "REQUEST-MEET":
                        //bergo's stuff popup richiesta se vuoi incontrarmi return si/no
                        //database.setAccept(valore ritornato da bergo);
                        tcp_client.startConnection(clientSocket.getInetAddress().toString(), 50000);
                        tcp_client.sendMessage("RESPONSE-MEET£€" + database.getMyInformation()[0] + "£€"/*+valore di bergo*/, "");
                        break;
                    case "RESPONSE-MEET":
                        database.setAccept(splittedR[1], splittedR[2]);
                        break;
                    case "RESULT-MEET":
                        if (database.getAccept(splittedR[1]).equals("yes")) {
                            tcp_client.startConnection(clientSocket.getInetAddress().toString(), 50000);
                            tcp_client.sendMessage("RESULT-MEET£€" + database.getMyInformation()[0] + splittedR[2] + "£€" + localizationController.gpsDirection(Double.parseDouble(splittedR[3]), Double.parseDouble(splittedR[4])), "");
                            //modifica gui luca

                        } else {
                            tcp_client.startConnection(clientSocket.getInetAddress().toString(), 50000);
                            tcp_client.sendMessage("RESPONSE-MEET£€" + database.getMyInformation()[0] + "£€"/*+valore di bergo*/, "");
                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (
                IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }


    }
}
