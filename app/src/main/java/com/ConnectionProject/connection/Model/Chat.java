package com.ConnectionProject.connection.Model;

import java.util.HashMap;
import java.util.Map;

public class Chat {

    private String id, name, lastMessage, dateTime;
    private int notReadMessage, numberMsg;
    private Map<Integer, Message> message;

    public Chat(String id, String name, String lastMessage, String dateTime) {
        this.id = id;
        this.name = name;
        this.lastMessage = lastMessage;
        this.dateTime = dateTime;
        message = new HashMap<>();
        numberMsg = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public int getNotReadMessage() {
        return notReadMessage;
    }

    public void setNotReadMessage(int notReadMessage) {
        this.notReadMessage = notReadMessage;
    }

    public void addMsg(String idSender, String msg, String data) {
        //TODO Da revisionare, commentata perché da errore per l'idmessage e forse il metodo non verrà più usato
        //message.put(numberMsg, new Message(idSender, msg, data));
        numberMsg++;
    }

    public Map<Integer, Message> getAllMessage() {
        return message;
    }

    public void deleteAllMsg() {
        message.clear();
        numberMsg = 0;
    }

    public void deleteMsg(int number) {
        message.remove(number);
    }
}
