package com.example.connection.TCP_Connection;

import com.example.connection.Controller.ChatController;
import com.example.connection.TCP_Connection.WorkerRunnable;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MultiThreadedServer implements Runnable {
    protected int serverPort = 50000;
    protected ServerSocket serverSocket = null;
    protected boolean isStopped = false;
    protected Thread runningThread = null;
    String receive;

    public MultiThreadedServer(int port) {
        this.serverPort = port;
        receive="";
    }

    public void run() {
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        while (!isStopped()) {
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
                receive=clientSocket.getInputStream().toString();
                //richiamo metodo di altra classe che prende l'id e lo scrive in un file per l'associazione dei messaggi
            } catch (IOException e) {
                if (isStopped()) {
                    System.out.println("Server Stopped.");
                    return;
                }
                throw new RuntimeException(
                        "Error accepting client connection", e);
            }
            new Thread(new WorkerRunnable(clientSocket)).start();
        }
        System.out.println("Server Stopped.");
    }

    public void receiveMsg(){}//creare scrittura su file della chat tenendo da conto gli id

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
            this.serverSocket = new ServerSocket(this.serverPort);
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 8080", e);
        }
    }

}