package com.ConnectionProject.connection.TCP_Connection;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.widget.Toast;

import com.ConnectionProject.connection.Controller.ConnectionController;
import com.ConnectionProject.connection.Database.Database;
import com.ConnectionProject.connection.Listener.MessageListener;
import com.ConnectionProject.connection.Model.User;
import com.ConnectionProject.connection.UDP_Connection.MyNetworkInterface;
import com.ConnectionProject.connection.View.Connection;
import com.ConnectionProject.connection.libs.AsyncServer;
import com.ConnectionProject.connection.libs.AsyncSocket;
import com.ConnectionProject.connection.libs.ByteBufferList;
import com.ConnectionProject.connection.libs.DataEmitter;
import com.ConnectionProject.connection.libs.Util;
import com.ConnectionProject.connection.libs.callback.CompletedCallback;
import com.ConnectionProject.connection.libs.callback.ConnectCallback;
import com.ConnectionProject.connection.libs.callback.DataCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
     * @param imagePath image to be sent
     * @param id        id of the person which the image is for
     * @throws IOException
     */
    public void sendImage(String imagePath, String id) throws IOException {
        noKey = false;
        Bitmap bmp = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, bos);
        String imageString = "image£€" + id + "£€";
        oldImage = imagePath;
        checkInterface(id);
        try {
            imageString += encryption.encryptAES(new String(bos.toByteArray(), StandardCharsets.UTF_8), encryption.convertStringToSecretKey(database.getSymmetricKey(id)));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        AsyncServer.getDefault().connectSocket(new InetSocketAddress(oldIp, port), new ConnectCallback() {
            @Override
            public void onConnectCompleted(Exception ex, final AsyncSocket socket) {
                System.out.println("Done");
                handleConnectCompleted(ex, socket, oldMsg);
            }
        });
    }

    /**
     * Send my image to every user connected to me
     *
     * @throws IOException
     */
    public void sendMyImageToEveryone(User user, String secretKey) throws IOException {

        noKey = false;
        String imageString = "";
        String imageToSend = ConnectionController.myUser.getProfilePicBase64();
        if (imageToSend.equals("noImage")) return;
        boolean needToReadImage = true;
        int size = 800;
        for (int i = 0; needToReadImage; i++) {
            if ((i + 1) * size < imageToSend.length()) {
                imageString = ConnectionController.myUser.getIdUser() + "£€" + i + "£€" + imageToSend.substring(i * size, (i + 1) * size);
            } else {
                imageString = ConnectionController.myUser.getIdUser() + "£€" + i + "£€" + imageToSend.substring(i * size) + "£€endImage"; //if you alter the length alter it in the receive
                needToReadImage = false;
            }
            oldIp = database.findIp(user.getIdUser());
            checkInterface(user.getIdUser());
            oldId = user.getIdUser();
            try {
                String msg = encryption.encryptAES(imageString, encryption.convertStringToSecretKey(secretKey));
                oldMsg = "imageToEveryone£€" + user.getIdUser() + "£€" + msg + "£€" + ConnectionController.myUser.getIdUser();
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
            long l = System.currentTimeMillis();
            while (l + 200 > System.currentTimeMillis()) ;
        }
    }

    /**
     * Check the interface to use to send the message
     *
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
     * On connection completed i check if it all went correctly and choose what to do in base of the return message
     *
     * @param ex
     * @param socket
     * @param text   message to check the result of the tcp operation
     */
    private void handleConnectCompleted(Exception ex, final AsyncSocket socket, String text) {
        if (ex != null) throw new RuntimeException(ex);

        Util.writeAll(socket, text.getBytes(), new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                if (ex != null) throw new RuntimeException("[RUNTIME - CLIENT] " + ex);
                System.out.println("[Client] Successfully wrote message");

            }
        });

        socket.setDataCallback(new DataCallback() {
            @Override
            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {
                String received = new String(bb.getAllByteArray());
                System.out.println("[Client] Received Message " + received);

                if (!received.split("£€")[0].equals("messageConfirmed"))
                    if (counter != 5)
                        sendMessageNoKey(oldIp, oldMsg, oldLocalAddress);
                    else {
                        database.addMsg(oldClearMsg, ConnectionController.myUser.getIdUser(), oldId);
                        ;
                        database.setMessageSent(oldId, database.getLastMessageId(oldId), "0");
                        counter++;
                        Intent intent = new Intent(connection.getApplicationContext(), MessageListener.getIstance().getClass());
                        intent.putExtra("intentType", "messageController");
                        intent.putExtra("communicationType", "tcp");
                        intent.putExtra("msg", oldClearMsg);
                        intent.putExtra("idChat", oldId);
                        intent.putExtra("idUser", ConnectionController.myUser.getIdUser());
                        intent.putExtra("sent", "0");
                        connection.getApplicationContext().sendBroadcast(intent);
                        if (received.split("£€")[1].equals("reMessage"))
                            Toast.makeText(connection.getApplicationContext(), "Send message failed", Toast.LENGTH_SHORT).show();
                    }
                else {
                    if (!noKey) {
                        Intent intent = new Intent(connection.getApplicationContext(), MessageListener.getIstance().getClass());
                        if (received.split("£€")[1].equals("handShake")) {
                            database.createChat(oldId, database.getUserName(oldId), oldSecretKey);
                            if(!oldClearMsg.isEmpty()) {
                                database.addMsg(oldClearMsg, ConnectionController.myUser.getIdUser(), oldId);
                                intent.putExtra("intentType", "messageController");
                                intent.putExtra("communicationType", "tcp");
                                intent.putExtra("msg", oldClearMsg);
                                intent.putExtra("idChat", oldId);
                                intent.putExtra("idUser", ConnectionController.myUser.getIdUser());
                                intent.putExtra("sent", "1");
                                connection.getApplicationContext().sendBroadcast(intent);
                            }
                        } else if (received.split("£€")[1].equals("message")) {
                            database.addMsg(oldClearMsg, ConnectionController.myUser.getIdUser(), oldId);
                            intent.putExtra("intentType", "messageController");
                            intent.putExtra("communicationType", "tcp");
                            intent.putExtra("msg", oldClearMsg);
                            intent.putExtra("idChat", oldId);
                            intent.putExtra("idUser", ConnectionController.myUser.getIdUser());
                            intent.putExtra("sent", "1");
                            connection.getApplicationContext().sendBroadcast(intent);
                        } else if (received.split("£€")[1].equals("reMessage")) {
                            database.addMsg(oldClearMsg, ConnectionController.myUser.getIdUser(), oldId);
                            intent.putExtra("intentType", "messageController");
                            intent.putExtra("communicationType", "tcp");
                            intent.putExtra("msg", oldClearMsg);
                            intent.putExtra("idChat", oldId);
                            intent.putExtra("idUser", ConnectionController.myUser.getIdUser());
                            intent.putExtra("sent", "2");
                            connection.getApplicationContext().sendBroadcast(intent);
                        } else if (received.split("£€")[1].equals("share") || received.split("£€")[1].equals("imageToEveryone")
                                || received.split("£€")[1].equals("sendImageZipped")) {
                            //Non deve fare nulla, deve essere vuoto
                        } else { //Image
                            database.addImage(oldImage, ConnectionController.myUser.getIdUser(), oldId);
                            intent.putExtra("intentType", "messageController");
                            intent.putExtra("communicationType", "tcp");
                            intent.putExtra("msg", oldImage);
                            intent.putExtra("idChat", oldId);
                            intent.putExtra("idUser", ConnectionController.myUser.getIdUser());
                            intent.putExtra("sent", "1");
                            connection.getApplicationContext().sendBroadcast(intent);
                        }
                        if(!received.split("£€")[1].equals("handShake"))
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