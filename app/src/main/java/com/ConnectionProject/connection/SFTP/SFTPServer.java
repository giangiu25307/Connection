package com.ConnectionProject.connection.SFTP;

import android.content.Context;
import android.os.AsyncTask;

import com.ConnectionProject.connection.Database.Database;
import com.ConnectionProject.connection.TCP_Connection.Encryption;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SFTPServer extends AsyncTask<String, Void, String> {

    private ExecutorService executor = Executors.newCachedThreadPool();
    private Database database;
    private Encryption encryption;
    private Context context;

    public void setDatabase(Database database) {
        this.database = database;
    }

    public void setEncryption(Encryption encryption) {
        this.encryption = encryption;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... params) {
        ServerSocket s = null;
        Socket incoming = null;

        try{
            s = new ServerSocket(41000, 0, InetAddress.getByName("::"));
            System.out.println("[FTP-SERVER] server started listening");
            while(true){
                incoming = s.accept();
                executor.execute(new ServerPI(incoming,database,encryption,context.getApplicationContext()));
            }
        }
        catch(Exception e){
            System.out.println(e.toString());
            e.printStackTrace();
        }
        finally{
            try
            {
                if(incoming != null)incoming.close();
            }
            catch(IOException ignore)
            {
                //ignore
            }

            try
            {
                if (s!= null)
                {
                    System.out.println("[FTP-SERVER] server closed");
                    s.close();
                }
            }
            catch(IOException ignore)
            {
                //ignore
            }
        }

        return "Executed";
    }

    @Override
    protected void onPostExecute(String result) {
    }

    @Override
    protected void onPreExecute() {
    }

    @Override
    protected void onProgressUpdate(Void... values) {
    }
}
