package com.example.docsavior;

public class Conversation {
    private String username;
    private String lastMessage;
    private boolean isSeen;

    public Conversation(String username, String lastMessage, boolean isSeen) {
        this.username = username;
        this.lastMessage = lastMessage;
        this.isSeen = isSeen;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getUsername() {
        return username;
    }

    public boolean getIsSeen() {
        return isSeen;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setIsSeen(boolean isSeen) {
        isSeen = isSeen;
    }
}
