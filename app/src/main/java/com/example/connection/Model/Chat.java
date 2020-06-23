package com.example.connection.Model;

import java.util.HashMap;
import java.util.Map;

public class Chat {

    private String name, id;
    private int numberMsg;
    private Map<Integer, Message> message;

    public Chat(String id, String name) {
        numberMsg = 0;
        this.name = name;
        this.id = id;
        message = new HashMap<>();
    }

    public void addMsg(String id, String idSender, String msg, String data) {
        message.put(numberMsg, new Message(idSender, msg, data));
        numberMsg++;
    }

    public Map<Integer, Message> getAllMessage() {
        return message;
    }

    public String[] getLastMessage() {
        String[] array = {message.get(message.size() - 1).getMessage(), message.get(message.size() - 1).getDate()};
        return array;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void deleteAllMsg() {
        message.clear();
        numberMsg = 0;
    }

    public void deleteMsg(int number) {
        message.remove(number);
    }

}
