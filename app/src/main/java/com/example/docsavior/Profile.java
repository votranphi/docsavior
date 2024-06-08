package com.example.docsavior;

import java.util.ArrayList;
import java.util.List;

public class Profile {

    private String username;

    private String image;

    private List<String> posts;

    public Profile()
    {
        username = image = "";
    }
    public Profile(String username, String image, List<String> posts)
    {
        this.username = username;
        this.image = image;
        this.posts = posts;
    }
    public String getUsername() {
        return username;
    }

    public String getImage() {
        return image;
    }

    public List<String> getPosts() {
        return posts;
    }
}
