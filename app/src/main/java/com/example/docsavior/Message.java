package com.example.docsavior;

public class Message {
    private String message;
    private String image;
    public Message(String message, String image) {
        this.message = message; this.image = image;
    }

    public String getMessage() {
        return message;
    }

    public String getImage() {
        return image;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
