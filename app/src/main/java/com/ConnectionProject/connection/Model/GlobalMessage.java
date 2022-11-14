package com.ConnectionProject.connection.Model;

public class GlobalMessage extends Message {

    String username;

    public GlobalMessage(String idMessage, String idSender, String message, String date, String username) {
        super(idMessage, idSender, message, date, null);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

}
