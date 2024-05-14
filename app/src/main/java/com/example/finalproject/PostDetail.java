package com.example.finalproject;

public class PostDetail {
    private String username;
    private String comment;
    private String avatar;

    public PostDetail(String username, String comment, String avatar)
    {
        this.username = username;
        this.comment = comment;
        this.avatar = avatar;
    }

    public PostDetail()
    {
        username=comment=avatar="";
    }

    //get and set


    public String getUsername() {
        return username;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getComment() {
        return comment;
    }
}
