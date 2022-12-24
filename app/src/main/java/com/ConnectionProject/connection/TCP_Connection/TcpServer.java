package com.ConnectionProject.connection.TCP_Connection;

import android.content.Intent;

import com.ConnectionProject.connection.Controller.ConnectionController;
import com.ConnectionProject.connection.Controller.ImageController;
import com.ConnectionProject.connection.Database.Database;
import com.ConnectionProject.connection.Listener.MessageListener;
import com.ConnectionProject.connection.Model.ImageTcp;
import com.ConnectionProject.connection.UDP_Connection.Multicast;
import com.ConnectionProject.connection.UDP_Connection.Multicast_P2P;
import com.ConnectionProject.connection.UDP_Connection.MyNetworkInterface;
import com.ConnectionProject.connection.View.Connection;
import com.ConnectionProject.connection.View.MapFragment;
import com.ConnectionProject.connection.libs.AsyncServer;
import com.ConnectionProject.connection.libs.AsyncServerSocket;
import com.ConnectionProject.connection.libs.AsyncSocket;
import com.ConnectionProject.connection.libs.ByteBufferList;
import com.ConnectionProject.connection.libs.DataEmitter;
import com.ConnectionProject.connection.libs.Util;
import com.ConnectionProject.connection.libs.callback.CompletedCallback;
import com.ConnectionProject.connection.libs.callback.DataCallback;
import com.ConnectionProject.connection.libs.callback.ListenCallback;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;

public class TcpServer {

    private int port;
    private Database database;
    private Encryption encryption;
    private Connection connection;
    private SimpleDateFormat sdf;
    private TcpClient tcpClient;
    private Multicast_P2P multicastP2p;
    private boolean isRunning;
    private HashMap<String, ImageTcp> hashMap;

    public TcpServer(Connection connection, Database database, Encryption encryption, TcpClient tcpClient) {
        this.connection = connection;
        this.database = database;
        this.encryption = encryption;
        this.tcpClient = tcpClient;
        isRunning = false;
        port = 50000;
        hashMap = new HashMap<String, ImageTcp>();
    }

    public void setMulticastP2p(Multicast_P2P multicastP2p) {
        this.multicastP2p = multicastP2p;
    }

    /**
     * Server close socket
     */
    public void close() {
        AsyncServer.getDefault().dump();
        isRunning = false;
        System.out.println("[Server] Server close socket");
    }

    public boolean isRunning(){
        return isRunning;
    }

