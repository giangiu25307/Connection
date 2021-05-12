package com.example.connection.Database;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.connection.Controller.ConnectionController;
import com.example.connection.Controller.Task;
import com.example.connection.Model.User;

import java.time.LocalDateTime;

public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Connection";
    private SQLiteDatabase db = this.getWritableDatabase();
    private Context context;
    private Intent intent = new Intent();

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
                + Task.TaskEntry.IP_GROUP_OWNER + " TEXT, "
                + Task.TaskEntry.ACCEPT + " TEXT DEFAULT 'false', "
                + Task.TaskEntry.MESSAGES_ACCEPTED + " TEXT DEFAULT 'true', "
                + Task.TaskEntry.OTHER_GROUP + " TEXT DEFAULT 0 "
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
                + Task.TaskEntry.REQUEST + " TEXT DEFAULT 'true', "
                + Task.TaskEntry.SYMMETRIC_KEY + " TEXT"
                + ")";

        String CREATE_GLOBAL_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS " + Task.TaskEntry.GLOBAL_MESSAGE + " ( "
                + Task.TaskEntry.ID_SENDER + " TEXT NOT NULL, "
                + Task.TaskEntry.MSG + " TEXT NOT NULL, "
                + Task.TaskEntry.DATETIME + " TEXT, "
                + Task.TaskEntry.LAST_MESSAGE + " TEXT, "
                + Task.TaskEntry.NOT_READ_MESSAGE + " INTEGER "
                + ")";

        String BACKGROUND_CHAT_IMAGES = "CREATE TABLE IF NOT EXISTS " + Task.TaskEntry.BACKGROUND_CHAT_IMAGES + " ( "
                + Task.TaskEntry.BACKGROUND_IMAGE + " TEXT NOT NULL "
                + ")";

        database.execSQL(CREATE_USER_TABLE);
        database.execSQL(CREATE_MESSAGE_TABLE);
        database.execSQL(CREATE_CHAT_TABLE);
        database.execSQL(CREATE_GLOBAL_MESSAGE_TABLE);
        database.execSQL(BACKGROUND_CHAT_IMAGES);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public void onDestroy() {
        db.close();
    }

    //CHAT-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    /**
     * Add the received message in the database
     * @param msg textual message received
     * @param idSender id of the person who sent this message
     * @param idChat id of the person i'm speaking to
     */
    public void addMsg(String msg, String idSender, String idChat) {
        //if (idSender.equals("0")) setRequest(idChat, "false");
        db = this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.ID_CHAT, idChat);
        msgValues.put(Task.TaskEntry.ID_SENDER, idSender);
        msgValues.put(Task.TaskEntry.MSG, msg);
        msgValues.put(Task.TaskEntry.DATETIME, String.valueOf(LocalDateTime.now()));
        db.insert(Task.TaskEntry.MESSAGE, null, msgValues);
        lastMessageChat(msg, idChat);
    }

    /**
     * Add a imagePath in the database
     * @param paths path of the image received
     * @param idSender id of the person who sent this message
     * @param idChat id of the person i'm speaking to
     */
    public void addImage(String paths, String idSender, String idChat) {
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.ID_CHAT, idChat);
        msgValues.put(Task.TaskEntry.ID_SENDER, idSender);
        msgValues.put(Task.TaskEntry.PATH, paths);
        msgValues.put(Task.TaskEntry.DATETIME, String.valueOf(LocalDateTime.now()));
        db.insert(Task.TaskEntry.MESSAGE, null, msgValues);
        lastMessageChat(paths, idChat);
    }

    /**
     * Update the last message of a specified chat
     * @param msg last textual message received
     * @param idChat id of the person i'm speaking to
     */
    private void lastMessageChat(String msg, String idChat) {
        db = this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.LAST_MESSAGE, msg);
        msgValues.put(Task.TaskEntry.DATETIME, String.valueOf(LocalDateTime.now()));
        db.update(Task.TaskEntry.CHAT, msgValues, Task.TaskEntry.ID_CHAT + "=" + idChat, null);
    }

    /**
     * Get the last message of the specified chat
     * @param idChat id of the person i'm speaking to
     */
    public Cursor getLastMessageChat(String idChat) {
        String query = "SELECT last_message, datetime " +
                " FROM " + Task.TaskEntry.CHAT +
                " WHERE '" + Task.TaskEntry.ID_CHAT + "' = '" + idChat + "'";
        Cursor cursor = db.rawQuery(query, null);
        return cursor;

    }

    /**
     * Create a chat in the db
     */
    public void createChat(String idChat, String name, String symmetric) {
        db = this.getWritableDatabase();
        ContentValues chatValues = new ContentValues();
        chatValues.put(Task.TaskEntry.ID_CHAT, idChat);
        chatValues.put(Task.TaskEntry.NAME, name);
        //chatValues.put(Task.TaskEntry.REQUEST, "true");
        chatValues.put(Task.TaskEntry.SYMMETRIC_KEY, symmetric);
        db.insert(Task.TaskEntry.CHAT, null, chatValues);
    }

    /**
     * Get all msg from a specified chat
     */
    public Cursor getAllMsg(String idChat) {
        String query = " SELECT id_sender,msg,path,datetime " +
                " FROM MESSAGE " +
                " WHERE id_chat = " + idChat +
                " ORDER BY " + Task.TaskEntry.DATETIME + " ASC ";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    /**
     * Get all chat saved in the db retrieving:
     * last message, date and time, and other person name of every chat
     * that i accepted or which i sent the first message and are who i not blocked
     */
    public Cursor getAllChat() {
        String query = "SELECT " + Task.TaskEntry.ID_CHAT + ", " + Task.TaskEntry.NAME + ", " + Task.TaskEntry.LAST_MESSAGE + ", " + Task.TaskEntry.DATETIME +
                " FROM " + Task.TaskEntry.CHAT + " ORDER BY " + Task.TaskEntry.DATETIME + " DESC ";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    /**
     * Get all chat who i not already accepted and not blocked
     */
    public Cursor getAllRequestChat() {
        String query = " SELECT c.id_chat, c.name, c.last_message, c.datetime, u.birth, u.gender " +
                " FROM CHAT c INNER JOIN USER u on c.id_chat = u.id_user" +
                " WHERE c.request = 'true' AND u.message_accept = 'true'" +
                " ORDER BY c.datetime DESC";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    /**
     * Get all chat who i already accepted and not blocked
     */
    public Cursor getAllNoRequestChat() {
        String query = " SELECT c.id_chat, c.name, c.last_message, c.datetime, u.birth, u.gender" +
                " FROM CHAT c INNER JOIN USER u on c.id_chat = u.id_user" +
                " WHERE c.request = 'false' AND u.message_accept = 'true'" +
                " ORDER BY c.datetime DESC";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    /**
     * Set request false if i accept the person to write me
     */
    public void setRequest(String id, String value) {
        db = this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.REQUEST, value);
        db.update(Task.TaskEntry.CHAT, msgValues, Task.TaskEntry.ID_CHAT + "=" + id, null);
    }

    /**
     * Get symmetricKey of a specified chat
     */
    public String getSymmetricKey(String idChat) {
        db = this.getWritableDatabase();
        String query = "SELECT " + Task.TaskEntry.SYMMETRIC_KEY +
                " FROM " + Task.TaskEntry.CHAT +
                " WHERE " + Task.TaskEntry.ID_CHAT + " = " + idChat;
        try {
            Cursor c = db.rawQuery(query, null);
            if (c != null) {
                c.moveToFirst();
                return c.getString(0);
            } else {
                return null;
            }
        }
       catch (IndexOutOfBoundsException  e){
            return null;
       }

    }

    /**
     * Get the background image of the chat
     */
    public Cursor getBackgroundImage() {
        db = this.getWritableDatabase();
        String query = "SELECT " + Task.TaskEntry.BACKGROUND_IMAGE +
                " FROM " + Task.TaskEntry.BACKGROUND_CHAT_IMAGES;
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToLast();
        } else {
            return null;
        }
        return c;
    }

    /**
     * Set the background image of the chat
     */
    public void setBackgroundImage(String bgImage) {
        db = this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.BACKGROUND_IMAGE, bgImage);
        db.insert(Task.TaskEntry.BACKGROUND_CHAT_IMAGES, null, msgValues);
    }

    //globale
    /**
     * Add a global message
     */
    public void addGlobalMsg(String msg, String idSender) {
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.ID_SENDER, idSender);
        msgValues.put(Task.TaskEntry.MSG, msg);
        db.insert(Task.TaskEntry.GLOBAL_MESSAGE, null, msgValues);
        lastGlobalMessageChat(msg, idSender);
    }

    /**
     * Get last global message chat
     */
    private void lastGlobalMessageChat(String msg, String idSender) {
        db = this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.LAST_MESSAGE, msg);
        db.update(Task.TaskEntry.GLOBAL_MESSAGE, msgValues, Task.TaskEntry.ID_SENDER + " = " + idSender, null);
    }

    /**
     * Get all msg from global chat
     */
    public Cursor getAllGlobalMsg() {
        String query = " SELECT m.id_sender,m.msg,m.datetime, u.username  " +
                " FROM " + Task.TaskEntry.GLOBAL_MESSAGE +
                " m INNER JOIN " + Task.TaskEntry.USER + " u ON u." + Task.TaskEntry.ID_USER + " = m." + Task.TaskEntry.ID_SENDER +
                " ORDER BY m." + Task.TaskEntry.DATETIME + " ASC ";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }


    //USER
    /**
     * Get my user information
     */
    public String[] getMyInformation() {
        String[] user = new String[11];
        String query = "SELECT *" +
                " FROM " + Task.TaskEntry.USER;
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        user[0] = c.getString(0);
        user[1] = c.getString(1);
        user[2] = c.getString(2);
        user[3] = c.getString(3);
        user[4] = c.getString(4);
        user[5] = c.getString(5);
        user[6] = c.getString(6);
        user[7] = c.getString(7);
        user[8] = c.getString(8);
        user[9] = c.getString(9);
        user[10] = c.getString(10);
        return user;
    }

    /**
     * Get all user information
     */
    public User getAllUserInformation(String id) {
        String[] user = new String[11];
        String query = "SELECT *" +
                " FROM " + Task.TaskEntry.USER
                +" WHERE "+ Task.TaskEntry.ID_USER + " = "+ id ;
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return new User(c.getString(0),c.getString(1),c.getString(2),c.getString(3),c.getString(4),c.getString(5),c.getString(6),c.getString(7),c.getString(8),c.getString(9),c.getString(10));
    }

    /**
     * Get the birth of a specified user
     */
    public Cursor getBirth(String id) {
        db = this.getWritableDatabase();
        String query = "SELECT " + Task.TaskEntry.BIRTH +
                " FROM " + Task.TaskEntry.USER +
                " WHERE " + Task.TaskEntry.ID_USER + "= "+id;
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        } else {
            return null;
        }
        return c;
    }

    /**
     * Get the gender of a specified user
     */
    public Cursor getGender(String id) {
        db = this.getWritableDatabase();
        String query = "SELECT " + Task.TaskEntry.GENDER +
                " FROM " + Task.TaskEntry.USER +
                " WHERE " + Task.TaskEntry.ID_USER + "= "+id;
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        } else {
            return null;
        }
        return c;
    }

    /**
     * Get the profile pic of a specified user
     */
    public Cursor getProfilePic(String id) {
        db = this.getWritableDatabase();
        String query = "SELECT " + Task.TaskEntry.PROFILE_PIC +
                " FROM " + Task.TaskEntry.USER +
                " WHERE " + Task.TaskEntry.ID_USER + "= "+id;
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        } else {
            return null;
        }
        return c;
    }

    /**
     * Set the profile pic of a user
     */
    public void setProfilePic(String id, String profilePic) {
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.PROFILE_PIC, profilePic);
        db.update(Task.TaskEntry.USER, msgValues, Task.TaskEntry.ID_USER + " = " + id, null);
    }

    /**
     * Set my public key
     */
    public void setPublicKey(String publicKey) {
        db = this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.PUBLIC_KEY, publicKey);
        db.update(Task.TaskEntry.USER, msgValues,Task.TaskEntry.ID_USER + " = " + ConnectionController.myUser.getIdUser(), null);
    }

    /**
     * Get my public key
     */
    public String getPublicKey(String id) {
        db = this.getWritableDatabase();
        String query = "SELECT " + Task.TaskEntry.PUBLIC_KEY +
                " FROM " + Task.TaskEntry.USER +
                " WHERE " + Task.TaskEntry.ID_USER + " = " + id;
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToLast();
        } else {
            return null;
        }
        return c.getString(0);
    }

    /**
     * Add a user in the db
     */
    public void addUser(String idUser, String inetAddress, String username, String mail, String gender, String name, String surname, String country, String city, String birth, String profilePic, String publicKey) {
        db = this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.ID_USER, idUser);
        msgValues.put(Task.TaskEntry.USERNAME, username);
        msgValues.put(Task.TaskEntry.MAIL, mail);
        msgValues.put(Task.TaskEntry.GENDER, gender);
        msgValues.put(Task.TaskEntry.NAME, name);
        msgValues.put(Task.TaskEntry.SURNAME, surname);
        msgValues.put(Task.TaskEntry.COUNTRY, country);
        msgValues.put(Task.TaskEntry.CITY, city);
        msgValues.put(Task.TaskEntry.BIRTH, birth);
        msgValues.put(Task.TaskEntry.IP, inetAddress);
        msgValues.put(Task.TaskEntry.PROFILE_PIC, profilePic);
        msgValues.put(Task.TaskEntry.PUBLIC_KEY, publicKey);
        db.insert(Task.TaskEntry.USER, null, msgValues);
        ContentValues ipValues = new ContentValues();
    }

    /**
     * Get the information of every person which are connected to me
     */
    public String getAllMyGroupInfo() {
        Cursor allUser = getAllUsersWithoutME();
        String allMyGroupInfo = ConnectionController.myUser.getAll();
        for (int i = 0; i < allUser.getCount(); i++) {
            if (allUser.getString(16).equals("1")) {
                allMyGroupInfo += "£€" + allUser.getString(0) + "£€" + ConnectionController.myUser.getInetAddress().getHostName() + "%" + "£€" + allUser.getString(1) + "£€" + allUser.getString(2) + "£€"
                        + allUser.getString(3) + "£€" + allUser.getString(4) + "£€" + allUser.getString(5) + "£€" + allUser.getString(6) + "£€"
                        + allUser.getString(7) + "£€" + allUser.getString(9) + "£€" + allUser.getString(10) + "£€"
                        + allUser.getString(11);
            } else {
                allMyGroupInfo += "£€" + allUser.getString(0) + "£€" + allUser.getString(12) + "£€" + allUser.getString(1) + "£€" + allUser.getString(2) + "£€"
                        + allUser.getString(3) + "£€" + allUser.getString(4) + "£€" + allUser.getString(5) + "£€" + allUser.getString(6) + "£€"
                        + allUser.getString(7) + "£€" + allUser.getString(9) + "£€" + allUser.getString(10) + "£€"
                        + allUser.getString(11);

            }
            System.out.println(allMyGroupInfo);
            allUser.moveToNext();
        }
        return allMyGroupInfo;
    }

    /**
     * Set my information, we usually use add user anyway because are the same
     */
    public void SetMyInformation(String inetAddress, String username, String mail, String gender, String name, String surname, String country, String city, String birth, String number, String profilePic) {
        db = this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.ID_USER, 0);
        msgValues.put(Task.TaskEntry.USERNAME, username);
        msgValues.put(Task.TaskEntry.MAIL, mail);
        msgValues.put(Task.TaskEntry.GENDER, gender);
        msgValues.put(Task.TaskEntry.NAME, name);
        msgValues.put(Task.TaskEntry.SURNAME, surname);
        msgValues.put(Task.TaskEntry.COUNTRY, country);
        msgValues.put(Task.TaskEntry.CITY, city);
        msgValues.put(Task.TaskEntry.BIRTH, birth);
        msgValues.put(Task.TaskEntry.NUMBER, number);
        msgValues.put(Task.TaskEntry.IP, inetAddress);
        msgValues.put(Task.TaskEntry.PROFILE_PIC, profilePic);
        db.insert(Task.TaskEntry.USER, null, msgValues);
        ContentValues ipValues = new ContentValues();
    }

    /**
     * Add the number of the specified user
     */
    public void addNumber(String number, String idUser) {
        db = this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.NUMBER, number);
        db.update(Task.TaskEntry.USER, msgValues, Task.TaskEntry.ID_USER + " = " + idUser, null);
    }

    /**
     * Return all user who got an ip
     */
    public Cursor getAllUsers() {
        db = this.getWritableDatabase();
        String query = "SELECT *" +
                " FROM " + Task.TaskEntry.USER +
                " WHERE " + Task.TaskEntry.IP + " IS NOT NULL";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        } else {
            return null;
        }
        return c;
    }

    /**
     * Return all user who got an ip and not myself
     */
    public Cursor getAllUsersWithoutME() {
        db = this.getWritableDatabase();
        String query = "SELECT *" +
                " FROM " + Task.TaskEntry.USER +
                " WHERE " + Task.TaskEntry.IP + " IS NOT NULL AND NOT "+ Task.TaskEntry.ID_USER + " = " + ConnectionController.myUser.getIdUser();
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        } else {
            return null;
        }
        return c;
    }

    /**
     * Return the ip of a specified user
     */
    public String findIp(String id_user) {
        String query = "SELECT " + Task.TaskEntry.IP +
                " FROM " + Task.TaskEntry.USER +
                " WHERE id_user ='" + id_user + "'";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c.getString(0);
    }

    /**
     * Return the user of the specified ip
     */
    public String findId_user(String ip) {

        String query = "SELECT " + Task.TaskEntry.ID_USER +
                " FROM " + Task.TaskEntry.USER +
                " WHERE ip ='" + ip + "'";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c.getString(1);
    }

    /**
     * Return the specified user
     */
    public Cursor getUser(String id) {
        String query = "SELECT * " +
                " FROM " + Task.TaskEntry.USER +
                " WHERE id_user ='" + id + "'";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    /**
     * Delete the specified user, if i have a chat with him
     * this method will cancel only his ip
     */
    public void deleteUser(String idUser) {
        db = this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.IP, "NULL");
        db.update(Task.TaskEntry.USER, msgValues, Task.TaskEntry.ID_USER + " = " + idUser, null);

        String query = "SELECT *" +
                " FROM " + Task.TaskEntry.CHAT +
                " WHERE " + Task.TaskEntry.ID_CHAT + "=" + idUser;
        Cursor c = db.rawQuery(query, null);
        try {
            c.moveToFirst();
            c.getString(0);
        }catch(IndexOutOfBoundsException e){
            query = " DELETE FROM " + Task.TaskEntry.USER
                    + " WHERE '" + Task.TaskEntry.ID_USER + "' = '" + idUser + "'";
            db.execSQL(query);
        }
    }

    /**
     * Delete all user, if a user has a chat with me
     * this method will delete only his ip
     */
    public void deleteAllUser() {
        db = this.getWritableDatabase();
        String query = "DELETE FROM USER " +
                " WHERE NOT EXISTS(SELECT NULL" +
                "                    FROM CHAT f" +
                "                   WHERE f.id_chat = USER.id_user)";
        db.execSQL(query);
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.IP, "NULL");
        db.update(Task.TaskEntry.USER, msgValues, null, null);

    }

    /**
     * Return the max id from the user of my group
     */
    public String getMaxId() {
        String query = " SELECT MAX( " + Task.TaskEntry.ID_USER + " ) " +
                " FROM " + Task.TaskEntry.USER +
                " WHERE " + Task.TaskEntry.IP + " IN (SELECT " + Task.TaskEntry.IP +
                "                   FROM " + Task.TaskEntry.USER + " " +
                "                   GROUP BY " + Task.TaskEntry.IP + "" +
                "                   HAVING count(" + Task.TaskEntry.IP + ") = 1 ) AND 0 = "+ Task.TaskEntry.OTHER_GROUP+" AND NOT "+ Task.TaskEntry.ID_USER+" = "+ ConnectionController.myUser.getIdUser();
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c.getString(0);
    }


    public String getAccept(String idUser) {
        String query = "SELECT " + Task.TaskEntry.ACCEPT +
                " FROM " + Task.TaskEntry.USER +
                " WHERE " + Task.TaskEntry.ID_USER + "=" + idUser;
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c.getString(0);
    }


    public void setAccept(String idUser, String value) {
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.ACCEPT, value);
        db.update(Task.TaskEntry.USER, msgValues, Task.TaskEntry.ID_USER + " = " + idUser, null);
    }

    /**
     * Set the number of the specified user
     */
    public void setNumber(String idUser, String value) {
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.NUMBER, value);
        db.update(Task.TaskEntry.USER, msgValues, Task.TaskEntry.ID_USER + " = " + idUser, null);
    }

    /**
     * Return my email
     */
    public String getMyEmail() {
        String query = "SELECT " + Task.TaskEntry.MAIL +
                " FROM " + Task.TaskEntry.USER +
                " WHERE " + Task.TaskEntry.ID_USER + "=" + 0;
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        String data = "";
        try {
            data = c.getString(c.getColumnIndex((Task.TaskEntry.MAIL)));
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Utente 0 non trovato");
        }
        return data;
    }

    /**
     * Return an array with my email and my password
     */
    public String[] getMyEmailAndPassword() {
        String query = "SELECT " + Task.TaskEntry.PASSWORD +
                " FROM " + Task.TaskEntry.USER +
                " WHERE " + Task.TaskEntry.ID_USER + "=" + 0;
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        String[] data = {c.getString(c.getColumnIndex((Task.TaskEntry.MAIL))), c.getString(c.getColumnIndex((Task.TaskEntry.PASSWORD)))};
        return data;
    }

    /**
     * Discard the chat with the specified user
     */
    public void discard(String id) {
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.MESSAGES_ACCEPTED, "false");
        db.update(Task.TaskEntry.USER, msgValues, Task.TaskEntry.ID_USER + " = " + id, null);
    }

    /**
     * Return if i accept or not the specified chat
     */
    public String getMessageAccepted(String id) {
        String query = "SELECT " + Task.TaskEntry.MESSAGES_ACCEPTED +
                " FROM " + Task.TaskEntry.USER +
                " WHERE " + Task.TaskEntry.ID_USER + "=" + id;
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c.getString(0);
    }

    /**
     * Set my password
     */
    public void setMyPassword(String password) {
        db = this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.PASSWORD, password);
        db.update(Task.TaskEntry.USER, msgValues, Task.TaskEntry.ID_USER + " = " + "0", null);
    }

    /**
     * Set the city of the specified user
     */
    public void setCity(String id, String city) {
        db = this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.CITY, city);
        db.update(Task.TaskEntry.USER, msgValues, Task.TaskEntry.ID_USER + " = " + id, null);
    }

    /**
     * Set the ip of the specified user
     */
    public void setIp(String id, String ip) {
        db = this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.IP, ip);
        db.update(Task.TaskEntry.USER, msgValues, Task.TaskEntry.ID_USER + " = " + id, null);
    }

    /**
     * Set the username of the specified user
     */
    public void setUsername(String id, String username) {
        db = this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.USERNAME, username);
        db.update(Task.TaskEntry.USER, msgValues, Task.TaskEntry.ID_USER + " = " + id, null);
    }

    /**
     * Set the name of the specified user
     */
    public void setName(String id, String name) {
        db = this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.NAME, name);
        db.update(Task.TaskEntry.USER, msgValues, Task.TaskEntry.ID_USER + " = " + id, null);
    }

    /**
     * Set the email of the specified user
     */
    public void setMail(String id, String mail) {
        db = this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.MAIL, mail);
        db.update(Task.TaskEntry.USER, msgValues, Task.TaskEntry.ID_USER + " = " + id, null);
    }

    /**
     * Set the surname of the specified user
     */
    public void setSurname(String id, String surname) {
        db = this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.SURNAME, surname);
        db.update(Task.TaskEntry.USER, msgValues, Task.TaskEntry.ID_USER + " = " + id, null);
    }

    /**
     * Set the gender of the specified user
     */
    public void setGender(String id, String gender) {
        db = this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.GENDER, gender);
        db.update(Task.TaskEntry.USER, msgValues, Task.TaskEntry.ID_USER + " = " + id, null);
    }

    /**
     * Set the country of the specified user
     */
    public void setCountry(String id, String country) {
        db = this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.COUNTRY, country);
        db.update(Task.TaskEntry.USER, msgValues, Task.TaskEntry.ID_USER + " = " + id, null);
    }

    /**
     * Set 1 if i'm connected to the specified user, else 0
     */
    public void setOtherGroup(String id) {
        db = this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.OTHER_GROUP, "1");
        db.update(Task.TaskEntry.USER, msgValues, Task.TaskEntry.ID_USER + " = " + id, null);
    }

    /**
     * return true if i'm connected to him
     */
    public boolean isOtherGroup(String id) {
        String query = "SELECT " + Task.TaskEntry.OTHER_GROUP +
                " FROM " + Task.TaskEntry.USER +
                " WHERE " + Task.TaskEntry.OTHER_GROUP + " = " + " 1 AND " + Task.TaskEntry.ID_USER + " = " + id;
        Cursor c = db.rawQuery(query, null);
        try {
            c.moveToFirst();
            c.getString(0);
            return true;
        } catch (CursorIndexOutOfBoundsException e) {
            return false;
        }
    }

    /**
     * Return all the id which i'm connected to
     */
    public String detectAllOtherGroupClient() {
        String query = "SELECT " + Task.TaskEntry.ID_USER +
                " FROM " + Task.TaskEntry.USER +
                " WHERE " + Task.TaskEntry.OTHER_GROUP + "=" + "1";
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        String idToBeDeleted = "";
        for (int i = 0; i < c.getCount(); i++) {
            idToBeDeleted += c.getString(0) + ",";
        }
        return idToBeDeleted;
    }

    /**
     *  Return all the id which the specified id
     */
    public String detectAllOtherGroupClientByIp(String ip) {
        String query = "SELECT " + Task.TaskEntry.ID_USER +
                " FROM " + Task.TaskEntry.USER +
                " WHERE " + Task.TaskEntry.IP + "=" + ip;
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }
        String idToBeDeleted = "";
        for (int i = 0; i < c.getCount(); i++) {
            idToBeDeleted += c.getString(0) + ",";
        }
        return idToBeDeleted;
    }

    /**
     *  Delete all user, if i have a chat with one,
     *  whis method will delete only his ip
     */
    public void deleteAllIdUser(String idsToBeDeleted) {
        db = this.getWritableDatabase();
        String query = "DELETE FROM USER " +
                " WHERE " + Task.TaskEntry.ID_USER + " IN( " + idsToBeDeleted + " ) AND NOT EXISTS(SELECT NULL " +
                " FROM CHAT f " +
                " WHERE f.id_chat = USER.id_user) ";
        db.execSQL(query);
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.IP, "NULL");
        db.update(Task.TaskEntry.USER, msgValues, Task.TaskEntry.ID_USER + " IN(" + idsToBeDeleted + " )", null);

    }

    /**
     *  Return the username of the specified user
     */
    public String getUserName(String id){
        String query = "SELECT " + Task.TaskEntry.NAME +
                " FROM " + Task.TaskEntry.USER +
                " WHERE " + Task.TaskEntry.ID_USER + "=" + id;
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }else{
            return null;
        }
        return c.getString(0);
    }

    /**
     *  Return my Group Owner ip
     */
    public String findGroupOwnerIp(){
        String query = "SELECT " + Task.TaskEntry.IP_GROUP_OWNER +
                " FROM " + Task.TaskEntry.USER +
                " WHERE NOT " + Task.TaskEntry.ID_USER + " = " + ConnectionController.myUser.getIdUser();
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        }else{
            return null;
        }
        return c.getString(0);
    }

    /**
     *  Return my client ip
     */
    public String getMyGroupOwnerIp(){
        String query = "SELECT " + Task.TaskEntry.IP_GROUP_OWNER +
                " FROM " + Task.TaskEntry.USER +
                " WHERE " + Task.TaskEntry.ID_USER + " = " + ConnectionController.myUser.getIdUser();
        Cursor c = db.rawQuery(query, null);
        if (c != null) {
            c.moveToFirst();
        } else {
            return null;
        }
        return c.getString(0);
    }

    /**
     * Set the ip of my group owner
     */
    public void setMyGroupOwnerIp(String ip, String id) {
        db = this.getWritableDatabase();
        ContentValues msgValues = new ContentValues();
        msgValues.put(Task.TaskEntry.IP_GROUP_OWNER, ip);
        db.update(Task.TaskEntry.USER, msgValues, Task.TaskEntry.ID_USER + "=" + id, null);
    }
}