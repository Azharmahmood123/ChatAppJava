package com.java.chat.chatapp.utils;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class LiveDataUtil {

    @NonNull
    public static <T> List<T> addNewItem(@NonNull List<T> previousList, T item) {
        List<T> newList = new ArrayList<>(previousList);
        newList.add(item);
        return newList;
    }

    @NonNull
    public static <T> List<T> updateItemAt(@NonNull List<T> previousList, T item, int index) {
        List<T> newList = new ArrayList<>(previousList);
        newList.set(index, item);
        return newList;
    }

    @NonNull
    public static <T> List<T> removeItem(@NonNull List<T> previousList, T item) {
        List<T> newList = new ArrayList<>(previousList);
        newList.remove(item);
        return newList;
    }

}
