package com.java.chat.chatapp.data.remote.observer;

import androidx.annotation.NonNull;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseReference;

public class FirebaseReferenceChildObserver {
    private ChildEventListener valueEventListener = null;
    private DatabaseReference dbRef = null;
    private Boolean isObserving = false;

    public void start(ChildEventListener valueEventListener, @NonNull DatabaseReference reference) {
        isObserving = true;
        reference.addChildEventListener(valueEventListener);
        this.valueEventListener = valueEventListener;
        this.dbRef = reference;
    }

    public void clear() {
        if (valueEventListener != null && dbRef != null) {
            dbRef.removeEventListener(valueEventListener);
        }
        isObserving = false;
        valueEventListener = null;
        dbRef = null;
    }

    public Boolean isObserving() {
        return isObserving;
    }
}
