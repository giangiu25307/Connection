package com.ConnectionProject.connection.Model;

import android.content.Context;

import com.ConnectionProject.connection.Database.Database;
import com.ConnectionProject.connection.TCP_Connection.Encryption;

public class UtilsObject {

    private Database database;
    private Context context;
    private Encryption encryption;

    public Database getDatabase() {
        return database;
    }

    public void setDatabase(Database database) {
        this.database = database;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Encryption getEncryption() {
        return encryption;
    }

    public void setEncryption(Encryption encryption) {
        this.encryption = encryption;
    }
}
