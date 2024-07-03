package com.example.docsavior;

public class Newsfeed {
    private int id;
    private String username;
    private String postDescription;
    private String postContent;
    private int likeNumber;
    private int dislikeNumber;
    private int commentNumber;
    private String fileData;
    private String fileName;
    private String fileExtension;
    private Long time;

    public Newsfeed(String username, String postDescription, String postContent, String fileData, String fileName, String fileExtension)
    {
        this.id = -1;
        this.username = username;
        this.postDescription = postDescription;
        this.postContent = postContent;
        this.likeNumber = 0;
        this.dislikeNumber = 0;
        this.commentNumber = 0;
        this.fileData = fileData;
        this.fileName = fileName;
        this.fileExtension = fileExtension;
        this.time = System.currentTimeMillis() / 1000L;
    }

    public Newsfeed()
    {
        id = 0;
        username = "";
        postDescription = "";
        postContent = "";
        likeNumber = 0;
        dislikeNumber = 0;
        commentNumber = 0;
        fileData = "";
        fileName = "";
        fileExtension = "";
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

    public String getPostContent() {
        return postContent;
    }

    public int getDislikeNumber() {
        return dislikeNumber;
    }

    public int getLikeNumber() {
        return likeNumber;
    }

    public int getCommentNumber() {
        return commentNumber;
    }

    public String getFileData() {
        return fileData;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public Long getTime() {
        return time;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    public void setPostContent(String postContent) {
        this.postContent = postContent;
    }

    public void setCommentNumber(int commentNumber) {
        this.commentNumber = commentNumber;
    }

    public void setDislikeNumber(int dislikeNumber) {
        this.dislikeNumber = dislikeNumber;
    }

    public void setFileData(String fileData) {
        this.fileData = fileData;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setLikeNumber(int likeNumber) {
        this.likeNumber = likeNumber;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
