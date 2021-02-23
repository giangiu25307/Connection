package com.example.connection.TCP_Connection;

import android.os.AsyncTask;

import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Database;
import com.example.connection.View.Connection;
import com.example.connection.View.HomeFragment;
import com.example.connection.localization.LocalizationController;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ServerSocketFactory;

public class MultiThreadedServer {
    protected int serverPort = 50000;
    protected ServerSocket serverSocket = null;
    protected boolean isStopped = false;
    private Database database;
    private Connection connection;
    private ConnectionController connectionController;
    private Encryption encryption;
    private TCP_Client tcp_client;
    //LocalizationController localizationController;
    ServerSocketFactory serverSocketFactory = ServerSocketFactory.getDefault();

    public MultiThreadedServer(Database database, Connection connection, ConnectionController connectionController, Encryption encryption, TCP_Client tcp_client/*, LocalizationController localizationController*/) {
        this.database = database;
        this.connection = connection;
        this.connectionController = connectionController;
        this.encryption = encryption;
        this.tcp_client = tcp_client;
        //this.localizationController= localizationController;
    }

    private boolean isStopped() {
        return this.isStopped;
    }

    //close the server socket --------------------------------------------------------------------------------------------------------------------------------
    public void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    //open the server socket --------------------------------------------------------------------------------------------------------------------------------
    public void openServerSocketP2p() {
        try {
            this.serverSocket = serverSocketFactory.createServerSocket(serverPort, 0, InetAddress.getByName("192.168.49.1"));
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port", e);
        }
    }

    public void openServerSocketWlan() {
        try {
            this.serverSocket = serverSocketFactory.createServerSocket(serverPort, 1, ConnectionController.myUser.getInetAddress());
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port", e);
        }
    }

    public void run() {
        while (!isStopped()) {
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if (isStopped()) {
                    System.out.println("Server Stopped.");
                    return;
                }
                throw new RuntimeException(
                        "Error accepting client connection", e);
            }
            new Thread(new WorkerRunnable(clientSocket, database, connection, connectionController,encryption,tcp_client/*,localizationController*/)).start();
        }
        System.out.println("Server Stopped.");
        return;
    }

    /*@Override
    protected Void doInBackground(Void... voids) {
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        while (!isStopped()) {
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if (isStopped()) {
                    System.out.println("Server Stopped.");
                    return null;
                }
                throw new RuntimeException(
                        "Error accepting client connection", e);
            }
            new Thread(new WorkerRunnable(clientSocket,database,connection,connectionController/*,localizationController)).start();
        }
        System.out.println("Server Stopped.");
        return null;
    }*/
}