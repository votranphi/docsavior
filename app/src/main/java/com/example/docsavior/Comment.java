package com.example.docsavior;

public class Comment {

    private Integer idComment;

    private String username;

    private Integer idPost; //idPost is the ID of newsfeed Entity

    private String commentContent;

    private Long time; // time in Unix

    public Comment()
    {
        this.idComment = 0;
        this.idPost = 0;
        this.username = "";
        this.commentContent = "";
        this.time = 0L;
    }

    public Integer getIdComment()
    {
        return idComment;
    }

    public String getUsername() {
        return username;
    }

    public Integer getIdPost()
    {
        return idPost;
    }

    public String getCommentContent()
    {
        return commentContent;
    }

    public Long getTime() {
        return time;
    }

    public void setIdComment(Integer idComment)
    {
        this.idComment = idComment;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setIdPost(Integer idPost)
    {
        this.idPost = idPost;
    }

    public void setCommentContent(String commentContent)
    {
        this.commentContent = commentContent;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}