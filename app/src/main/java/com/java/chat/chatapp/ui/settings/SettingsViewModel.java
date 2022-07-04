package com.java.chat.chatapp.ui.settings;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.java.chat.chatapp.base.BaseViewModel;
import com.java.chat.chatapp.base.Event;
import com.java.chat.chatapp.base.Result;
import com.java.chat.chatapp.data.model.User;
import com.java.chat.chatapp.data.model.UserInfo;
import com.java.chat.chatapp.data.remote.observer.FirebaseReferenceValueObserver;
import com.java.chat.chatapp.data.repository.AuthRepository;
import com.java.chat.chatapp.data.repository.DatabaseRepository;
import com.java.chat.chatapp.data.repository.StorageRepository;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import kotlin.Unit;

@HiltViewModel
public class SettingsViewModel extends BaseViewModel {

    private final AuthRepository authRepository;
    private final DatabaseRepository databaseRepository;
    private final StorageRepository storageRepository;
    private String myUserID;

    private final MutableLiveData<UserInfo> _userInfo = new MutableLiveData<>();
    private final MutableLiveData<Event<Boolean>> _logoutEvent = new MutableLiveData<>();
    private final FirebaseReferenceValueObserver firebaseReferenceObserver = new FirebaseReferenceValueObserver();

    public LiveData<UserInfo> userInfo = _userInfo;
    public LiveData<Event<Boolean>> logoutEvent = _logoutEvent;

    @Inject
    public SettingsViewModel(AuthRepository authRepository, DatabaseRepository databaseRepository, StorageRepository storageRepository) {
        this.authRepository = authRepository;
        this.databaseRepository = databaseRepository;
        this.storageRepository = storageRepository;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        firebaseReferenceObserver.clear();
    }

    public void setMyUserID(String myUserID) {
        this.myUserID = myUserID;
    }

    public void init() {
        loadAndObserveUserInfo();
    }

    private void loadAndObserveUserInfo() {
        databaseRepository.loadAndObserveUserInfo(myUserID, firebaseReferenceObserver, result -> onResult(_userInfo, result));
    }

    public void changeUserStatus(String status) {
        databaseRepository.updateUserStatus(myUserID, status);
    }

    public void changeUserImage(byte[] byteArray) {
        storageRepository.updateUserProfileImage(myUserID, byteArray, result -> {
            onResult(null, result);
            if (result.states == Result.States.SUCCESS) {
                Uri imageUri = result.data;
                if (imageUri != null) {
                    databaseRepository.updateUserProfileImageUrl(myUserID, imageUri.toString());
                }
            }
        });
    }

    public void logoutUserPressed() {
        authRepository.logoutUser();
        _logoutEvent.setValue(new Event<>(true));
    }

}
