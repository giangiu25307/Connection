package com.ConnectionProject.connection.Model;

import java.util.HashMap;
import java.util.Map;

public class Chats {

    private Map<String, Chat> chats;//id,(idSender, msg,data);
    private Map<Integer, GlobalMessage> globals;
    private int count;

    public Chats() {
        chats = new HashMap<>();
        globals = new HashMap<>();
        count = 0;
    }

    public Map<String, Chat> getChats() {
        return chats;
    }

    public void setChats(Chat chat) {
        chats.put(chat.getId(), chat);
    }

    public Chat getChat(String id) {
        return chats.get(id);
    }


    public void deleteChat(String id) {
        chats.remove(id);
    }

    public Map<Integer, GlobalMessage> getAllGlobalMessages() {
        return globals;
    }

    public void deleteAllGlobalMessages() {
        globals.clear();
        count = 0;
    }

    public void deleteGlobalMessage(int number) {
        globals.remove(number);
    }

    public void addGlobalMessage(String idMessage, String idSender, String message, String data, String username) {
        globals.put(count, new GlobalMessage(idMessage, idSender, message, data, username));
        count++;
    }

}

