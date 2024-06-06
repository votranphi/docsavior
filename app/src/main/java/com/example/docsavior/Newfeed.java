package com.example.docsavior;

public class Newfeed {
    private int id;
    private String username;
    private String postDescription;
    private String postContent;
    private int likeNumber;
    private int dislikeNumber;
    private int commentNumber;

    public Newfeed(int id, String username, String postDescription, String postContent, int likeNumber, int dislikeNumber, int commentNumber)
    {
        this.id = id;
        this.username = username;
        this.postDescription = postDescription;
        this.postContent = postContent;
        this.likeNumber = likeNumber;
        this.dislikeNumber = dislikeNumber;
        this.commentNumber = commentNumber;
    }
    public Newfeed()
    {
        username=postContent=postDescription="";
        dislikeNumber=likeNumber=commentNumber=0;
    }

    //get and set
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPostDescription()
    {
        return postDescription;
    }

    public int getDislikeNumber() {
        return dislikeNumber;
    }

    public int getLikeNumber() {
        return likeNumber;
    }

    public String getPostContent() {
        return postContent;
    }

    public int getCommentNumber() {
        return commentNumber;
    }
}
