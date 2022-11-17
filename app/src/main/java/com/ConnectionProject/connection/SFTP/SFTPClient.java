package com.ConnectionProject.connection.SFTP;

import android.os.AsyncTask;

public class SFTPClient extends AsyncTask<String, Void, Integer> {


    protected Integer doInBackground(String... params) {
        //TODO CONNECTION TO SERVER, SEND STRING @ID£€@IMAGE64 CRIPTATI CON CHIAVE SIMMETRICA
        return 0;
    }

    protected void onPostExecute(SFTPClient result) {
        System.out.println("[SFTP-CLIENT] Connection finished with result: " + result);
        //Where ftpClient is a instance variable in the main activity
    }
}
