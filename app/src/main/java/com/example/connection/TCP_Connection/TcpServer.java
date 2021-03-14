package com.example.connection.TCP_Connection;

import android.database.Cursor;

import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Task;
import com.example.connection.Database.Database;
import com.example.connection.UDP_Connection.Multicast;
import com.example.connection.View.Connection;
import com.example.connection.libs.AsyncServer;
import com.example.connection.libs.AsyncServerSocket;
import com.example.connection.libs.AsyncSocket;
import com.example.connection.libs.ByteBufferList;
import com.example.connection.libs.DataEmitter;
import com.example.connection.libs.Util;
import com.example.connection.libs.callback.CompletedCallback;
import com.example.connection.libs.callback.DataCallback;
import com.example.connection.libs.callback.ListenCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class TcpServer {

    private int port;
    private Database database;
    private Encryption encryption;
    private Connection connection;
    private SimpleDateFormat sdf;
    private TcpClient tcpClient;

    public TcpServer(Connection connection, Database database, Encryption encryption, TcpClient tcpClient) {
        this.connection = connection;
        this.database = database;
        this.encryption = encryption;
        this.tcpClient = tcpClient;
        port = 50000;
    }

    public void setup() {
        try {

            AsyncServer.getDefault().listen(InetAddress.getByName("0.0.0.0"), port, new ListenCallback() {

                @Override
                public void onAccepted(final AsyncSocket socket) {
                    handleAccept(socket);
                }

                @Override
                public void onListening(AsyncServerSocket socket) {
                    System.out.println("[Server] Server started listening for connections");
                }

                @Override
                public void onCompleted(Exception ex) {
                    if (ex != null) throw new RuntimeException(ex);
                    System.out.println("[Server] Successfully shutdown server");
                }
            });
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    private void handleAccept(final AsyncSocket socket) {
        System.out.println("[Server] New Connection " + socket.toString());

        socket.setDataCallback(new DataCallback() {
            @Override
            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                String received=new String(bb.getAllByteArray());
                System.out.println("[Server] Received Message " + received);
                String identifier = messageIdentifier(received);
                Util.writeAll(socket, ("messageConfirmed£€" + identifier).getBytes(), new CompletedCallback() {
                    @Override
                    public void onCompleted(Exception ex) {
                        if (ex != null) throw new RuntimeException(ex);
                        System.out.println("[Server] Successfully wrote message");
                        socket.close();
                    }
                });
            }
        });

        socket.setClosedCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[Server] Successfully closed connection");
            }
        });

        socket.setEndCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) {
                    throw new RuntimeException(ex);
                }
                System.out.println("[Server] Successfully end connection");
            }
        });
    }

    public String messageIdentifier(String msg) {
        try {
            String[] splittedR = msg.split("£€");
            System.out.println(msg);

            switch (splittedR[0]) {
                case "image":
                    if (splittedR[1].equals(ConnectionController.myUser.getIdUser())) {
                        sdf = new SimpleDateFormat("yyyy.MM.dd_HH:mm:ss");
                        String currentDateandTime = sdf.format(new Date());
                        File.createTempFile(currentDateandTime + ".jpeg", null, connection.getApplicationContext().getCacheDir());
                        FileOutputStream fos = new FileOutputStream(connection.getApplicationContext().getCacheDir() + currentDateandTime + ".jpeg");
                        fos.write(msg.getBytes());
                        fos.close();
                        database.addMsg(connection.getApplicationContext().getCacheDir() + currentDateandTime + ".jpeg", splittedR[1], splittedR[1]);
                    } else {
                        tcpClient.sendMessageNoKey(database.findIp(splittedR[1]), msg, database.findIp(ConnectionController.myUser.getIdUser()));
                    }
                    return "image";
                case "sendInfo":
                    //The group owner send all user information to the new user --------------------------------------------------------------------------------------------------------------------------------
                    for (int i = 1; i < splittedR.length; i = i + 12) {
                        if (i == 1)
                            database.addUser(splittedR[i], "192.168.49.1", splittedR[i + 2], splittedR[i + 3], splittedR[i + 4], splittedR[i + 5], splittedR[i + 6], splittedR[i + 7], splittedR[i + 8], splittedR[i + 9], splittedR[i + 10], splittedR[i + 11]);
                        else
                            database.addUser(splittedR[i], splittedR[i + 1], splittedR[i + 2], splittedR[i + 3], splittedR[i + 4], splittedR[i + 5], splittedR[i + 6], splittedR[i + 7], splittedR[i + 8], splittedR[i + 9], splittedR[i + 10], splittedR[i + 11]);
                    }
                    Multicast.dbUserEvent = false;
                    return "sendInfo";
                case "message":
                    //Add the receive msg to the db --------------------------------------------------------------------------------------------------------------------------------
                    if (splittedR[1].equals(ConnectionController.myUser.getIdUser())) {
                        msg = splittedR[2];
                        System.out.println(splittedR[2]);
                        Date date = new Date();
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
                        String datetime = "";
                        try {
                            Cursor dateDB = database.getLastMessageChat(splittedR[1]);
                            datetime = dateDB.getString(dateDB.getColumnIndex(Task.TaskEntry.DATETIME));
                        } catch (IndexOutOfBoundsException e) {
                            datetime = String.valueOf(LocalDateTime.now());
                        }
                        try {
                            date = format.parse(datetime);
                            if (date.compareTo(format.parse(String.valueOf(LocalDateTime.now()))) < 0) {
                                database.addMsg("", splittedR[1], splittedR[1]);
                            }
                            database.addMsg(encryption.decryptAES(msg.getBytes(), encryption.convertStringToSecretKey(database.getSymmetricKey(splittedR[1]))), splittedR[1], splittedR[1]);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println(msg);
                        tcpClient.sendMessageNoKey(database.findIp(splittedR[1]), msg, database.findIp(ConnectionController.myUser.getIdUser()));

                    }
                    return "message";
                case "handShake":
                    if (splittedR[1].equals(ConnectionController.myUser.getIdUser())) {
                        database.createChat(splittedR[1], database.getUserName(splittedR[1]), encryption.decrypt(splittedR[2]).split("£€")[0]);
                        database.addMsg("", splittedR[1], splittedR[1]);
                        database.addMsg(encryption.decrypt(splittedR[2]).split("£€")[1], splittedR[1], splittedR[1]);
                    } else {
                        tcpClient.sendMessageNoKey(database.findIp(splittedR[1]), msg, database.findIp(ConnectionController.myUser.getIdUser()));
                    }
                    return "handShake";
                    /*case "REQUEST-MEET":
                        //bergo's stuff popup richiesta se vuoi incontrarmi return si/no
                        //database.setAccept(valore ritornato da bergo);
                        tcp_client.startConnection(clientSocket.getInetAddress().toString(), 50000);
                        tcp_client.sendMessage("RESPONSE-MEET£€" + database.getMyInformation()[0] + "£€"/*+valore di bergo*//*, "");
                        break;
                    case "RESPONSE-MEET":
                        database.setAccept(splittedR[1], splittedR[2]);
                        break;*/
                    /*case "RESULT-MEET":
                        if (database.getAccept(splittedR[1]).equals("yes")) {
                            tcp_client.startConnection(clientSocket.getInetAddress().toString(), 50000);
                            tcp_client.sendMessage("RESULT-MEET£€" + database.getMyInformation()[0] + splittedR[2] + "£€" + localizationController.gpsDirection(Double.parseDouble(splittedR[3]), Double.parseDouble(splittedR[4])), "");
                            //modifica gui luca

                        } else {
                            tcp_client.startConnection(clientSocket.getInetAddress().toString(), 50000);
                            tcp_client.sendMessage("RESPONSE-MEET£€" + database.getMyInformation()[0] + "£€"/*+valore di bergo, "");
                        }
                        break;
                */
                default:
                    return "";
            }
        } catch (
                IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
        return "";
    }
}