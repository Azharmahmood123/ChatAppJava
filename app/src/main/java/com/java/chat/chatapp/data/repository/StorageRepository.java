package com.java.chat.chatapp.data.repository;

import android.net.Uri;

import com.java.chat.chatapp.base.Result;
import com.java.chat.chatapp.base.ResultCallback;
import com.java.chat.chatapp.data.remote.FirebaseStorageSource;

import javax.inject.Inject;

public class StorageRepository {

    private final FirebaseStorageSource firebaseStorageService;

    @Inject
    public StorageRepository(FirebaseStorageSource firebaseStorageService) {
        this.firebaseStorageService = firebaseStorageService;
    }

    public void updateUserProfileImage(String userID, byte[] byteArray, ResultCallback<Uri> b) {
        b.invoke(Result.Loading());
        firebaseStorageService.uploadUserImage(userID, byteArray).addOnSuccessListener(uri -> b.invoke((Result.Success(uri))))
                .addOnFailureListener(e -> b.invoke(Result.Error(e.getMessage())));
    }
}
