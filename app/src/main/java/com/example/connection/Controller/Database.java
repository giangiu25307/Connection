package com.example.connection.Controller;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;
    private static final String DATABASE_NAME = "Connection";


    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_USER_TABLE = "CREATE TABLE USER ( "
                + "id_user INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" + ", "
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
                + "id_chat INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" + ", "
                + "msg TEXT" + ", "
                + "path TEXT"
                + ")";

        String CREATE_CHAT_TABLE = "CREATE TABLE CHAT ( "
                + "id_chat INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" + ", "
                + "id_sender INTEGER NOT NULL" + ", "
                + "id_receiver INTEGER NOT NULL"+","
                + "PRIMARY KEY (id_chat)"+","
                + "FOREIGN KEY (id_chat) REFERENCES MESSAGE (id_chat)"
                + ")";

        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_MESSAGE_TABLE);
        db.execSQL(CREATE_CHAT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        String DROP_TABLE = "DROP TABLE IF EXISTS USER";
        db.execSQL(DROP_TABLE);
        onCreate(db);
        DROP_TABLE = "DROP TABLE IF EXISTS CHAT";
        db.execSQL(DROP_TABLE);
        onCreate(db);
        DROP_TABLE = "DROP TABLE IF EXISTS MESSAGE";
        db.execSQL(DROP_TABLE);
        onCreate(db);

    }

    //CRUD OPERATIONS
    /*
    public void addNote(Note note) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_TITLE, note.getTitle());
        values.put(KEY_NOTE, note.getNote());


        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public Note getNote(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.query(TABLE_NAME, COLS_ID_TITLE_NOTE, KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        db.close();

        Log.d(TAG, "Get Note Result " + c.getString(0) + "," + c.getString(1) + "," + c.getString(2));
        Note note = new Note(Integer.parseInt(c.getString(0)), c.getString(1), c.getString(2));
        return note;
    }

    public List<Note> getAllNotes() {
        SQLiteDatabase db = this.getReadableDatabase();

        List<Note> noteList = new ArrayList<>();

        Cursor cursor = db.query(TABLE_NAME, COLS_ID_TITLE_NOTE, null, null, null, null, null);


        if (cursor != null && cursor.moveToFirst()) {

            do {
                Note note = new Note();
                note.setId(Integer.parseInt(cursor.getString(0)));
                note.setTitle(cursor.getString(1));
                note.setNote(cursor.getString(2));
                noteList.add(note);

            } while (cursor.moveToNext());


        }
        db.close();
        return noteList;

    }*/


}