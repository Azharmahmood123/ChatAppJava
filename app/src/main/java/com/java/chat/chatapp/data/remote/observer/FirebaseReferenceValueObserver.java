package com.java.chat.chatapp.data.remote.observer;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class FirebaseReferenceValueObserver {
    private ValueEventListener valueEventListener = null;
    private DatabaseReference dbRef = null;

    public void start(ValueEventListener valueEventListener, @NonNull DatabaseReference reference) {
        reference.addValueEventListener(valueEventListener);
        this.valueEventListener = valueEventListener;
        this.dbRef = reference;
    }

    public void clear() {
        if (valueEventListener != null && dbRef != null) {
            dbRef.removeEventListener(valueEventListener);
        }
        valueEventListener = null;
        dbRef = null;
    }
}
