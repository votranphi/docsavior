package com.example.finalproject;

public class LookupHistory {

    private String lookupContent;

    LookupHistory(String content)
    {
        lookupContent = content;
    }

    LookupHistory()
    {
        lookupContent = "";
    }

    //get set


    public String getLookupContent() {
        return lookupContent;
    }
}
