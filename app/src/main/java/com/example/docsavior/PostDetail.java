package com.example.docsavior;

public class PostDetail {
    private int idComment;
    private String username;
    private String comment;
    private long time; // time in Unix in second

    public PostDetail(int idComment, String username, String comment, long time)
    {
        this.idComment = idComment;
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

    public int getIdComment() {
        return idComment;
    }
}
