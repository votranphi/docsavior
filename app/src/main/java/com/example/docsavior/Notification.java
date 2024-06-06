package com.example.docsavior;

public class Notification {

    private String image;
    private String notiTitle;
    private String notiDes;

    public Notification(String a, String b, String c)
    {
        image = a;
        notiTitle = b;
        notiDes = c;
    }

    public Notification()
    {
        image = notiTitle = notiDes = "";
    }

    // get and set
    public String getNotiTitle()
    {
        return notiTitle;
    }

    public String getNotiDes()
    {
        return notiDes;
    }
}
