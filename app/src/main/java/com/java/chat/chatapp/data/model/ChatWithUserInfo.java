package com.java.chat.chatapp.data.model;

public class ChatWithUserInfo {
    public Chat mChat;
    public UserInfo mUserInfo;

    public ChatWithUserInfo() {
        this.mChat = new Chat();
        this.mUserInfo = new UserInfo();
    }

    public ChatWithUserInfo(Chat mChat, UserInfo mUserInfo) {
        this.mChat = mChat;
        this.mUserInfo = mUserInfo;
    }
}
