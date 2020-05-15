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
    SQLiteDatabase db;
    Context context;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        db=database;
        db = this.getWritableDatabase();
        String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS " + Task.TaskEntry.USER + " ( "
                + Task.TaskEntry.ID_USER + " TEXT PRIMARY KEY, "
                + Task.TaskEntry.USERNAME + " TEXT NOT NULL, "
                + Task.TaskEntry.MAIL + " TEXT NOT NULL, "
                + Task.TaskEntry.GENDER + " TEXT NOT NULL, "
                + Task.TaskEntry.NAME + " TEXT NOT NULL, "
                + Task.TaskEntry.SURNAME + " TEXT NOT NULL, "
                + Task.TaskEntry.COUNTRY + " TEXT NOT NULL, "
                + Task.TaskEntry.CITY + " TEXT NOT NULL,"
                + Task.TaskEntry.NUMBER + " TEXT NOT NULL, "
                + Task.TaskEntry.AGE + " TEXT NOT NULL, "
                + Task.TaskEntry.PROFILE_PIC + " TEXT NOT NULL, "
                + Task.TaskEntry.IP + " TEXT"
                + ")";

        String CREATE_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS " + Task.TaskEntry.MESSAGE + " ( "
                + Task.TaskEntry.ID_CHAT + " TEXT NOT NULL, "
                + Task.TaskEntry.ID_SENDER + " TEXT NOT NULL, "
                + Task.TaskEntry.MSG + " TEXT, "
                + Task.TaskEntry.PATH + " TEXT, "
                + Task.TaskEntry.DATE + " DATETIME, "
                + "FOREIGN KEY (" + Task.TaskEntry.ID_CHAT + ") REFERENCES " + Task.TaskEntry.CHAT + "(" + Task.TaskEntry.ID_CHAT + ") ON DELETE CASCADE"
                + ")";



        String CREATE_CHAT_TABLE = "CREATE TABLE IF NOT EXISTS " + Task.TaskEntry.CHAT + " ( "
                + Task.TaskEntry.ID_CHAT + " TEXT PRIMARY KEY, "
                + Task.TaskEntry.LAST_MESSAGE + "TEXT,"
                + Task.TaskEntry.NAME + "TEXT,"
                + Task.TaskEntry.NOT_READ_MESSAGE + "INTEGER"
                + ")";

        String CREATE_GLOBAL_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS " + Task.TaskEntry.GLOBAL_MESSAGE + " ( "
                + Task.TaskEntry.ID_SENDER + " TEXT NOT NULL, "
                + Task.TaskEntry.MSG + " TEXT NOT NULL,"
                + Task.TaskEntry.DATE + " DATETIME"
                + Task.TaskEntry.LAST_MESSAGE + "TEXT"
                + Task.TaskEntry.NOT_READ_MESSAGE + "INTEGER"
                + ")";

        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_MESSAGE_TABLE);
        db.execSQL(CREATE_CHAT_TABLE);
        db.execSQL(CREATE_GLOBAL_MESSAGE_TABLE);

        /*String CREATE_GROUP_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS " + Task.TaskEntry.GROUP_MESSAGE + " ( "
                + Task.TaskEntry.ID_GROUP + " INTEGER NOT NULL, "
                + Task.TaskEntry.ID_SENDER + " INTEGER NOT NULL, "
                + Task.TaskEntry.MSG + " TEXT, "
                + Task.TaskEntry.PATH + " TEXT,"
                + Task.TaskEntry.DATE + " DATETIME, "
                + "FOREIGN KEY (" + Task.TaskEntry.ID_GROUP + ") REFERENCES " + Task.TaskEntry.CHAT + "(" + Task.TaskEntry.ID_GROUP + ") ON DELETE CASCADE"
                + ")";
        String CREATE_USER_GROUP_TABLE = "CREATE TABLE IF NOT EXISTS " + Task.TaskEntry.USERS_GROUP + " ( "
                + Task.TaskEntry.ID_GROUP + " INTEGER NOT NULL, "
                + Task.TaskEntry.ID_USER + " INTEGER NOT NULL, "
                + Task.TaskEntry.IP + "TEXT,"
                + "FOREIGN KEY (" + Task.TaskEntry.ID_USER + ") REFERENCES " + Task.TaskEntry.USER + "(" + Task.TaskEntry.ID_USER + ") ON DELETE CASCADE,"
                + "FOREIGN KEY (" + Task.TaskEntry.ID_GROUP + ") REFERENCES " + Task.TaskEntry.GROUPS + "(" + Task.TaskEntry.ID_GROUP + ") ON DELETE CASCADE"
                + ")";

        String CREATE_GROUPS_TABLE = "CREATE TABLE IF NOT EXISTS " + Task.TaskEntry.GROUPS + " ( "
                + Task.TaskEntry.ID_GROUP + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Task.TaskEntry.ID_USER + " INTEGER NOT NULL, "
                + Task.TaskEntry.GROUP_NAME + " TEXT NOT NULL,"
                + Task.TaskEntry.DATE + " DATETIME, "
                + Task.TaskEntry.LAST_MESSAGE + "TEXT"
                + Task.TaskEntry.NOT_READ_MESSAGE + "INTEGER"
                + ")";
        db.execSQL(CREATE_GROUPS_TABLE);
        db.execSQL(CREATE_GROUP_MESSAGE_TABLE);
        db.execSQL(CREATE_USER_GROUP_TABLE);*/
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
    public void onDestroy() {
        db.close();
    }
    //CHAT-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    public void addMsg(String msg, String idReceiver, String idSender) {
        ContentValues msgValues = new ContentValues();
        int idChat = retrieveIdChat(idReceiver);
        msgValues.put(Task.TaskEntry.ID_CHAT,idChat);
        msgValues.put(Task.TaskEntry.ID_SENDER,idSender);
        msgValues.put(Task.TaskEntry.MSG, msg);
        db.insert(Task.TaskEntry.MESSAGE, null, msgValues);
        lastMessageChat(msg,idChat);
    }

    public void addMsg(Paths paths, String idReceiver, String idSender) {
        ContentValues msgValues = new ContentValues();
        int idChat = retrieveIdChat(idReceiver);
        msgValues.put(Task.TaskEntry.ID_CHAT,idChat);
        msgValues.put(Task.TaskEntry.ID_SENDER,idSender);
        msgValues.put(Task.TaskEntry.PATH, paths.toString());
        db.insert(Task.TaskEntry.MESSAGE, null, msgValues);
        lastMessageChat(paths.toString(),idChat);
    }

    private void lastMessageChat(String msg, int idChat){
        String query="UPDATE "+ Task.TaskEntry.CHAT+
                " SET "+ Task.TaskEntry.LAST_MESSAGE+" = " +msg+
                " WHERE '"+ Task.TaskEntry.ID_CHAT+"' = '"+idChat+"'";
        db.execSQL(query);
    }

    private int retrieveIdChat(String idReceiver) {
        int idChat = 0;
        String query = "SELECT id_chat" +
                " FROM CHAT" +
                " WHERE id_receiver='" + idReceiver + "'";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        idChat = c.getColumnIndex(Task.TaskEntry.ID_CHAT);
        return idChat;
    }

    public void createChat(String idReceiver,String name) {
        ContentValues chatValues = new ContentValues();
        chatValues.put(Task.TaskEntry.ID_CHAT, idReceiver);
        chatValues.put(Task.TaskEntry.NAME, name);
        db.insert(Task.TaskEntry.CHAT, null, chatValues);
    }

    public boolean checkChatExist(String idReceiver) {
        String query = "SELECT id_chat" +
                " FROM CHAT" +
                " WHERE id_receiver='" + idReceiver + "'";
        Cursor c = db.rawQuery(query, null);
        if (c == null) {
            return false;
        }else{
            return true;
        }
    }

    public Cursor getAllMsg(String idReceiver) {
        String query = "SELECT id_sender,msg,path,date" +
                " FROM MESSAGE" +
                " WHERE id_chat = (SELECT id_chat" +
                " FROM CHAT" +
                " GROUP BY "+ Task.TaskEntry.DATE+
                " HAVING id_receiver='" + idReceiver + "'" +
                ") ORDER BY DESC";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public Cursor getAllChat(){
        String query = "SELECT " +Task.TaskEntry.ID_CHAT+","+ Task.TaskEntry.NAME+","+ Task.TaskEntry.DATE+","+ Task.TaskEntry.LAST_MESSAGE+
                " FROM "+ Task.TaskEntry.CHAT;
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }


    //globale
    public void addGlobalMsg(String msg, String idSender) {
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.ID_SENDER,idSender);
        msgValues.put(Task.TaskEntry.MSG, msg);
        db.insert(Task.TaskEntry.GLOBAL_MESSAGE, null, msgValues);
        lastGlobalMessageChat(msg,idSender);
    }

    public Cursor getGlobalMessage(){
        String query = "SELECT *" +
                " FROM "+ Task.TaskEntry.GLOBAL_MESSAGE;
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    private void lastGlobalMessageChat(String msg, String idSender){
        String query="UPDATE "+ Task.TaskEntry.GLOBAL_MESSAGE+
                " SET "+ Task.TaskEntry.LAST_MESSAGE+" = " +msg+
                " WHERE '"+ Task.TaskEntry.ID_CHAT+"' = '"+idSender+"'";
        db.execSQL(query);
    }

    //USER
    public String[] getMyInformation(){
        String user[]=new String[11];
        String query = "SELECT *"+
                " FROM "+ Task.TaskEntry.USER;
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        user[0]=c.getString(0);
        user[1]=c.getString(1);
        user[2]=c.getString(2);
        user[3]=c.getString(3);
        user[4]=c.getString(4);
        user[5]=c.getString(5);
        user[6]=c.getString(6);
        user[7]=c.getString(7);
        user[8]=c.getString(9);
        user[9]=c.getString(10);
        user[10]=c.getString(11);
        return user;
    }

    public void addUser(String idUser,String inetAddress,String username,String mail,String gender,String name,String surname,String country,String city,String age,String profilePic){
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.USERNAME,username);
        msgValues.put(Task.TaskEntry.ID_USER,idUser);
        msgValues.put(Task.TaskEntry.MAIL,mail);
        msgValues.put(Task.TaskEntry.GENDER,gender);
        msgValues.put(Task.TaskEntry.NAME,name);
        msgValues.put(Task.TaskEntry.SURNAME,surname);
        msgValues.put(Task.TaskEntry.COUNTRY,country);
        msgValues.put(Task.TaskEntry.CITY,city);
        msgValues.put(Task.TaskEntry.AGE,age);
        msgValues.put(Task.TaskEntry.IP,inetAddress);
        msgValues.put(Task.TaskEntry.PROFILE_PIC,profilePic);
        db.insert(Task.TaskEntry.USER, null, msgValues);
        ContentValues ipValues = new ContentValues();
    }

    public void addNumber(String number,String idUser){
        String query="UPDATE "+ Task.TaskEntry.USER+
                " SET "+ Task.TaskEntry.NUMBER+" = " +number+
                " WHERE '"+ Task.TaskEntry.ID_USER+"' = '"+idUser+"'";
        db.execSQL(query);
    }

    public Cursor  getAllUsers(){
        String query = "SELECT *" +
                " FROM "+ Task.TaskEntry.USER;
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public String findIp(String id_user){
        String query = "SELECT " + Task.TaskEntry.IP+
                " FROM " + Task.TaskEntry.USER+
                " WHERE id_user ='" +id_user+ "'" ;
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c.getString(0);
    }
    public String findId_user(String ip){

        String query = "SELECT " + Task.TaskEntry.ID_USER+
                " FROM " +Task.TaskEntry.USER+
                " WHERE ip ='" +ip+ "'" ;
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c.getString(1);
    }

    public void deleteUser(String idUser){
        String query = "SELECT *" +
                " FROM "+ Task.TaskEntry.CHAT+
                " WHERE "+ Task.TaskEntry.ID_CHAT +"="+idUser;
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            query="UPDATE "+ Task.TaskEntry.USER+
                    " SET "+ Task.TaskEntry.IP+" = NULL" +
                    " WHERE '"+ Task.TaskEntry.ID_USER+"' = '"+idUser+"'";
            db.execSQL(query);
        } else {
            query = " DELETE FROM " + Task.TaskEntry.USER
                    + " WHERE '" + Task.TaskEntry.ID_USER + "' = '" + idUser + "'";
            db.execSQL(query);
        }
    }

    public void leave(){
        String query="UPDATE "+ Task.TaskEntry.USER+
                " SET "+ Task.TaskEntry.IP+" = NULL";
        db.execSQL(query);
    }

    /*GRUPPI------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //duplicare i metodi sopra
    public void addGroupMsg(String msg, int idSender,int idgroup) {
        ContentValues msgValues = new ContentValues();
        //int idgroup = retrieveIdGroup(GroupName);
        msgValues.put(Task.TaskEntry.ID_GROUP,idgroup);
        msgValues.put(Task.TaskEntry.ID_SENDER,idSender);
        msgValues.put(Task.TaskEntry.MSG, msg);
        db.insert(Task.TaskEntry.GROUP_MESSAGE, null, msgValues);
        lastMessageChat(msg,idgroup);
    }

    private void lastMessageGroup(String msg, int idgroup){
        String query="UPDATE "+ Task.TaskEntry.GROUPS+
                " SET "+ Task.TaskEntry.LAST_MESSAGE+" = " +msg+
                " WHERE '"+ Task.TaskEntry.ID_GROUP+"' = '"+idgroup+"'";
        db.execSQL(query);
    }

    public void addGroupMsg(Paths paths, int idSender,String GroupName) {
        ContentValues msgValues = new ContentValues();
        int idgroup = retrieveIdGroup(GroupName);
        msgValues.put(Task.TaskEntry.ID_GROUP,idgroup);
        msgValues.put(Task.TaskEntry.ID_SENDER,idSender);
        msgValues.put(Task.TaskEntry.PATH, paths.toString());
        db.insert(Task.TaskEntry.GROUP_MESSAGE, null, msgValues);
        lastMessageGroup(paths.toString(),idgroup);
    }

    private int retrieveIdGroup(String Group_Name) {
        String query = "SELECT id_group" +
                " FROM GROUPS" +
                " WHERE GROUP_NAME='" + Group_Name + "'";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        int idGroup = c.getColumnIndex(Task.TaskEntry.ID_GROUP);
        return idGroup;
    }

    public void createGroup(String id_users[],int idgroup,String name) {
        ContentValues chatValues = new ContentValues();
        for (int i=0;i<id_users.length;i++) {
            chatValues.put(Task.TaskEntry.ID_GROUP, idgroup);
            chatValues.put(Task.TaskEntry.ID_USER, id_users[i]);
            chatValues.put(Task.TaskEntry.IP, findIp(id_users[i]));
            db.insert(Task.TaskEntry.USERS_GROUP, null, chatValues);
        }
        chatValues.put(Task.TaskEntry.ID_GROUP, idgroup);
        chatValues.put(Task.TaskEntry.GROUP_NAME, name);
        db.insert(Task.TaskEntry.GROUPS, null, chatValues);
    }

    public Cursor getAllGroupMsg(int idGroup) {
        String query = "SELECT id_sender,msg,path,date" +
                " FROM GROUP_MESSAGE" +
                " WHERE id_group = (SELECT id_group" +
                " FROM GROUPS" +
                " GROUP BY "+ Task.TaskEntry.DATE+
                " HAVING id_group='" + idGroup + "'" +
                ") ORDER BY DESC";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public Cursor getAllGroupChat(){
        String query = "SELECT " +Task.TaskEntry.ID_GROUP+","+ Task.TaskEntry.GROUP_NAME+","+ Task.TaskEntry.DATE+","+ Task.TaskEntry.LAST_MESSAGE+
                " FROM "+ Task.TaskEntry.GROUPS;
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }
    public boolean checkGroupId(String id){
        String query = "SELECT 1" +
                " FROM "+ Task.TaskEntry.GROUPS+
                " WHERE id_group ='"+id+"'";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            return true;
        }else{
            return false;
        }

    }*/
}