package com.ConnectionProject.connection.Model;

public class LastMessage {

    private String lastMessage, dateTime;

    public LastMessage(String lastMessage, String dateTime) {
        this.lastMessage = lastMessage;
        this.dateTime = dateTime;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
