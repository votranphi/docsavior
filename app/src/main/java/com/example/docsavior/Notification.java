package com.example.docsavior;

public class Notification {
    private Integer idNotification; // Notification's id

    private String username; //username of this account

    private Integer type; // like = 0, dislike = 1, comment = 2, friend request = 3, accept friend request = 4, reject friend request = 5

    private Integer idPost; // post's id. This can be -1 if type is 2

    private String interacter; // username of the person who interact with post or send a request

    private String notificationContent; // "has liked/disliked your post", "has left a comment on your post", "has sent you a friend request"

    private Long time; // in Unix

    public Notification() { }

    public Integer getIdNotification() {
        return idNotification;
    }

    public Integer getIdPost() {
        return idPost;
    }

    public String getInteracter() {
        return interacter;
    }

    public String getNotificationContent() {
        return notificationContent;
    }

    public Long getTime() {
        return time;
    }

    public Integer getType() {
        return type;
    }

    public String getUsername() {
        return username;
    }

    public void setIdNotification(Integer idNotification) {
        this.idNotification = idNotification;
    }

    public void setIdPost(Integer idPost) {
        this.idPost = idPost;
    }

    public void setInteracter(String interacter) {
        this.interacter = interacter;
    }

    public void setNotificationContent(String notificationContent) {
        this.notificationContent = notificationContent;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}

