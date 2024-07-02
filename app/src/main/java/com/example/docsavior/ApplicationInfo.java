package com.example.docsavior;

// class to store application's info
public class ApplicationInfo {
    public static String apiPath = "http://192.168.1.10:8080/";
    public static String username = "";

    public static String KEY_TO_PROFILE_ACTIVITY = "username";
    public static String KEY_TO_LOOK_UP_POST_USER_ACTIVITY = "lookup_type";
    public static String KEY_TO_CHAT_DETAIL_ACTIVITY = "username1";
    public static String KEY_TO_POST_DETAIL_ACTIVITY = "id";
    public static int LOOK_UP_TYPE_POST = 0;
    public static int LOOK_UP_TYPE_CHAT = 1;
    public static int LOOK_UP_TYPE_USER = 2;

    public static ActivityLifecycleManager activityLifecycleManager = new ActivityLifecycleManager();
}
