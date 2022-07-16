package com.example.connection.Model;

public class Message {

    private String idMessage;
    private String message;
    private String idSender;
    private String date;
    private String sent;
    private String username;

    public Message(String idMessage, String idSender, String message, String date, String sent, String username) {
        this.idMessage = idMessage;
        this.idSender = idSender;
        this.message = message;
        this.date = date;
        this.sent = sent;
        this.username = username;
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

    public void setSent(String sent){
        this.sent = sent;
    }

    public String getSent(){
        return sent;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
