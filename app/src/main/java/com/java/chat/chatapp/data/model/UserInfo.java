package com.java.chat.chatapp.data.model;

public class UserInfo {
    public String id;
    public String displayName;
    public String email;
    public String phone;
    public String status;
    public String profileImageUrl;
    public Boolean online;

    public UserInfo() {
        this.id = "";
        this.displayName = "";
        this.email = "";
        this.phone = "";
        this.status = "No status";
        this.profileImageUrl = "";
        this.online = false;
    }
}
