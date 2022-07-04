package com.java.chat.chatapp.base;

public interface ResultCallback<T> {
    void invoke(Result<T> result);
}