    /**
     * Server started setup
     */
    public void setup() {
        try {
            System.out.println("[Server] Server started setup");
            isRunning = true;

            AsyncServer.getDefault().listen(InetAddress.getByName("::"), port, new ListenCallback() {

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
                    isRunning = false;
                    System.out.println("[Server] Successfully shutdown server");
                }
            });
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle the connection
     * @param socket
     */
    private void handleAccept(final AsyncSocket socket) {
        System.out.println("[Server] New Connection " + socket.toString());

        socket.setDataCallback(new DataCallback() {
            @Override
            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                String received = new String(bb.getAllByteArray());
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

    /**
     * Identify the message receive and choose what to do in base of it
     * @param msg message received
     * @return the operation to be resent to the other user for the successful communication
     */
    public String messageIdentifier(String msg) {
        try {
            Intent intent = new Intent(connection.getApplicationContext(), MessageListener.getIstance().getClass());
            String[] splittedR = msg.split("£€");
            if (database.isUserBlocked(splittedR[2])) {
                return "";
            }
            switch (splittedR[0]) {
                case "share":
                    if (splittedR[1].equals(ConnectionController.myUser.getIdUser())) {
                        try {
                            String message = encryption.decryptAES(splittedR[2], encryption.convertStringToSecretKey(database.getSymmetricKey(splittedR[3])));
                            String[] splittedMessage = message.split("£€");
                            if (splittedMessage[1].equals("number")) {
                                database.setNumber(splittedR[3], splittedMessage[0]);
                            } else if (splittedMessage[1].equals("whatsapp")) {
                                database.setWhatsapp(splittedR[3], splittedMessage[0]);
                            } else if (splittedMessage[1].equals("telegram")) {
                                database.setTelegram(splittedR[3], splittedMessage[0]);
                            }
                        } catch (InvalidKeyException e) {
                            e.printStackTrace();
                        }
                    } else {
                        tcpClient.sendMessageNoKey(database.findIp(splittedR[1]), msg, splittedR[1]);
                    }
                    return "share";
                case "image":
                    if (splittedR[1].equals(ConnectionController.myUser.getIdUser())) {
                        sdf = new SimpleDateFormat("yyyy.MM.dd_HH:mm:ss");
                        String currentDateandTime = sdf.format(new Date());
                        File.createTempFile(currentDateandTime + ".jpeg", null, connection.getApplicationContext().getCacheDir());
                        FileOutputStream fos = new FileOutputStream(connection.getApplicationContext().getCacheDir() + currentDateandTime + ".jpeg");
                        fos.write(msg.getBytes());
                        fos.close();
                        database.addMsg(connection.getApplicationContext().getCacheDir() + currentDateandTime + ".jpeg", splittedR[1], splittedR[1]);
                        intent = new Intent(connection.getApplicationContext(), MessageListener.getIstance().getClass());
                        intent.putExtra("intentType", "messageController");
                        intent.putExtra("communicationType", "tcp");
                        intent.putExtra("idUser", splittedR[1]);
                        //  intent.putExtra("msg",message); da finire la parte delle immagini
                        intent.putExtra("idChat", splittedR[1]);
                        connection.getApplicationContext().sendBroadcast(intent);
                    } else {
                        tcpClient.sendMessageNoKey(database.findIp(splittedR[1]), msg, splittedR[1]);
                    }
                    return "image";
                case "sendInfo":
                    //The group owner send all user information to the new user --------------------------------------------------------------------------------------------------------------------------------
                    for (int i = 1; i < splittedR.length - 1; i = i + 12) {
                        if (!splittedR[i].equals(ConnectionController.myUser.getIdUser())) {
                            if (i == 1) {
                                splittedR[2] = splittedR[2].split("%")[0] + "%" + MyNetworkInterface.wlanName;
                                database.addUser(splittedR[i], splittedR[i + 1], splittedR[i + 2], splittedR[i + 3], splittedR[i + 4], splittedR[i + 5], splittedR[i + 6], splittedR[i + 7], splittedR[i + 8], splittedR[i + 9], splittedR[i + 10]/*ImageController.decodeImage(splittedR[i + 10], connection.getApplicationContext(), splittedR[i])*/, splittedR[i + 11]);
                                database.setOtherGroup(splittedR[i]);
                                database.setMyGroupOwnerIp(splittedR[i + 1], splittedR[i]);
                            } else {
                                splittedR[i + 1] = splittedR[i + 1].split("%")[0] + "%" + MyNetworkInterface.wlanName;
                                database.addUser(splittedR[i], splittedR[i + 1], splittedR[i + 2], splittedR[i + 3], splittedR[i + 4], splittedR[i + 5], splittedR[i + 6], splittedR[i + 7], splittedR[i + 8], splittedR[i + 9], splittedR[i + 10]/*ImageController.decodeImage(splittedR[i + 10], connection.getApplicationContext(), splittedR[i])*/, splittedR[i + 11]);
                                database.setOtherGroup(splittedR[i]);
                            }
                        }
                    }
                    intent = new Intent(connection.getApplicationContext(), MessageListener.getIstance().getClass());
                    intent.putExtra("intentType", "messageController");
                    intent.putExtra("communicationType", "sendImage");
                    connection.getApplicationContext().sendBroadcast(intent);
                    Multicast.dbUserEvent = false;
                    if (Connection.fragmentName.equals("MAP")) {
                        MapFragment mapFragment = MapFragment.getIstance();
                        mapFragment.graphicRefresh();
                    }
                    return "sendInfo";
                case "message":
                    //Add the receive msg to the db --------------------------------------------------------------------------------------------------------------------------------
                    if (splittedR[1].equals(ConnectionController.myUser.getIdUser())) {
                        try {
                            String message = encryption.decryptAES(splittedR[2], encryption.convertStringToSecretKey(database.getSymmetricKey(splittedR[3])));
                            database.addMsg(message, splittedR[3], splittedR[3]);
                            intent = new Intent(connection.getApplicationContext(), MessageListener.getIstance().getClass());
                            intent.putExtra("intentType", "messageController");
                            intent.putExtra("communicationType", "tcp");
                            intent.putExtra("msg", message);
                            intent.putExtra("idChat", splittedR[3]);
                            intent.putExtra("idUser", splittedR[3]);
                            connection.getApplicationContext().sendBroadcast(intent);
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                        }
                    } else {
                        tcpClient.sendMessageNoKey(database.findIp(splittedR[1]), msg, splittedR[1]);
                    }
                    return "message";
                case "reMessage":
                    //Add the receive msg to the db --------------------------------------------------------------------------------------------------------------------------------
                    if (splittedR[1].equals(ConnectionController.myUser.getIdUser())) {
                        try {
                            String message = encryption.decryptAES(splittedR[2], encryption.convertStringToSecretKey(database.getSymmetricKey(splittedR[3])));
                            database.addMsg(message, splittedR[3], splittedR[3]);
                            intent = new Intent(connection.getApplicationContext(), MessageListener.getIstance().getClass());
                            intent.putExtra("intentType", "messageController");
                            intent.putExtra("communicationType", "tcp");
                            intent.putExtra("msg", message);
                            intent.putExtra("idChat", splittedR[3]);
                            intent.putExtra("idUser", splittedR[3]);
                            connection.getApplicationContext().sendBroadcast(intent);
                        } catch (GeneralSecurityException e) {
                            e.printStackTrace();
                        }
                    } else {
                        tcpClient.sendMessageNoKey(database.findIp(splittedR[1]), msg, splittedR[1]);
                    }
                    return "reMessage";
                case "handShake":
                    if (splittedR[1].equals(ConnectionController.myUser.getIdUser())) {
                        String message[] = encryption.decrypt(splittedR[2]).split("£€");
                        database.createChat(message[2], database.getUserName(message[2]), message[0]);
                        if(message[1].isEmpty()) {
                            return "handShake";
                        }
                        database.addMsg(message[1], message[2], message[2]);
                        intent = new Intent(connection.getApplicationContext(), MessageListener.getIstance().getClass());
                        intent.putExtra("intentType", "messageController");
                        intent.putExtra("communicationType", "tcp");
                        intent.putExtra("msg", message[1]);
                        intent.putExtra("idChat", message[2]);
                        intent.putExtra("idUser", message[2]);
                        connection.getApplicationContext().sendBroadcast(intent);
                    } else {
                        tcpClient.sendMessageNoKey(database.findIp(splittedR[1]), msg, splittedR[1]);
                    }
                    return "handShake";
                case "groupInfo":
                    for (int i = 1; i < splittedR.length - 1; i = i + 12) {
                        if (!splittedR[i].equals(ConnectionController.myUser.getIdUser())) {
                            if (i == 1)
                                database.setMyGroupOwnerIp(splittedR[2].split("%")[0] + "%" + MyNetworkInterface.wlanName, splittedR[i]);
                            database.addUser(splittedR[i], splittedR[2].split("%")[0] + "%" + MyNetworkInterface.wlanName, splittedR[i + 2], splittedR[i + 3], splittedR[i + 4], splittedR[i + 5], splittedR[i + 6], splittedR[i + 7], splittedR[i + 8], splittedR[i + 9], "", splittedR[i + 11]);
                            database.setOtherGroup(splittedR[i]);
                        }
                    }
                    splittedR[1] += "€€" + ConnectionController.myUser.getIdUser();
                    splittedR[2] = database.getMyGroupOwnerIp();
                    String string = "";
                    for (int i = 0; i < splittedR.length; i++) {
                        string += splittedR[i] + "£€";
                    }
                    DatagramPacket message = new DatagramPacket(string.getBytes(), string.getBytes().length, InetAddress.getByName("234.0.0.0"), port);
                    multicastP2p.getMulticastP2P().send(message);
                    Multicast_P2P.dbUserEvent = false;
                    if (Connection.fragmentName.equals("MAP")) {
                        MapFragment mapFragment = MapFragment.getIstance();
                        mapFragment.graphicRefresh();
                    }
                    return "groupInfo";
                default:
                    return "";
            }
        } catch (IOException e) {
            //report exception somewhere.
            e.printStackTrace();
        }
        return "";
    }

}