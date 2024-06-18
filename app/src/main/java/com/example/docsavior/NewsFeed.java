package com.example.docsavior;

public class NewsFeed {
    private int id;
    private String username;
    private String postDescription;
    private String postContent;
    private int likeNumber;
    private int dislikeNumber;
    private int commentNumber;
    private String fileData;
    private String fileExtension;

    public NewsFeed(int id, String username, String postDescription, String postContent, int likeNumber, int dislikeNumber, int commentNumber, String fileData, String fileExtension)
    {
        this.id = id;
        this.username = username;
        this.postDescription = postDescription;
        this.postContent = postContent;
        this.likeNumber = likeNumber;
        this.dislikeNumber = dislikeNumber;
        this.commentNumber = commentNumber;
        this.fileData = fileData;
        this.fileExtension = fileExtension;
    }
    public NewsFeed()
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

    public String getFileData() {
        return fileData;
    }

    public String getFileExtension() {
        return fileExtension;
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
