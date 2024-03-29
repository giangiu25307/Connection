package com.ConnectionProject.connection.Controller;

import android.provider.BaseColumns;

public class Task {

    //LIST OF ALL THE DATABASE VARIABLE --------------------------------------------------------------------------------------------------------------------------------
    public class TaskEntry implements BaseColumns {
        public static final String USER = "USER";
        public static final String ID_USER = "id_user";
        public static final String NAME = "name";
        public static final String SURNAME = "surname";
        public static final String BIRTH = "birth";
        public static final String GENDER = "gender";
        public static final String NUMBER = "number";
        public static final String MAIL = "mail";
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String COUNTRY = "country";
        public static final String CITY = "city";
        public static final String PROFILE_PIC = "profile_pic";
        public static final String MESSAGE= "MESSAGE";
        public static final String ID_MESSAGE= "id_message";
        public static final String ID_CHAT = "id_chat";
        public static final String MSG = "msg";
        public static final String PATH = "path";
        public static final String CHAT = "CHAT";
        public static final String ID_SENDER = "id_sender";
        public static final String IP= "ip";
        public static final String IP_GROUP_OWNER = "ip_group_owner";
        public static final String GLOBAL_MESSAGE = "GLOBAL_MESSAGE";
        public static final String DATETIME = "datetime";
        public static final String LAST_MESSAGE = "last_message";
        public static final String NOT_READ_MESSAGE = "not_read_message";
        public static final String ACCEPT = "accept";
        public static final String MESSAGES_ACCEPTED = "message_accept";
        public static final String BACKGROUND_CHAT_IMAGES = "background_chat_images";
        public static final String BACKGROUND_IMAGE = "background_image";
        public static final String REQUEST = "request";
        public static final String PUBLIC_KEY = "public_key";
        public static final String SYMMETRIC_KEY = "symmetric_key";
        public static final String OTHER_GROUP = "other_group";
        public static final String NUMBER_SHARED = "number_shared";
        public static final String TELEGRAM_SHARED = "telegram_shared";
        public static final String WHATSAPP_SHARED = "whatsapp_shared";
        public static final String TELEGRAM_NICK = "telegram_nick";
        public static final String IS_READ = "is_read"; //0 no, 1 si
        public static final String MESSAGE_SENT = "message_sent"; //0 no, 1 si

        /*public static final String GROUPS = "GROUPS";
        public static final String GROUP_NAME = "GROUP_NAME";
        public static final String USERS_GROUP = "USER_GROUP";
        public static final String ID_GROUP = "id_group";
        public static final String GROUP_MESSAGE = "GROUP_MESSAGE";*/
    }

    public class Company implements BaseColumns {
        public static final String USER_PLUS = "USER_PLUS";
        public static final String ID_PLUS = "id_plus";
        public static final String NAME = "name";
        public static final String SURNAME = "surname";
        public static final String VAT_NUMBER = "vat_number";
        public static final String SDI = "sdi";
        public static final String NUMBER = "number";
        public static final String MAIL = "mail";
        public static final String COUNTRY = "country";
        public static final String CITY = "city";
        public static final String STREET = "street";
        public static final String COMPANY_NAME = "company_name";
        public static final String PROMOTION_MESSAGE = "promotion_message";
        public static final String PROMOTION_PAGE = "promotion_page";
    }

    public class ServiceEntry implements BaseColumns {
        public static final String serviceGroupOwner = "SGO"; //fatto
        public static final String serviceClientConnectedToGroupOwner = "CTG";//fatto
        public static final String serviceRequestClientBecomeGroupOwner = "CBG";//fatto
        public static final String serviceLookingForGroupOwner = "LFG";//fatto
        public static final String serviceLookingForGroupOwnerWithSpecifiedId = "LGS";//fatto
        public static final String serviceLookingForGroupOwnerWithGreaterId = "LGG";//fatto
        public static final String servicePlusSearchingNetwork = "PSN";//fatto
        //public static final String serviceUserPlus = "SUP"; IT NEEDS TO BE CREATED ONLY IF WE WANT TO KNOW THE DISTANCE BETWEEN THE USER AND THE COMPANY NEXT TO YOU (NOT DONE)
    }

}
