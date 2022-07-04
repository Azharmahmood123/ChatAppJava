package com.java.chat.chatapp.data.model;

public class Chat {
    public Message lastMessage;
    public ChatInfo info;

    public Chat() {
        this.lastMessage = new Message();
        this.info = new ChatInfo();
    }
}
