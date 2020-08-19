package com.example.connection.Controller;

import android.provider.BaseColumns;

public class Task {

    //LIST OF ALL THE DATABASE VARIABLE --------------------------------------------------------------------------------------------------------------------------------
    public class TaskEntry implements BaseColumns {
        public static final String USER = "USER";
        public static final String ID_USER = "id_user";
        public static final String NAME = "name";
        public static final String SURNAME = "surname";
        public static final String AGE = "age";
        public static final String GENDER = "gender";
        public static final String NUMBER = "number";
        public static final String MAIL = "mail";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String COUNTRY = "country";
        public static final String CITY = "city";
        public static final String PROFILE_PIC = "profile_pic";
        public static final String MESSAGE= "MESSAGE";
        public static final String ID_CHAT = "id_chat";
        public static final String MSG = "msg";
        public static final String PATH = "path";
        public static final String CHAT = "CHAT";
        public static final String ID_SENDER = "id_sender";
        public static final String IP = "ip";
        public static final String GLOBAL_MESSAGE = "GROUPS_MESSAGE";
        public static final String DATETIME = "datetime";
        public static final String LAST_MESSAGE = "last_message";
        public static final String NOT_READ_MESSAGE = "not_read_message";
        public static final String ACCEPT = "accept";
        public static final String BACKGROUND_CHAT_IMAGES = "BACKGROUND_CHAT_IMAGES";
        public static final String BACKGROUND_IMAGE = "BACKGROUND_IMAGE";
        /*public static final String GROUPS = "GROUPS";
        public static final String GROUP_NAME = "GROUP_NAME";
        public static final String USERS_GROUP = "USER_GROUP";
        public static final String ID_GROUP = "id_group";
        public static final String GROUP_MESSAGE = "GROUP_MESSAGE";*/
    }

}
