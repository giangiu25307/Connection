package com.example.connection.Model;

public class Message {

    private String idMessage;
    private String message;
    private String idSender;
    private String date;

    public Message(String idMessage, String idSender, String message, String date) {
        this.idMessage = idMessage;
        this.message = message;
        this.idSender = idSender;
        this.date = date;
    }

    public String getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(String idMessage) {
        this.idMessage = idMessage;
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
