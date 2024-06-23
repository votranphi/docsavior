package com.example.docsavior;

public class PostDetail {
    private String username;
    private String comment;
    private long time; // time in Unix in second

    public PostDetail(String username, String comment, long time)
    {
        this.username = username;
        this.comment = comment;
        this.time = time;
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

    public long getTime() {
        return time;
    }
}
