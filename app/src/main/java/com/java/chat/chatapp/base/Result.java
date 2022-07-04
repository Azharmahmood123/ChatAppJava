package com.java.chat.chatapp.base;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Result<T> {

    @NonNull
    public final States states;
    @Nullable
    public final T data;
    @Nullable
    public final String errorMessage;

    public Result(@NonNull States states, @Nullable T data, @Nullable String errorMessage) {
        this.states = states;
        this.data = data;
        this.errorMessage = errorMessage;
    }

    @NonNull
    public static <T> Result<T> Loading() {
        return new Result<>(States.LOADING, null, null);
    }

    @NonNull
    public static <T> Result<T> Error(String errorMessage) {
        return new Result<>(States.ERROR, null, errorMessage);
    }

    @NonNull
    public static <T> Result<T> Success(T data) {
        return new Result<>(States.SUCCESS, data, null);
    }

    public enum States {
        SUCCESS,
        LOADING,
        ERROR
    }
}
