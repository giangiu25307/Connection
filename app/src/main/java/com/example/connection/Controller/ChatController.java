package com.example.connection.Controller;

import android.content.Context;
import android.content.Intent;

import com.example.connection.Database.Database;
import com.example.connection.Listener.MessageListener;
import com.example.connection.TCP_Connection.TcpClient;
import com.example.connection.UDP_Connection.Multicast_P2P;
import com.example.connection.UDP_Connection.Multicast_WLAN;
import com.example.connection.UDP_Connection.MyNetworkInterface;

import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class ChatController {

    private TcpClient tcp;
    private Multicast_P2P udpP2p;
    private Multicast_WLAN udpWlan;
    private Database database;
    public static ChatController istance = null;
    private ConnectionController connectionController;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    public static ChatController getInstance() {
        return istance;
    }

    public ChatController newIstance(Database database, TcpClient tcp, Multicast_P2P udpP2p, Multicast_WLAN udpWlan, ConnectionController connectionController) {
        istance = new ChatController();
        istance.setDatabase(database);
        istance.setTcp(tcp);
        istance.setUdpP2p(udpP2p);
        istance.setUdpWlan(udpWlan);
        istance.setConnectionController(connectionController);
        return istance;
    }

    /**
     * Set the tcpClient
     *
     * @param tcp  tcpClient to be set
     */
    public void setTcp(TcpClient tcp) {
        this.tcp = tcp;
    }

    /**
     * Set the MulticastP2P
     *
     * @param udpP2p  MulticastP2P to be set
     */
    public void setUdpP2p(Multicast_P2P udpP2p) {
        this.udpP2p = udpP2p;
    }

    /**
     * Set the MulticastWLAN
     *
     * @param udpWlan  MulticastWLAN to be set
     */
    public void setUdpWlan(Multicast_WLAN udpWlan) {
        this.udpWlan = udpWlan;
    }

    /**
     * Set the ConnectionController
     *
     * @param connectionController  connectionController to be set
     */
    public void setConnectionController(ConnectionController connectionController) {
        this.connectionController = connectionController;
    }

    /**
     * Set the Database
     *
     * @param database  database to be set
     */
    private void setDatabase(Database database) {
        this.database = database;
    }


    public ChatController() {
    }

    /**
     * Send a global message
     *
     * @param msg  message to send
     */
    public void sendGlobalMsg(Context context, String msg) {
        if (MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.wlanName) != null && connectionController.getSSID().contains("DIRECT-CONNECTION"))
            udpWlan.sendGlobalMsg(msg);
        if (MyNetworkInterface.getMyP2pNetworkInterface(MyNetworkInterface.p2pName) != null)
            udpP2p.sendGlobalMsg(msg);
        database.addGlobalMsg(msg, ConnectionController.myUser.getIdUser());
        Intent intent = new Intent(context, MessageListener.getIstance().getClass());
        intent.putExtra("intentType", "messageController");
        intent.putExtra("communicationType", "multicast");
        intent.putExtra("msg", msg);
        intent.putExtra("idUser", ConnectionController.myUser.getIdUser());
        intent.putExtra("username", ConnectionController.myUser.getUsername());
        intent.putExtra("idMessage",  database.getLastGlobalMessageId());
        intent.putExtra("data", LocalDateTime.now().toString());
        context.sendBroadcast(intent);
    }

    /**
     * Send a direct message
     *
     * @param msg         message to send
     * @param idReceiver  id of the person to send the message
     */
    public void sendTCPMsg(String msg, String idReceiver) {
        if (database.getSymmetricKey(idReceiver) != null) {
            tcp.sendMessage(msg, idReceiver);
        } else {
            tcp.handShake(idReceiver, database.getPublicKey(idReceiver), msg);
        }

    }

    /**
     * Retry to send a direct message
     *
     * @param msg         message to send
     * @param idReceiver  id of the person to send the message
     */
    public void reSendTCPMsg(String msg, String idReceiver) {
        tcp.reSendMessage(msg, idReceiver);
    }

    public void share(String numberOrNickAndType, String idReceiver) {
        tcp.sendShare(numberOrNickAndType, idReceiver);
    }

    /**
     * Send a direct image
     *
     * @param path        path of the image to send
     * @param idReceiver  id of the person to send the image
     */
    public void sendTCPPath(Paths path, String idReceiver) {
        //DA FARE PER BENE
        /*tcp.sendMessage(path.toString(), idReceiver);//encoding to byte DA FARE
        database.addMsg(path, idReceiver, ConnectionController.myUser.getIdUser(), idReceiver);*/

    }
}
