package com.java.chat.chatapp.utils;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FirebaseUtil {

    public static <T> T wrapSnapshotToClass(Class<T> className, @NonNull DataSnapshot snap) {
        return snap.getValue(className);
    }

    @NonNull
    public static <T> List<T> wrapSnapshotToArrayList(Class<T> className, @NonNull DataSnapshot snap) {
        List<T> arrayList = new ArrayList<>();
        for (DataSnapshot child : snap.getChildren()) {
            T value = child.getValue(className);
            if (value != null) {
                arrayList.add(value);
            }
        }
        return arrayList;
    }

    // Always returns the same combined id when comparing the two users id's
    @NonNull
    public static String convertTwoUserIDs(@NonNull String userID1, String userID2) {
        if (userID1.compareTo(userID2) < 0) {
            return userID2 + userID1;
        } else {
            return userID1 + userID2;
        }
    }
}
