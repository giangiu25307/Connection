package com.example.connection.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.io.File;
import java.nio.file.Paths;

public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "Connection";
    Context context;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        File f=new File(context.getFilesDir().toString()+"/connection.db");
        if(f.exists()){
            db.openOrCreateDatabase(context.getFilesDir().toString()+"/connection.db",null);
            String query="DELETE FROM IP";
            db.execSQL(query);
        }else{
            db.openOrCreateDatabase(context.getFilesDir().toString()+"/connection.db",null);
            String CREATE_USER_TABLE = "CREATE TABLE USER ( "
                    + "id_user INTEGER PRIMARY KEY AUTOINCREMENT" + ", "
                    + "number INTEGER NOT NULL" + ", "
                    + "name TEXT NOT NULL" + ", "
                    + "surname TEXT NOT NULL" + ", "
                    + "age INTEGER NOT NULL" + ", "
                    + "gender TEXT NOT NULL" + ", "
                    + "mail TEXT NOT NULL" + ", "
                    + "username TEXT NOT NULL" + ", "
                    + "password TEXT NOT NULL" + ", "
                    + "country TEXT NOT NULL" + ", "
                    + "city TEXT NOT NULL"
                    + ")";

            String CREATE_MESSAGE_TABLE = "CREATE TABLE MESSAGE ( "
                    + "id_chat INTEGER PRIMARY KEY AUTOINCREMENT" + ", "
                    + "msg TEXT" + ", "
                    + "path TEXT"
                    + ")";

            String CREATE_CHAT_TABLE = "CREATE TABLE CHAT ( "
                    + "id_chat INTEGER PRIMARY KEY AUTOINCREMENT" + ", "
                    + "id_sender INTEGER NOT NULL" + ", "
                    + "id_receiver INTEGER NOT NULL" + ","
                    + "PRIMARY KEY (id_chat)" + ","
                    + "FOREIGN KEY (id_chat) REFERENCES MESSAGE (id_chat)"
                    + ")";

            String CREATE_IP_TABLE = "CREATE TABLE MESSAGE ( "
                    + "id_user INTEGER NOT NULL" + ", "
                    + "ip TEXT NOT NULL"
                    + ")";

            db.execSQL(CREATE_USER_TABLE);
            db.execSQL(CREATE_MESSAGE_TABLE);
            db.execSQL(CREATE_CHAT_TABLE);
            db.execSQL(CREATE_IP_TABLE);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String DROP_TABLE = "DROP TABLE IF EXISTS USER";
        db.execSQL(DROP_TABLE);
        DROP_TABLE = "DROP TABLE IF EXISTS CHAT";
        db.execSQL(DROP_TABLE);
        DROP_TABLE = "DROP TABLE IF EXISTS MESSAGE";
        db.execSQL(DROP_TABLE);
        onCreate(db);

    }

    //CRUD OPERATIONS

    public void addMsg(String msg, int idSender, int idReceiver) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues chatValues = new ContentValues();
        chatValues.put("id_sender", idSender);
        chatValues.put("id_receiver", idReceiver);
        ContentValues msgValues = new ContentValues();
        msgValues.put("msg", msg);

        db.insert("MESSAGE", null, msgValues);
        db.insert("CHAT", null, chatValues);
        db.close();
    }

    public void addMsg(Paths paths, int idSender, int idReceiver) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues chatValues = new ContentValues();
        chatValues.put("id_sender", idSender);
        chatValues.put("id_receiver", idReceiver);
        ContentValues msgValues = new ContentValues();
        msgValues.put("path", paths.toString());

        db.insert("MESSAGE", null, msgValues);
        db.insert("CHAT", null, chatValues);
        db.close();
    }

    public String getMsg(int idSender, int idReceiver) {//questo metodo non dovrà ritornare nulla ma mostrare con due appositi metodi o la chat o l'immagine al ricevitore del seguente messaggio
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT msg,path" +
                "FROM CHAT C INNER JOIN MESSAGE M ON M.id_chat = C.id_chat" +
                "WHERE C.id_sender = '" + idSender + "' and C.id_receiver = '" + idReceiver + "'";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        if (!c.getString(1).equals("")) return c.getString(1);//ritorno un messaggio
        else return c.getString(2);//ritorno una path per esempio per un immagine
    }

}