package com.java.chat.chatapp.data.model;

public class Register {
    public String displayName;
    public String email;
    public String password;
    public String phone;

    public Register(String displayName, String email, String password, String phone) {
        this.displayName = displayName;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }
}
