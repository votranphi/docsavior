package com.example.docsavior;

public class PostDetail {
    private String username;
    private String comment;

    public PostDetail(String username, String comment)
    {
        this.username = username;
        this.comment = comment;
    }

    public PostDetail()
    {
        username=comment="";
    }

    //get and set


    public String getUsername() {
        return username;
    }

    public String getComment() {
        return comment;
    }
}
