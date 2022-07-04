package com.java.chat.chatapp.data.remote.observer;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseReferenceConnectedObserver {
    private ValueEventListener valueEventListener = null;
    private DatabaseReference dbRef = null;
    private DatabaseReference userRef = null;

    public void start(String userID) {
        String pathString = "users/" + userID + "/info/online";
        this.userRef = FirebaseDatabase.getInstance().getReference().child(pathString);
        this.valueEventListener = getEventListener(userID);
        this.dbRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        this.dbRef.addValueEventListener(valueEventListener);
    }

    private ValueEventListener getEventListener(String userID) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean connected = snapshot.getValue(Boolean.class);
                if (connected != null) {
                    if (connected) {
                        String pathString = "users/" + userID + "/info/online";
                        FirebaseDatabase.getInstance().getReference().child(pathString).setValue(true);
                        if (userRef != null) {
                            userRef.onDisconnect().setValue(false);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    public void clear() {
        if (valueEventListener != null && dbRef != null) {
            dbRef.removeEventListener(valueEventListener);
        }
        if (userRef != null) {
            userRef.setValue(false);
        }
        valueEventListener = null;
        dbRef = null;
        userRef = null;
    }
}
