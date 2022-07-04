package com.java.chat.chatapp.data.model;

import java.util.Date;

public class Message {
    public String senderID;
    public String text;
    public Long epochTimeMs;
    public Boolean seen;

    public Message() {
        this.senderID = "";
        this.text = "";
        this.epochTimeMs = new Date().getTime();
        this.seen = false;
    }

    public Message(String senderID, String text) {
        this.senderID = senderID;
        this.text = text;
        this.epochTimeMs = new Date().getTime();
        this.seen = false;
    }
}
