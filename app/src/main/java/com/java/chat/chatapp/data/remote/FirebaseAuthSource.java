package com.java.chat.chatapp.data.remote;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.java.chat.chatapp.base.Result;
import com.java.chat.chatapp.base.ResultCallback;
import com.java.chat.chatapp.data.model.Register;
import com.java.chat.chatapp.data.model.Login;
import com.java.chat.chatapp.data.remote.observer.FirebaseAuthStateObserver;

import javax.inject.Inject;

public class FirebaseAuthSource {
    private final FirebaseAuth authInstance;

    @Inject
    public FirebaseAuthSource() {
        authInstance = FirebaseAuth.getInstance();
    }

    public Task<AuthResult> loginWithEmailAndPassword(@NonNull Login login) {
        return authInstance.signInWithEmailAndPassword(login.email, login.password);
    }

    public Task<AuthResult> createUser(@NonNull Register register) {
        return authInstance.createUserWithEmailAndPassword(register.email, register.password);
    }

    public void logout() {
        authInstance.signOut();
    }

    public void attachAuthStateObserver(FirebaseAuthStateObserver firebaseAuthStateObserver, ResultCallback<FirebaseUser> b) {
        FirebaseAuth.AuthStateListener listener = attachAuthObserver(b);
        firebaseAuthStateObserver.start(listener, authInstance);
    }

    private FirebaseAuth.AuthStateListener attachAuthObserver(ResultCallback<FirebaseUser> b) {
        return firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                b.invoke(Result.Error("No User"));
            } else {
                b.invoke(Result.Success(firebaseAuth.getCurrentUser()));
            }
        };
    }
}
