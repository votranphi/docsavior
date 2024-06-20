package com.example.docsavior;

public class Notification {

    private String image;
    private String notificationContent;
    private Integer type; // like=0, comment=1, friendRequest=2

    private Integer idPost; // Id post that userInteract interact
    private String usernameInteract; //The person who interacts with post or send a request

    public Notification(String image, String notificationContent)
    {
        this.image = image;
        this.notificationContent = notificationContent;
    }

    public Notification(Integer id,Integer type, String usernameInteract, String username)
    {
        this.type = type;
        this.usernameInteract = usernameInteract;
    }

    public Notification(Integer id, Integer type, String userInteract, String username, Integer idPost)
    {

        this.type = type;
        this.usernameInteract = userInteract;
        this.idPost = idPost;
    }

    // get and set

    public String getImage() {
        return image;
    }

    public String getNotificationContent() {
        return notificationContent;
    }

    public Integer getIdPost() {
        return idPost;
    }

    public Integer getType() {
        return type;
    }

    public String getUsernameInteract() {
        return usernameInteract;
    }


    public void setImage(String image) {
        this.image = image;
    }

    public void setNotificationContent(String notificationContent) {
        this.notificationContent = notificationContent;
    }

    public void setIdPost(Integer idPost) {
        this.idPost = idPost;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public void setUserInteract(String userInteract) {
        this.usernameInteract = userInteract;
    }

}

