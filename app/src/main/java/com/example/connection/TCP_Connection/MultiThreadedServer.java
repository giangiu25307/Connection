package com.example.connection.TCP_Connection;

import android.os.AsyncTask;

import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Database;
import com.example.connection.View.Connection;
import com.example.connection.localization.LocalizationController;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class MultiThreadedServer extends AsyncTask<Void, Void, Void> {
    protected int serverPort = 50000;
    protected ServerSocket serverSocket = null;
    protected boolean isStopped = false;
    protected Thread runningThread = null;
    private String receive;
    private Database database;
    private Connection connection;
    private ConnectionController connectionController;
     LocalizationController localizationController;

    public MultiThreadedServer(int port, Database database, Connection connection, ConnectionController connectionController, LocalizationController localizationController) throws NoSuchAlgorithmException, KeyManagementException {
        this.serverPort = port;
        this.database=database;
        receive="";
        this.connection=connection;
        this.connectionController = connectionController;
        this.localizationController= localizationController;
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



    @Override
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
            new Thread(new WorkerRunnable(clientSocket,database,connection,connectionController,localizationController)).start();
        }
        System.out.println("Server Stopped.");
        return null;
    }
    public void test(){
        try {
            SSLContext sslContext = null;
            try {
                sslContext = getSSLContext();
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.serverSocket = sslContext.getServerSocketFactory().createServerSocket(serverPort);
            while (true) {
                try {
                    SSLSocket c = (SSLSocket) this.serverSocket.accept();
                InputStream input = c.getInputStream();
                    DataInputStream dIn = new DataInputStream(c.getInputStream());
                    int length = dIn.readInt();                    // read length of incoming message
                    if (length > 0) {
                        byte[] message = new byte[length];
                        dIn.readFully(message, 0, message.length);
                        System.out.println( message);
                    }
                    //new Thread(new WorkerRunnable(clientSocket,database,connection,connectionController,localizationController)).start();
                } catch (IOException e) {

                    throw new RuntimeException(
                            "Error accepting client connection", e);
                }

            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 443", e);
        }

    }
    private SSLContext getSSLContext() throws Exception
    {
        SSLContext sslContext = null;

        TrustManager[] trustManager = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
        } };


        //Create an SSLContext
        sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(null, trustManager, null);

        return sslContext;
    }
}