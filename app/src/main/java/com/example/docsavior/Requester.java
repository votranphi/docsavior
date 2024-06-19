package com.example.docsavior;

public class Requester {
    private String[] requesters;

    public Requester(String[] requesters) {
        this.requesters = requesters;
    }

    public String[] getRequesters() {
        return requesters;
    }

    public void setRequesters(String[] requesters) {
        this.requesters = requesters;
    }
}
