package com.example.connection.Model;

public class Message {

    private String idMessage;
    private String message;
    private String idSender;
    private String date;
    private String sent;

    public Message(String idMessage, String idSender, String message, String date, String sent) {
        this.idMessage = idMessage;
        this.idSender = idSender;
        this.message = message;
        this.date = date;
        this.sent = sent;
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

}
