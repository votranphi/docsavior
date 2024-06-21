package com.example.docsavior;

public class Friend {
    private String avatarData;
    private String username;

    public Friend (String avatarData, String username)
    {
        this.avatarData = avatarData;
        this.username = username;
    }
    public Friend ()
    {
        avatarData = "";
        username = "";
    }

    //get and set

    public String getUsername() {
        return username;
    }

    public String getAvatarData() {
        return avatarData;
    }
}
