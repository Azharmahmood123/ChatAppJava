package com.java.chat.chatapp.data.remote;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import javax.inject.Inject;

public class FirebaseStorageSource {

    private final FirebaseStorage storageInstance;

    @Inject
    public FirebaseStorageSource() {
        storageInstance = FirebaseStorage.getInstance();
    }

    public Task<Uri> uploadUserImage(String userID, byte[] bArr) {
        String pathString = "user_photos/" + userID + "/profile_image";
        StorageReference ref = storageInstance.getReference().child(pathString);
        return ref.putBytes(bArr).continueWithTask(task -> ref.getDownloadUrl());
    }
}
