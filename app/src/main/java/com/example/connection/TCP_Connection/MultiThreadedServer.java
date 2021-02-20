package com.example.connection.TCP_Connection;

import android.os.AsyncTask;

import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Database;
import com.example.connection.View.Connection;
import com.example.connection.localization.LocalizationController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ServerSocketFactory;

public class MultiThreadedServer extends AsyncTask<Void, Void, Void> {
    protected int serverPort = 50000;
    protected ServerSocket serverSocket = null;
    protected boolean isStopped = false;
    protected Thread runningThread = null;
    private String receive;
    private Database database;
    private Connection connection;
    private ConnectionController connectionController;
    //LocalizationController localizationController;
    ServerSocketFactory serverSocketFactory= ServerSocketFactory.getDefault();

    public MultiThreadedServer(int port, Database database, Connection connection, ConnectionController connectionController/*, LocalizationController localizationController*/) throws NoSuchAlgorithmException, KeyManagementException {
        this.serverPort = port;
        this.database=database;
        receive="";
        this.connection=connection;
        this.connectionController = connectionController;
        //this.localizationController= localizationController;
    }

    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    //close the server socket --------------------------------------------------------------------------------------------------------------------------------
    public synchronized void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    //open the server socket --------------------------------------------------------------------------------------------------------------------------------
        public  void openServerSocket() {
        try {
            this.serverSocket= serverSocketFactory.createServerSocket(serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port", e);
        }
    }

    @Override
    protected Void doInBackground(Void... voids) {
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
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
            new Thread(new WorkerRunnable(clientSocket,database,connection,connectionController/*,localizationController*/)).start();
        }
        System.out.println("Server Stopped.");
        return null;
    }

    public void test(){
        openServerSocket();
        while (!isStopped()) {
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if (isStopped()) {
                    System.out.println("Server Stopped.");
                    return ;
                }
                throw new RuntimeException(
                        "Error accepting client connection", e);
            }
            new Thread(new WorkerRunnable(clientSocket,database,connection,connectionController/*,localizationController*/)).start();
        }
        System.out.println("Server Stopped.");
        return ;
    }
}