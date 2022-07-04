package com.java.chat.chatapp.data.model;

import java.util.HashMap;

public class User {
    public UserInfo info;
    public HashMap<String, UserFriend> friends;
    public HashMap<String, UserNotification> notifications;
    public HashMap<String, UserRequest> sentRequests;

    public User() {
        this.info = new UserInfo();
        this.friends = new HashMap<>();
        this.notifications = new HashMap<>();
        this.sentRequests = new HashMap<>();
    }
}
