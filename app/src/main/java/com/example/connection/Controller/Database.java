package com.example.connection.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.nio.file.Paths;

public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "Connection";
    Context context;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS " + Task.TaskEntry.USER + " ( "
                + Task.TaskEntry.ID_USER + " INTEGER PRIMARY KEY AUTOINCREMENT" + ", "
                + Task.TaskEntry.NUMBER + " INTEGER NOT NULL" + ", "
                + Task.TaskEntry.NAME + " TEXT NOT NULL" + ", "
                + Task.TaskEntry.SURNAME + " TEXT NOT NULL" + ", "
                + Task.TaskEntry.AGE + " INTEGER NOT NULL" + ", "
                + Task.TaskEntry.GENDER + " TEXT NOT NULL" + ", "
                + Task.TaskEntry.MAIL + " TEXT NOT NULL" + ", "
                + Task.TaskEntry.USERNAME + " TEXT NOT NULL" + ", "
                + Task.TaskEntry.PASSWORD + " TEXT NOT NULL" + ", "
                + Task.TaskEntry.COUNTRY + " TEXT NOT NULL" + ", "
                + Task.TaskEntry.PROFILE_PIC + " TEXT NOT NULL" + ", "
                + Task.TaskEntry.CITY + " TEXT NOT NULL"
                + ")";

        String CREATE_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS "+ Task.TaskEntry.MESSAGE +" ( "
                + Task.TaskEntry.ID_CHAT+ " INTEGER PRIMARY KEY AUTOINCREMENT" + ", "
                + Task.TaskEntry.ID_SENDER+ " INTEGER NOT NULL" + ", "
                + Task.TaskEntry.MSG+" TEXT" + ", "
                + Task.TaskEntry.PATH+" TEXT"
                + ")";

        String CREATE_GROUP_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS "+ Task.TaskEntry.GROUP_MESSAGE +" ( "
                + Task.TaskEntry.ID_GROUP+ " INTEGER PRIMARY KEY AUTOINCREMENT" + ", "
                + Task.TaskEntry.ID_SENDER+ " INTEGER NOT NULL" + ", "
                + Task.TaskEntry.MSG+" TEXT" + ", "
                + Task.TaskEntry.PATH+" TEXT"
                + ")";

        String CREATE_CHAT_TABLE = "CREATE TABLE IF NOT EXISTS "+ Task.TaskEntry.CHAT +" ( "
                + Task.TaskEntry.ID_CHAT+ " INTEGER PRIMARY KEY AUTOINCREMENT" + ", "
                + Task.TaskEntry.ID_TALKER1+ " INTEGER NOT NULL" + ", "
                + Task.TaskEntry.ID_TALKER2+ " INTEGER NOT NULL"
                + ")";

        String CREATE_GROUPS_TABLE = "CREATE TABLE IF NOT EXISTS "+ Task.TaskEntry.GROUPS +" ( "
                + Task.TaskEntry.ID_GROUP+ " INTEGER PRIMARY KEY AUTOINCREMENT" + ", "
                + Task.TaskEntry.ID_USER+ " INTEGER NOT NULL" + ", "
                + Task.TaskEntry.IP+ " INTEGER NOT NULL"
                + ")";

        String CREATE_GLOBAL_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS "+ Task.TaskEntry.GLOBAL_MESSAGE +" ( "
                + Task.TaskEntry.ID_SENDER+ " INTEGER PRIMARY KEY AUTOINCREMENT" + ", "
                + Task.TaskEntry.MSG+ " INTEGER NOT NULL"
                + ")";

        String CREATE_IP_TABLE = "CREATE TABLE IF NOT EXISTS "+ Task.TaskEntry.NETWORK_IPS +" ( "
                + Task.TaskEntry.ID_USER+" INTEGER NOT NULL" + ", "
                + Task.TaskEntry.IP+ " TEXT"
                + ")";

        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_MESSAGE_TABLE);
        db.execSQL(CREATE_CHAT_TABLE);
        db.execSQL(CREATE_IP_TABLE);
        db.execSQL(CREATE_GROUPS_TABLE);
        db.execSQL(CREATE_GROUP_MESSAGE_TABLE);
        db.execSQL(CREATE_GLOBAL_MESSAGE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        /*String DROP_TABLE = "DROP TABLE IF EXISTS USER";
        db.execSQL(DROP_TABLE);
        DROP_TABLE = "DROP TABLE IF EXISTS CHAT";
        db.execSQL(DROP_TABLE);
        DROP_TABLE = "DROP TABLE IF EXISTS MESSAGE";
        db.execSQL(DROP_TABLE);*/
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

    public String getMsg(int idTalker1, int idTalker2) {//questo metodo non dovrà ritornare nulla ma mostrare con due appositi metodi o la chat o l'immagine al ricevitore del seguente messaggio
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT msg,path,id_sender" +
                " FROM MESSAGE" +
                " WHERE id_chat = (SELECT id_chat" +
                " FROM CHAT" +
                " WHERE id_talker1='"+idTalker1+"' and id_talker2='"+idTalker2+"' )";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        if (!c.getString(2).equals("")) return c.getString(2);//ritorno un messaggio
        else return c.getString(3);//ritorno una path per esempio per un immagine
    }

}