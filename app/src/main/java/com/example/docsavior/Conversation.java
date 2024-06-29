package com.example.docsavior;

public class Conversation {
    private String username;
    private String lastMessage;

    public Conversation(String username, String lastMessage) {
        this.username = username;
        this.lastMessage = lastMessage;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
