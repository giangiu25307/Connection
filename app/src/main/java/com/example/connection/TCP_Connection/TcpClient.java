package com.example.connection.TCP_Connection;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.ImageView;

import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.MessageController;
import com.example.connection.Controller.Task;
import com.example.connection.Database.Database;
import com.example.connection.TCP_Connection.Encryption;
import com.example.connection.View.Connection;
import com.example.connection.libs.AsyncServer;
import com.example.connection.libs.AsyncSocket;
import com.example.connection.libs.ByteBufferList;
import com.example.connection.libs.DataEmitter;
import com.example.connection.libs.Util;
import com.example.connection.libs.callback.CompletedCallback;
import com.example.connection.libs.callback.ConnectCallback;
import com.example.connection.libs.callback.DataCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Random;

public class   TcpClient {

    private int port = 50000;
    private Database database;
    private Encryption encryption;
    private String oldIp, oldMsg, oldLocalAddress, oldSecretKey, oldId, oldClearMsg, oldImage;
    private boolean noKey = false;
    private Connection connection;



    public TcpClient(Database database, Encryption encryption,Connection connection) {
        this.connection=connection;
        this.database = database;
        this.encryption = encryption;
    }

    public void sendMessageNoKey(String ip, String text, String id) {
        noKey=true;
        oldIp = ip;
        oldMsg = text;
        oldId = id;
        checkInterface(id);
        AsyncServer.getDefault().connectSocket(new InetSocketAddress(oldIp, port), new ConnectCallback() {
            @Override
            public void onConnectCompleted(Exception ex, final AsyncSocket socket) {
                System.out.println("Done");
                handleConnectCompleted(ex, socket, text);
            }
        });
    }

    public void handShake(String id, String publicKey, String msg) {
        noKey=false;
        oldClearMsg = msg;
        oldIp = database.findIp(id);
        oldId = id;
        checkInterface(id);
        encryption.generateAES();
        String shake = "handShake£€" + id + "£€";
        String secretKey = null;
        try {
            secretKey = encryption.convertSecretKeyToString(encryption.getSecretKey());
            oldSecretKey = secretKey;
            shake += encryption.encrypt(secretKey + "£€" + msg+ "£€" +ConnectionController.myUser.getIdUser(), encryption.convertStringToPublicKey(publicKey));
            oldMsg = shake;
            AsyncServer.getDefault().connectSocket(new InetSocketAddress(database.findIp(id), port), new ConnectCallback() {
                @Override
                public void onConnectCompleted(Exception ex, final AsyncSocket socket) {
                    System.out.println("Done");
                    handleConnectCompleted(ex, socket, oldMsg);
                }
            });
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message, String id) {
        noKey=false;
        oldClearMsg = message;
        oldIp = database.findIp(id);
        oldId = id;
        checkInterface(id);
        String msg = "message£€" + id + "£€";
        try {
            msg +=encryption.encryptAES(message, encryption.convertStringToSecretKey(database.getSymmetricKey(id)));

            oldMsg = msg;
            AsyncServer.getDefault().connectSocket(new InetSocketAddress(oldIp, port), new ConnectCallback() {
                @Override
                public void onConnectCompleted(Exception ex, final AsyncSocket socket) {
                    System.out.println("Done");
                    handleConnectCompleted(ex, socket, oldMsg);
                }
            });
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    public void sendImage(String imagePath, String id) throws IOException {
        noKey=false;
        Bitmap bmp = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        String imageString = "image£€" + id + "£€";
        oldImage = imagePath;
        try {
            imageString += encryption.encryptAES( new String(bos.toByteArray(), StandardCharsets.UTF_8),encryption.convertStringToSecretKey(database.getSymmetricKey(id)));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        checkInterface(id);
        AsyncServer.getDefault().connectSocket(new InetSocketAddress(oldIp, port), new ConnectCallback() {
            @Override
            public void onConnectCompleted(Exception ex, final AsyncSocket socket) {
                System.out.println("Done");
                handleConnectCompleted(ex, socket, oldMsg);
            }
        });
    }

    private void checkInterface(String id) {
        if (database.isOtherGroup(id)) {
            oldLocalAddress = database.findIp(ConnectionController.myUser.getIdUser());
            AsyncServer.getDefault().setLocalAddress(oldLocalAddress);
        } else {
            oldLocalAddress = "192.168.49.1";
            AsyncServer.getDefault().setLocalAddress(oldLocalAddress);
        }
    }

    private void checkDate(String id) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String datetime = "";
        try {
            Cursor dateDB = database.getLastMessageChat(id);
            datetime = dateDB.getString(dateDB.getColumnIndex(Task.TaskEntry.DATETIME));
        } catch (IndexOutOfBoundsException e) {
            datetime = String.valueOf(LocalDateTime.now());
        }
        try {
            date = format.parse(datetime);
            if (date.compareTo(format.parse(String.valueOf(LocalDateTime.now()))) < 0) {
                database.addMsg("", ConnectionController.myUser.getIdUser(), id);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void handleConnectCompleted(Exception ex, final AsyncSocket socket, String text) {
        if (ex != null) throw new RuntimeException(ex);

        Util.writeAll(socket, text.getBytes(), new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[Client] Successfully wrote message");

            }
        });

        socket.setDataCallback(new DataCallback() {
            @Override
            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                String received = new String(bb.getAllByteArray());
                System.out.println("[Client] Received Message " + received);

                if (!received.split("£€")[0].equals("messageConfirmed"))
                    sendMessageNoKey(oldIp, oldMsg, oldLocalAddress);
                else {
                    if(!noKey) {
                        Intent intent = new Intent(connection.getApplicationContext(), MessageController.getIstance().getClass());
                        if (received.split("£€")[1].equals("handShake")) {
                            database.createChat(oldId, database.getUserName(oldId), oldSecretKey);
                            checkDate(oldId);
                            database.addMsg(oldClearMsg, ConnectionController.myUser.getIdUser(), oldId);
                            intent.putExtra("intentType","messageController");
                            intent.putExtra("communicationType","tcp");
                            intent.putExtra("msg",oldClearMsg);
                            intent.putExtra("id",oldId);
                        } else if (received.split("£€")[1].equals("message")) {
                            checkDate(oldId);
                            database.addMsg(oldClearMsg, ConnectionController.myUser.getIdUser(), oldId);
                            intent.putExtra("intentType","messageController");
                            intent.putExtra("communicationType","tcp");
                            intent.putExtra("msg",oldClearMsg);
                            intent.putExtra("id",oldId);
                        } else {
                            checkDate(oldId);
                            database.addImage(oldImage, ConnectionController.myUser.getIdUser(), oldId);
                            intent.putExtra("intentType","messageController");
                            intent.putExtra("communicationType","tcp");
                            intent.putExtra("msg",oldImage);
                            intent.putExtra("id",oldId);

                        }
                        connection.getApplicationContext().sendBroadcast(intent);
                    }
                }
            }
        });

        socket.setClosedCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[Client] Successfully closed connection");
            }
        });

        socket.setEndCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException(ex);
                System.out.println("[Client] Successfully end connection");
            }
        });
    }
}