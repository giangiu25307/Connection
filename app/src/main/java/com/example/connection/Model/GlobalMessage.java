package com.example.connection.Model;

public class GlobalMessage extends Message {

    String name;

    public GlobalMessage(String idSender, String message, String date,String name) {
        super(idSender, message, date);
        this.name=name;
    }

    public String getName() {
        return name;
    }

}
