package com.example.connection.Controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.nio.file.Paths;
import java.time.LocalDateTime;

public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Connection";
    SQLiteDatabase db=this.getWritableDatabase();
    Context context;

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS " + Task.TaskEntry.USER + " ( "
                + Task.TaskEntry.ID_USER + " TEXT PRIMARY KEY, "
                + Task.TaskEntry.USERNAME + " TEXT NOT NULL, "
                + Task.TaskEntry.MAIL + " TEXT NOT NULL, "
                + Task.TaskEntry.GENDER + " TEXT NOT NULL, "
                + Task.TaskEntry.NAME + " TEXT NOT NULL, "
                + Task.TaskEntry.SURNAME + " TEXT NOT NULL, "
                + Task.TaskEntry.COUNTRY + " TEXT NOT NULL, "
                + Task.TaskEntry.CITY + " TEXT NOT NULL,"
                + Task.TaskEntry.NUMBER + " TEXT  DEFAULT 0, "
                + Task.TaskEntry.BIRTH + " TEXT NOT NULL, "
                + Task.TaskEntry.PROFILE_PIC + " TEXT NOT NULL, "
                + Task.TaskEntry.PUBLIC_KEY + " TEXT, "
                + Task.TaskEntry.IP + " TEXT, "
                + Task.TaskEntry.ACCEPT + " TEXT, "
                + Task.TaskEntry.MESSAGES_ACCEPTED + " TEXT "
                + ")";

        String CREATE_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS " + Task.TaskEntry.MESSAGE + " ( "
                + Task.TaskEntry.ID_CHAT + " TEXT NOT NULL, "
                + Task.TaskEntry.ID_SENDER + " TEXT NOT NULL, "
                + Task.TaskEntry.MSG + " TEXT, "
                + Task.TaskEntry.PATH + " TEXT, "
                + Task.TaskEntry.DATETIME + " TEXT, "
                + "FOREIGN KEY (" + Task.TaskEntry.ID_CHAT + ") REFERENCES " + Task.TaskEntry.CHAT + "(" + Task.TaskEntry.ID_CHAT + ") ON DELETE CASCADE"
                + ")";

        String CREATE_CHAT_TABLE = "CREATE TABLE IF NOT EXISTS " + Task.TaskEntry.CHAT + " ( "
                + Task.TaskEntry.ID_CHAT + " TEXT PRIMARY KEY, "
                + Task.TaskEntry.LAST_MESSAGE + " TEXT, "
                + Task.TaskEntry.NAME + " TEXT, "
                + Task.TaskEntry.DATETIME + " TEXT, "
                + Task.TaskEntry.NOT_READ_MESSAGE + " INTEGER, "
                + Task.TaskEntry.REQUEST + " TEXT, "
                + Task.TaskEntry.SYMMETRIC_KEY + "TEXT"
                + ")";

        String CREATE_GLOBAL_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS " + Task.TaskEntry.GLOBAL_MESSAGE + " ( "
                + Task.TaskEntry.ID_SENDER + " TEXT NOT NULL, "
                + Task.TaskEntry.MSG + " TEXT NOT NULL, "
                + Task.TaskEntry.DATETIME + " TEXT, "
                + Task.TaskEntry.LAST_MESSAGE + "TEXT, "
                + Task.TaskEntry.NOT_READ_MESSAGE + "INTEGER "
                + ")";

        String BACKGROUND_CHAT_IMAGES = "CREATE TABLE IF NOT EXISTS " + Task.TaskEntry.BACKGROUND_CHAT_IMAGES + " ( "
                + Task.TaskEntry.BACKGROUND_IMAGE + " TEXT NOT NULL "
                + ")";

        database.execSQL(CREATE_USER_TABLE);
        database.execSQL(CREATE_MESSAGE_TABLE);
        database.execSQL(CREATE_CHAT_TABLE);
        database.execSQL(CREATE_GLOBAL_MESSAGE_TABLE);
        database.execSQL(BACKGROUND_CHAT_IMAGES);


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
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON");
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
    public void addMsg(String msg, String idSender, String idChat) {
        if(idSender.equals("0"))setRequest(idChat,"false");
        db=this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.ID_CHAT, idChat);
        msgValues.put(Task.TaskEntry.ID_SENDER, idSender);
        msgValues.put(Task.TaskEntry.MSG, msg);
        msgValues.put(Task.TaskEntry.DATETIME, String.valueOf(LocalDateTime.now()));
        db.insert(Task.TaskEntry.MESSAGE, null, msgValues);
        lastMessageChat(msg,idChat);
    }
    public void addMsg(Paths paths, String idReceiver, String idSender,String idChat) {
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.ID_CHAT,idChat);
        msgValues.put(Task.TaskEntry.ID_SENDER,idSender);
        msgValues.put(Task.TaskEntry.PATH, paths.toString());
        msgValues.put(Task.TaskEntry.DATETIME, String.valueOf(LocalDateTime.now()));
        db.insert(Task.TaskEntry.MESSAGE, null, msgValues);
        lastMessageChat(paths.toString(),idChat);
    }

    private void lastMessageChat(String msg, String idChat){
        db=this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.LAST_MESSAGE, msg);
        msgValues.put(Task.TaskEntry.DATETIME, String.valueOf(LocalDateTime.now()));
        db.update(Task.TaskEntry.CHAT,msgValues,Task.TaskEntry.ID_CHAT+"="+idChat,null);
    }

    public Cursor getLastMessageChat(String idChat){
        String query="SELECT last_message, datetime " +
                " FROM "+ Task.TaskEntry.CHAT +
                " WHERE '"+ Task.TaskEntry.ID_CHAT + "' = '" + idChat + "'";
        Cursor cursor = db.rawQuery(query, null);
        return cursor;

    }

    private String retrieveIdChat(String idChat) {
        String query = "SELECT id_chat" +
                " FROM CHAT" +
                " WHERE id_chat='" + idChat + "'";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c.getString(0);

    }

    public void createChat(String idChat,String name) {
        db=this.getWritableDatabase();
        ContentValues chatValues = new ContentValues();
        chatValues.put(Task.TaskEntry.ID_CHAT, idChat);
        chatValues.put(Task.TaskEntry.NAME, name);
        chatValues.put(Task.TaskEntry.REQUEST,"true");
        db.insert(Task.TaskEntry.CHAT, null, chatValues);
    }

    public boolean checkChatExist(String idChat) {
        String query = "SELECT id_chat" +
                " FROM CHAT" +
                " WHERE id_chat='" + idChat + "'";
        Cursor c = db.rawQuery(query, null);
        return c != null;
    }

    public Cursor getAllMsg(String idChat) {
        String query = " SELECT id_sender,msg,path,datetime " +
                " FROM MESSAGE " +
                " WHERE id_chat = " + idChat +
                " ORDER BY " +  Task.TaskEntry.DATETIME + " ASC ";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public Cursor getAllChat(){
        String query = "SELECT " +Task.TaskEntry.ID_CHAT+", "+ Task.TaskEntry.NAME+", "+ Task.TaskEntry.LAST_MESSAGE+", "+Task.TaskEntry.DATETIME+
                " FROM "+ Task.TaskEntry.CHAT + " ORDER BY " + Task.TaskEntry.DATETIME + " DESC ";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public Cursor getAllRequestChat(){
        String query= " SELECT c.id_chat, c.name, c.last_message, c.datetime, u.birth, u.gender " +
                " FROM CHAT c INNER JOIN USER u on c.id_chat = u.id_user" +
                " WHERE c.request = 'true' AND u.message_accept = 'true'" +
                " ORDER BY c.datetime DESC";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public Cursor getAllNoRequestChat(){
        String query= " SELECT c.id_chat, c.name, c.last_message, c.datetime, u.birth, u.gender" +
                " FROM CHAT c INNER JOIN USER u on c.id_chat = u.id_user" +
                " WHERE c.request = 'false' AND u.message_accept = 'true'" +
                " ORDER BY c.datetime DESC";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public void setRequest(String id, String value){
        db=this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.REQUEST, value);
        db.update(Task.TaskEntry.CHAT,msgValues,Task.TaskEntry.ID_CHAT+"="+id,null);
    }

    public void setSymmetricKey(String symmetricKey){
        db=this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.SYMMETRIC_KEY, symmetricKey);
        db.update(Task.TaskEntry.CHAT,msgValues,null,null);
    }

    public Cursor getBacgroundImage(){
        db=this.getWritableDatabase();
        String query = "SELECT " + Task.TaskEntry.BACKGROUND_IMAGE +
                " FROM "+ Task.TaskEntry.BACKGROUND_CHAT_IMAGES ;
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToLast();
        }else{
            return null;
        }
        return c;
    }

    public void setBacgroundImage(String bgImage){
        db=this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.BACKGROUND_IMAGE,bgImage);
        db.insert(Task.TaskEntry.BACKGROUND_CHAT_IMAGES,null, msgValues);
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
        db=this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.LAST_MESSAGE, msg);
        db.update(Task.TaskEntry.GLOBAL_MESSAGE,msgValues, Task.TaskEntry.ID_SENDER+" = "+idSender,null);
    }

    //USER
    public String[] getMyInformation(){
        String[] user =new String[11];
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
        user[11]=c.getString(12);
        return user;
    }

    public Cursor getProfilePic(){
        db=this.getWritableDatabase();
        String query = "SELECT " + Task.TaskEntry.PROFILE_PIC +
                " FROM "+ Task.TaskEntry.USER +
                " WHERE "+ Task.TaskEntry.ID_USER + "= 0";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }else{
            return null;
        }
        return c;
    }

    public void setProfilePic(String profilePic){
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.PROFILE_PIC,profilePic);
        db.update(Task.TaskEntry.USER, msgValues,Task.TaskEntry.ID_USER+" = "+"0",null);
    }
    
    public void setPublicKey(String symmetricKey){
        db=this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.PUBLIC_KEY, symmetricKey);
        db.update(Task.TaskEntry.USER,msgValues,null,null);
    }

    public void addUser(String idUser,String inetAddress,String username,String mail,String gender,String name,String surname,String country,String city,String birth,String profilePic,String publicKey){
        db=this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.ID_USER,idUser);
        msgValues.put(Task.TaskEntry.USERNAME,username);
        msgValues.put(Task.TaskEntry.MAIL,mail);
        msgValues.put(Task.TaskEntry.GENDER,gender);
        msgValues.put(Task.TaskEntry.NAME,name);
        msgValues.put(Task.TaskEntry.SURNAME,surname);
        msgValues.put(Task.TaskEntry.COUNTRY,country);
        msgValues.put(Task.TaskEntry.CITY,city);
        msgValues.put(Task.TaskEntry.BIRTH,birth);
        msgValues.put(Task.TaskEntry.IP,inetAddress);
        msgValues.put(Task.TaskEntry.PROFILE_PIC,profilePic);
        msgValues.put(Task.TaskEntry.MESSAGES_ACCEPTED,"true");
        msgValues.put(Task.TaskEntry.ACCEPT,"false");
        msgValues.put(Task.TaskEntry.PUBLIC_KEY,publicKey);
        db.insert(Task.TaskEntry.USER, null, msgValues);
        ContentValues ipValues = new ContentValues();
    }

    public void SetMyInformation(String inetAddress,String username,String mail,String gender,String name,String surname,String country,String city,String birth,String number,String profilePic){
        db=this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.ID_USER,0);
        msgValues.put(Task.TaskEntry.USERNAME,username);
        msgValues.put(Task.TaskEntry.MAIL,mail);
        msgValues.put(Task.TaskEntry.GENDER,gender);
        msgValues.put(Task.TaskEntry.NAME,name);
        msgValues.put(Task.TaskEntry.SURNAME,surname);
        msgValues.put(Task.TaskEntry.COUNTRY,country);
        msgValues.put(Task.TaskEntry.CITY,city);
        msgValues.put(Task.TaskEntry.BIRTH,birth);
        msgValues.put(Task.TaskEntry.NUMBER,number);
        msgValues.put(Task.TaskEntry.IP,inetAddress);
        msgValues.put(Task.TaskEntry.PROFILE_PIC,profilePic);
        db.insert(Task.TaskEntry.USER, null, msgValues);
        ContentValues ipValues = new ContentValues();
    }

    public void addNumber(String number,String idUser){
        db=this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.NUMBER, number);
        db.update(Task.TaskEntry.USER,msgValues, Task.TaskEntry.ID_USER+" = "+idUser,null);
    }

    public Cursor  getAllUsers(){
        db=this.getWritableDatabase();
        String query = "SELECT *" +
                " FROM "+ Task.TaskEntry.USER+
                " WHERE "+ Task.TaskEntry.IP+" IS NOT NULL";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }else{
            return null;
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

    public Cursor getUser(String id){
        String query = "SELECT * " +
                " FROM " +Task.TaskEntry.USER+
                " WHERE id_user ='" + id + "'" ;
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    public void deleteUser(String idUser){
        db=this.getWritableDatabase();
        String query = "SELECT *" +
                " FROM "+ Task.TaskEntry.CHAT+
                " WHERE "+ Task.TaskEntry.ID_CHAT +"="+idUser;
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            ContentValues msgValues = new ContentValues();
            msgValues.put(Task.TaskEntry.IP, "NULL");
            db.update(Task.TaskEntry.USER,msgValues, Task.TaskEntry.ID_USER+" = "+idUser,null);
        } else {
            query = " DELETE FROM " + Task.TaskEntry.USER
                    + " WHERE '" + Task.TaskEntry.ID_USER + "' = '" + idUser + "'";
            db.execSQL(query);
        }
    }

    public void deleteAllUser(){
        db=this.getWritableDatabase();
        String query="DELETE FROM USER " +
                " WHERE NOT EXISTS(SELECT NULL" +
                "                    FROM CHAT f" +
                "                   WHERE f.id_chat = USER.id_user)";
        db.execSQL(query);
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.IP, "NULL");
        db.update(Task.TaskEntry.USER,msgValues, null,null);

    }
    public String getMaxId(){
        String query = " SELECT MAX("+ Task.TaskEntry.ID_USER+")"+
                " FROM "+ Task.TaskEntry.USER+
                " WHERE "+ Task.TaskEntry.IP +" IS NOT NULL"  ;
        Cursor c = db.rawQuery(query,null);
        if (c != null) {
            c.moveToFirst();
        }
        return c.getString(0);
    }

    public String getAccept(String idUser) {
        String query = "SELECT " + Task.TaskEntry.ACCEPT +
                " FROM "+ Task.TaskEntry.USER+
                " WHERE "+ Task.TaskEntry.ID_USER +"="+idUser;
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c.getString(0);
    }

    public void setAccept(String idUser, String value) {
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.ACCEPT, value);
        db.update(Task.TaskEntry.USER,msgValues, Task.TaskEntry.ID_USER+" = "+idUser,null);
    }

    public void setNumber(String idUser, String value) {
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.NUMBER, value);
        db.update(Task.TaskEntry.USER,msgValues, Task.TaskEntry.ID_USER+" = "+idUser,null);
    }

    public String getMyEmail(){
        String query = "SELECT " + Task.TaskEntry.MAIL +
                " FROM "+ Task.TaskEntry.USER+
                " WHERE "+ Task.TaskEntry.ID_USER +"="+0;
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        String data="";
        try {
            data = c.getString(c.getColumnIndex((Task.TaskEntry.MAIL)));
        }catch(IndexOutOfBoundsException e){
            System.out.println("Utente 0 non trovato");
        }
        return data;
    }

    public String[] getMyEmailAndPassword(){
        String query = "SELECT " + Task.TaskEntry.PASSWORD +
                " FROM "+ Task.TaskEntry.USER+
                " WHERE "+ Task.TaskEntry.ID_USER +"="+0;
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        String [] data ={c.getString(c.getColumnIndex((Task.TaskEntry.MAIL))),c.getString(c.getColumnIndex((Task.TaskEntry.PASSWORD)))};
        return data;
    }

    public void discard(String id){
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.MESSAGES_ACCEPTED, "false");
        db.update(Task.TaskEntry.USER,msgValues, Task.TaskEntry.ID_USER+" = "+id,null);
    }

    public String getMessageAccepted(String id){
        String query = "SELECT " + Task.TaskEntry.MESSAGES_ACCEPTED +
                " FROM "+ Task.TaskEntry.USER+
                " WHERE "+ Task.TaskEntry.ID_USER +"="+id;
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c.getString(0);
    }

    public void setMyPassword(String password){
        db=this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.PASSWORD, password);
        db.update(Task.TaskEntry.USER,msgValues, Task.TaskEntry.ID_USER+" = "+"0",null);
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