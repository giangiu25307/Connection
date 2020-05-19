package com.example.connection.TCP_Connection;

import android.os.AsyncTask;

import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Database;
import com.example.connection.View.Connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

public class MultiThreadedServer extends AsyncTask<Void, Void, Void> {
    protected int serverPort = 50000;
    protected SSLServerSocket serverSocket = null;
    ServerSocketFactory f = SSLServerSocketFactory.getDefault();
    protected boolean isStopped = false;
    protected Thread runningThread = null;
    private String receive;
    private Database database;
    private Connection connection;
    private ConnectionController connectionController;
     SSLContext sslContext=null;

    public MultiThreadedServer(int port,Database database, Connection connection,ConnectionController connectionController) throws NoSuchAlgorithmException, KeyManagementException {
        sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(null, null, null);
        this.serverPort = port;
        this.database=database;
        receive="";
        this.connection=connection;
        this.connectionController = connectionController;
    }

    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket =(SSLServerSocket) sslContext.getServerSocketFactory().createServerSocket(serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 443", e);
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
            new Thread(new WorkerRunnable(clientSocket,database,connection,connectionController)).start();
        }
        System.out.println("Server Stopped.");
        return null;
    }
}