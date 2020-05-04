package com.example.connection.Controller;

import android.provider.BaseColumns;

public class Task {

    public class TaskEntry implements BaseColumns {
        public static final String USER_TABLE = "USER";
        public static final String ID_USER = "id_user";
        public static final String NAME = "name";
        public static final String SURNAME = "surname";
        public static final String AGE = "age";
        public static final String GENDER = "gender";
        public static final String MAIL = "mail";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String COUNTRY = "country";
        public static final String CITY = "city";
        public static final String PROFILE_PIC = "profile_pic";
        public static final String MESSAGE_TABLE = "MESSAGE";
        public static final String ID_CHAT = "id_chat";
        public static final String MSG = "msg";
        public static final String PATH = "path";
        public static final String CHAT = "CHAT";
        public static final String ID_CHAT2 = "id_chat";
        public static final String ID_SENDER = "id_sender";
        public static final String ID_RECEIVER = "id_receiver";
        public static final String IP_TABLE = "IP";
        public static final String ID_USER_FROM_IP = "id_user";
        public static final String IP = "ip";
    }

}
