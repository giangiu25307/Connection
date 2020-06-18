package com.example.connection.Model;

public class Message {

    private String message;
    private String idSender;
    private String date;

    public Message(String idSender, String message, String date) {
        this.message = message;
        this.idSender = idSender;
        this.date = date;
    }

    public String getMessage() {
        return message;
    }

    public String getIdSender() {
        return idSender;
    }

    public String getDate() {
        return date;
    }
}
