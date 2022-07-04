package com.java.chat.chatapp.data.remote.observer;

import com.google.firebase.auth.FirebaseAuth;

public class FirebaseAuthStateObserver {
    private FirebaseAuth.AuthStateListener authListener = null;
    private FirebaseAuth instance = null;

    public void start(FirebaseAuth.AuthStateListener valueEventListener, FirebaseAuth instance) {
        this.authListener = valueEventListener;
        this.instance = instance;
        if (this.instance != null) {
            this.instance.addAuthStateListener(authListener);
        }
    }

    public void clear() {
        if (authListener != null && instance != null) {
            instance.removeAuthStateListener(authListener);
        }
    }
}
