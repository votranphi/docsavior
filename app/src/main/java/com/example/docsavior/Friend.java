package com.example.docsavior;

public class Friend {
    public String avatar;
    public String username;

    public Friend (String avatar, String username)
    {
        this.avatar = avatar;
        this.username = username;
    }
    public Friend ()
    {
        avatar=username="";
    }

    //get and set

    public String getUsername() {
        return username;
    }

    public String getAvatar() {
        return avatar;
    }
}
