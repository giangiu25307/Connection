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
                + Task.TaskEntry.ID_USER + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Task.TaskEntry.NUMBER + " INTEGER NOT NULL, "
                + Task.TaskEntry.NAME + " TEXT NOT NULL, "
                + Task.TaskEntry.SURNAME + " TEXT NOT NULL, "
                + Task.TaskEntry.AGE + " INTEGER NOT NULL, "
                + Task.TaskEntry.GENDER + " TEXT NOT NULL, "
                + Task.TaskEntry.MAIL + " TEXT NOT NULL, "
                + Task.TaskEntry.USERNAME + " TEXT NOT NULL, "
                + Task.TaskEntry.PASSWORD + " TEXT NOT NULL, "
                + Task.TaskEntry.COUNTRY + " TEXT NOT NULL, "
                + Task.TaskEntry.PROFILE_PIC + " TEXT NOT NULL, "
                + Task.TaskEntry.CITY + " TEXT NOT NULL"
                + ")";

        String CREATE_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS " + Task.TaskEntry.MESSAGE + " ( "
                + Task.TaskEntry.ID_CHAT + " INTEGER NOT NULL, "
                + Task.TaskEntry.ID_SENDER + " INTEGER NOT NULL, "
                + Task.TaskEntry.MSG + " TEXT, "
                + Task.TaskEntry.PATH + " TEXT, "
                + Task.TaskEntry.DATE + " DATETIME, "
                + "FOREIGN KEY (" + Task.TaskEntry.ID_CHAT + ") REFERENCES " + Task.TaskEntry.CHAT + "(" + Task.TaskEntry.ID_CHAT + ") ON DELETE CASCADE"
                + ")";

        String CREATE_GROUP_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS " + Task.TaskEntry.GROUP_MESSAGE + " ( "
                + Task.TaskEntry.ID_GROUP + " INTEGER NOT NULL, "
                + Task.TaskEntry.ID_SENDER + " INTEGER NOT NULL, "
                + Task.TaskEntry.MSG + " TEXT, "
                + Task.TaskEntry.PATH + " TEXT,"
                + Task.TaskEntry.DATE + " DATETIME, "
                + "FOREIGN KEY (" + Task.TaskEntry.ID_GROUP + ") REFERENCES " + Task.TaskEntry.CHAT + "(" + Task.TaskEntry.ID_GROUP + ") ON DELETE CASCADE"
                + ")";

        String CREATE_CHAT_TABLE = "CREATE TABLE IF NOT EXISTS " + Task.TaskEntry.CHAT + " ( "
                + Task.TaskEntry.ID_CHAT + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Task.TaskEntry.ID_TALKER1 + " INTEGER NOT NULL, "
                + Task.TaskEntry.ID_TALKER2 + " INTEGER NOT NULL,"
                + Task.TaskEntry.LAST_MESSAGE + "TEXT,"
                + Task.TaskEntry.NAME + "TEXT"
                + ")";

        String CREATE_USER_GROUP_TABLE = "CREATE TABLE IF NOT EXISTS " + Task.TaskEntry.USER_GROUPS + " ( "
                + Task.TaskEntry.ID_GROUP + " INTEGER NOT NULL, "
                + Task.TaskEntry.ID_USER + " INTEGER NOT NULL, "
                + Task.TaskEntry.NAME + "TEXT,"
                + "FOREIGN KEY (" + Task.TaskEntry.ID_USER + ") REFERENCES " + Task.TaskEntry.USER + "(" + Task.TaskEntry.ID_USER + ") ON DELETE CASCADE,"
                + "FOREIGN KEY (" + Task.TaskEntry.ID_GROUP + ") REFERENCES " + Task.TaskEntry.GROUPS + "(" + Task.TaskEntry.ID_GROUP + ") ON DELETE CASCADE"
                + ")";

        String CREATE_GROUPS_TABLE = "CREATE TABLE IF NOT EXISTS " + Task.TaskEntry.GROUPS + " ( "
                + Task.TaskEntry.ID_GROUP + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Task.TaskEntry.ID_USER + " INTEGER NOT NULL, "
                + Task.TaskEntry.IP + " INTEGER NOT NULL,"
                + Task.TaskEntry.LAST_MESSAGE + "TEXT"
                + ")";

        String CREATE_GLOBAL_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS " + Task.TaskEntry.GLOBAL_MESSAGE + " ( "
                + Task.TaskEntry.ID_SENDER + " INTEGER NOT NULL, "
                + Task.TaskEntry.MSG + " INTEGER NOT NULL,"
                + Task.TaskEntry.DATE + " DATETIME"
                + ")";

        String CREATE_IP_TABLE = "CREATE TABLE IF NOT EXISTS " + Task.TaskEntry.NETWORK_IPS + " ( "
                + Task.TaskEntry.ID_USER + " INTEGER NOT NULL, "
                + Task.TaskEntry.IP + " TEXT"
                + ")";

        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_MESSAGE_TABLE);
        db.execSQL(CREATE_CHAT_TABLE);
        db.execSQL(CREATE_IP_TABLE);
        db.execSQL(CREATE_GROUPS_TABLE);
        db.execSQL(CREATE_GROUP_MESSAGE_TABLE);
        db.execSQL(CREATE_GLOBAL_MESSAGE_TABLE);
        db.execSQL(CREATE_USER_GROUP_TABLE);
        db.close();
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

    //CHAT-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void addMsg(String msg, int idTalker1, int idTalker2, int idSender) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        int idChat = retrieveIdChat(idTalker1,idTalker2);
        msgValues.put(Task.TaskEntry.ID_CHAT,idChat);
        msgValues.put(Task.TaskEntry.ID_SENDER,idSender);
        msgValues.put(Task.TaskEntry.MSG, msg);
        db.insert(Task.TaskEntry.MESSAGE, null, msgValues);
        db.close();
        lastMessageChat(msg,idChat);
    }

    private void lastMessageChat(String msg, int idChat){
        SQLiteDatabase db = this.getWritableDatabase();
        String query="UPDATE "+ Task.TaskEntry.CHAT+
        " SET "+ Task.TaskEntry.LAST_MESSAGE+" = " +msg+
        " WHERE '"+ Task.TaskEntry.ID_CHAT+"' = '"+idChat+"'";
        db.execSQL(query);
        db.close();
    }

    public void addMsg(Paths paths, int idTalker1, int idTalker2, int idSender) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        int idChat = retrieveIdChat(idTalker1,idTalker2);
        msgValues.put(Task.TaskEntry.ID_CHAT,idChat);
        msgValues.put(Task.TaskEntry.ID_SENDER,idSender);
        msgValues.put(Task.TaskEntry.PATH, paths.toString());
        db.insert(Task.TaskEntry.MESSAGE, null, msgValues);
        db.close();
        lastMessageChat(paths.toString(),idChat);
    }

    private int retrieveIdChat(int idTalker1, int idTalker2) {
        int idChat = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT id_chat" +
                " FROM CHAT" +
                " WHERE id_talker1='" + idTalker1 + "' and id_talker2='" + idTalker2 + "'";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        idChat = c.getColumnIndex(Task.TaskEntry.ID_CHAT);
        return idChat;
    }

    public void createChat(int idTalker1, int idTalker2,String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues chatValues = new ContentValues();
        chatValues.put(Task.TaskEntry.ID_TALKER1, idTalker1);
        chatValues.put(Task.TaskEntry.ID_TALKER2, idTalker2);
        chatValues.put(Task.TaskEntry.NAME, name);
        db.insert(Task.TaskEntry.CHAT, null, chatValues);
        db.close();
    }

    public boolean checkChatExist(int idTalker1, int idTalker2) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT id_chat" +
                " FROM CHAT" +
                " WHERE id_talker1='" + idTalker1 + "' and id_talker2='" + idTalker2 + "'";
        Cursor c = db.rawQuery(query, null);
        db.close();
        if (c == null) {
            return false;
        }else{
            return true;
        }
    }

    public Cursor getAllMsg(int idTalker1, int idTalker2) {//questo metodo non dovrà ritornare nulla ma mostrare con due appositi metodi o la chat o l'immagine al ricevitore del seguente messaggio
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT id_sender,msg,path,date" +
                " FROM MESSAGE" +
                " WHERE id_chat = (SELECT id_chat" +
                " FROM CHAT" +
                " GROUP BY "+ Task.TaskEntry.DATE+
                " HAVING id_talker1='" + idTalker1 + "' and id_talker2='" + idTalker2 + "' " +
                ") ORDER BY DESC";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        return c;
    }

    public Cursor getAllChat(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " +Task.TaskEntry.ID_CHAT+","+ Task.TaskEntry.NAME+","+ Task.TaskEntry.LAST_MESSAGE+
                " FROM "+ Task.TaskEntry.CHAT;
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        db.close();
        return c;
    }

    //GRUPPI------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //duplicare i metodi sopra

}