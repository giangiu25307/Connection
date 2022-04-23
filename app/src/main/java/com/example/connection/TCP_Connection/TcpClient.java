package com.example.connection.TCP_Connection;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import com.example.connection.Controller.ConnectionController;
import com.example.connection.Database.Database;
import com.example.connection.Listener.MessageListener;
import com.example.connection.Model.LastMessage;
import com.example.connection.UDP_Connection.MyNetworkInterface;
import com.example.connection.View.ChatActivity;
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
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class TcpClient {

    private int port = 50000;
    private Database database;
    private Encryption encryption;
    private String oldIp, oldMsg, oldLocalAddress, oldSecretKey, oldId, oldClearMsg, oldImage;
    private boolean noKey = false;
    private Connection connection;
    private int counter;


    public TcpClient(Database database, Encryption encryption, Connection connection) {
        this.connection = connection;
        this.database = database;
        this.encryption = encryption;
        counter = 0;
    }

    /**
     * Send a message without encrypting it because it is already crypt
     * @param ip   ip to send the message
     * @param text text to send
     * @param id   id of the person which the message is for
     */
    public void sendMessageNoKey(String ip, String text, String id) {
        noKey = true;
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

    /**
     * HandShake to interchange the secret keys
     * @param id        id of the person which the message is for
     * @param publicKey key of the person which the message is for
     * @param msg       message to send with AES key
     */
    public void handShake(String id, String publicKey, String msg) {
        noKey = false;
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
            shake += encryption.encrypt(secretKey + "£€" + msg + "£€" + ConnectionController.myUser.getIdUser(), encryption.convertStringToPublicKey(publicKey));
            oldMsg = shake;
            AsyncServer.getDefault().connectSocket(new InetSocketAddress(database.findIp(id), port), new ConnectCallback() {
                @Override
                public void onConnectCompleted(Exception ex, final AsyncSocket socket) {
                    System.out.println("Done");
                    handleConnectCompleted(ex, socket, oldMsg);
                }
            });
        } catch (GeneralSecurityException | ConnectException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send a message to another user
     * @param message message to be sent
     * @param id      id of the person which the message is for
     */
    public void sendMessage(String message, String id) {
        noKey = false;
        oldClearMsg = message;
        oldIp = database.findIp(id);
        oldId = id;
        checkInterface(id);
        String msg = "message£€" + id + "£€";
        try {
            msg += encryption.encryptAES(message, encryption.convertStringToSecretKey(database.getSymmetricKey(id)));
            oldMsg = msg + "£€" + ConnectionController.myUser.getIdUser();
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

    /**
     * Retry to send a message
     * @param message message to be sent
     * @param id      id of the person which the message is for
     */
    public void reSendMessage(String message, String id) {
        noKey = false;
        oldClearMsg = message;
        oldIp = database.findIp(id);
        oldId = id;
        checkInterface(id);
        String msg = "reMessage£€" + id + "£€";
        try {
            msg += encryption.encryptAES(message, encryption.convertStringToSecretKey(database.getSymmetricKey(id)));
            oldMsg = msg + "£€" + ConnectionController.myUser.getIdUser();
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

    /**
     * Send my number or my telegram or my whatsapp to another user
     * @param message message to be sent
     * @param id      id of the person which the message is for
     */
    public void sendShare(String message, String id) {
        noKey = false;
        oldClearMsg = message;
        oldIp = database.findIp(id);
        oldId = id;
        checkInterface(id);
        String msg = "share£€" + id + "£€";
        try {
            msg += encryption.encryptAES(message, encryption.convertStringToSecretKey(database.getSymmetricKey(id)));
            oldMsg = msg + "£€" + ConnectionController.myUser.getIdUser();
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

    /**
     * Send an image to another user
     * @param imagePath image to be sent
     * @param id        id of the person which the image is for
     * @throws IOException
     */
    public void sendImage(String imagePath, String id) throws IOException {
        noKey = false;
        Bitmap bmp = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG,50,bos);
        String imageString = "image£€" + id + "£€";
        oldImage = imagePath;
        try {
            imageString += encryption.encryptAES(new String(bos.toByteArray(), StandardCharsets.UTF_8), encryption.convertStringToSecretKey(database.getSymmetricKey(id)));
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

    /**
     * Check the interface to use to send the message
     * @param id id of the person to see which interface need to be used
     */
    private void checkInterface(String id) {
        if (database.isOtherGroup(id)) {
            oldLocalAddress = MyNetworkInterface.wlanIpv6Address;
            AsyncServer.getDefault().setLocalAddress(oldLocalAddress);
        } else {
            oldLocalAddress = MyNetworkInterface.p2pIpv6Address;
            AsyncServer.getDefault().setLocalAddress(oldLocalAddress);
        }
    }

    /**
     * Check if a date message is already presence for today inside the database for this chat, otherwise i send a blank message
     * @param id id of the chat to be checked
     */
    private void checkDate(String id) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String datetime = "";
        try {
            LastMessage lastMessage = database.getLastMessageChat(id);
            datetime = lastMessage.getDateTime();
        } catch (IndexOutOfBoundsException e) {
            datetime = String.valueOf(LocalDateTime.now());
        }
        try {
            if(datetime != null) {
                date = format.parse(datetime);
                if (date.compareTo(format.parse(String.valueOf(LocalDateTime.now()))) < 0) {
                    database.addMsg("", ConnectionController.myUser.getIdUser(), id);
                }
            }else{
                date = format.parse(LocalDateTime.now().toString());
                database.addMsg("", ConnectionController.myUser.getIdUser(), id);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * On connection completed i check if it all went correctly and choose what to do in base of the return message
     * @param ex
     * @param socket
     * @param text   message to check the result of the tcp operation
     */
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
                    if(counter != 5)
                        sendMessageNoKey(oldIp, oldMsg, oldLocalAddress);
                    else
                    {
                        database.addMsg(oldClearMsg, ConnectionController.myUser.getIdUser(), oldId);                        ;
                        database.setMessageSent(oldId,database.getLastMessageId(oldId),"0");
                        counter++;
                        Intent intent = new Intent(connection.getApplicationContext(), MessageListener.getIstance().getClass());
                        intent.putExtra("intentType", "messageController");
                        intent.putExtra("communicationType", "tcp");
                        intent.putExtra("msg", oldClearMsg);
                        intent.putExtra("idChat", oldId);
                        intent.putExtra("idUser", ConnectionController.myUser.getIdUser());
                        intent.putExtra("sent", "0");
                        connection.getApplicationContext().sendBroadcast(intent);
                        if(received.split("£€")[1].equals("reMessage"))
                            Toast.makeText(connection.getApplicationContext(), "Send message failed", Toast.LENGTH_SHORT).show();
                    }
                else {
                    if (!noKey) {
                        Intent intent = new Intent(connection.getApplicationContext(), MessageListener.getIstance().getClass());
                        if (received.split("£€")[1].equals("handShake")) {
                            database.createChat(oldId, database.getUserName(oldId), oldSecretKey);
                            checkDate(oldId);
                            database.addMsg(oldClearMsg, ConnectionController.myUser.getIdUser(), oldId);
                            intent.putExtra("intentType", "messageController");
                            intent.putExtra("communicationType", "tcp");
                            intent.putExtra("msg", oldClearMsg);
                            intent.putExtra("idChat", oldId);
                            intent.putExtra("idUser", ConnectionController.myUser.getIdUser());
                            intent.putExtra("sent", "1");
                            connection.getApplicationContext().sendBroadcast(intent);
                        } else if (received.split("£€")[1].equals("message")) {
                            checkDate(oldId);
                            database.addMsg(oldClearMsg, ConnectionController.myUser.getIdUser(), oldId);
                            intent.putExtra("intentType", "messageController");
                            intent.putExtra("communicationType", "tcp");
                            intent.putExtra("msg", oldClearMsg);
                            intent.putExtra("idChat", oldId);
                            intent.putExtra("idUser", ConnectionController.myUser.getIdUser());
                            intent.putExtra("sent", "1");
                            connection.getApplicationContext().sendBroadcast(intent);
                        } else if (received.split("£€")[1].equals("reMessage")) {
                            checkDate(oldId);
                            database.addMsg(oldClearMsg, ConnectionController.myUser.getIdUser(), oldId);
                            intent.putExtra("intentType", "messageController");
                            intent.putExtra("communicationType", "tcp");
                            intent.putExtra("msg", oldClearMsg);
                            intent.putExtra("idChat", oldId);
                            intent.putExtra("idUser", ConnectionController.myUser.getIdUser());
                            intent.putExtra("sent", "2");
                            connection.getApplicationContext().sendBroadcast(intent);
                        } else if(received.split("£€")[1].equals("share")) {
                            //Non deve fare nulla, deve essere vuoto
                        }else { //Image
                            checkDate(oldId);
                            database.addImage(oldImage, ConnectionController.myUser.getIdUser(), oldId);
                            intent.putExtra("intentType", "messageController");
                            intent.putExtra("communicationType", "tcp");
                            intent.putExtra("msg", oldImage);
                            intent.putExtra("idChat", oldId);
                            intent.putExtra("idUser", ConnectionController.myUser.getIdUser());
                            intent.putExtra("sent", "1");
                            connection.getApplicationContext().sendBroadcast(intent);
                        }
                        database.setRequest(oldId, "false");
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