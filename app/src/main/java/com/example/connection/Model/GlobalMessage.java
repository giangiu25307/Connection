package com.example.connection.Model;

public class GlobalMessage extends Message {

    String name;

    public GlobalMessage(String idMessage, String idSender, String message, String date,String name) {
        super(idMessage, idSender, message, date, null);
        this.name=name;
    }

    public String getName() {
        return name;
    }

}
