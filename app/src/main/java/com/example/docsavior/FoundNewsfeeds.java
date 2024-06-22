package com.example.docsavior;

public class FoundNewsfeeds {
    private NewsFeed[] foundNewsfeeds;

    public FoundNewsfeeds(NewsFeed[] foundNewsfeeds) {
        this.foundNewsfeeds = foundNewsfeeds;
    }

    public NewsFeed[] getFoundNewsfeeds() {
        return foundNewsfeeds;
    }

    public void setFoundNewsfeeds(NewsFeed[] foundNewsfeeds) {
        this.foundNewsfeeds = foundNewsfeeds;
    }
}
