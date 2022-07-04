package com.java.chat.chatapp.data.repository;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseUser;
import com.java.chat.chatapp.base.Result;
import com.java.chat.chatapp.base.ResultCallback;
import com.java.chat.chatapp.data.model.Login;
import com.java.chat.chatapp.data.model.Register;
import com.java.chat.chatapp.data.remote.FirebaseAuthSource;
import com.java.chat.chatapp.data.remote.observer.FirebaseAuthStateObserver;

import javax.inject.Inject;

public class AuthRepository {

    private final FirebaseAuthSource firebaseAuthService;

    @Inject
    public AuthRepository(FirebaseAuthSource firebaseAuthService) {
        this.firebaseAuthService = firebaseAuthService;
    }

    public void observeAuthState(FirebaseAuthStateObserver stateObserver, ResultCallback<FirebaseUser> b) {
        firebaseAuthService.attachAuthStateObserver(stateObserver, b);
    }

    public void loginUser(Login login, @NonNull ResultCallback<FirebaseUser> b) {
        b.invoke(Result.Loading());
        firebaseAuthService.loginWithEmailAndPassword(login).addOnSuccessListener(authResult -> {
            b.invoke(Result.Success(authResult.getUser()));
        }).addOnFailureListener(e -> {
            b.invoke(Result.Error(e.getMessage()));
        });
    }

    public void createUser(Register register, @NonNull ResultCallback<FirebaseUser> b) {
        b.invoke(Result.Loading());
        firebaseAuthService.createUser(register).addOnSuccessListener(authResult -> {
            b.invoke(Result.Success(authResult.getUser()));
        }).addOnFailureListener(e -> {
            b.invoke(Result.Error(e.getMessage()));
        });
    }

    public void logoutUser() {
        firebaseAuthService.logout();
    }
}
