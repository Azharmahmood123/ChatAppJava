package com.java.chat.chatapp.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.java.chat.chatapp.base.BaseViewModel;
import com.java.chat.chatapp.base.Result;
import com.java.chat.chatapp.data.model.UserNotification;
import com.java.chat.chatapp.data.remote.observer.FirebaseAuthStateObserver;
import com.java.chat.chatapp.data.remote.observer.FirebaseReferenceConnectedObserver;
import com.java.chat.chatapp.data.remote.observer.FirebaseReferenceValueObserver;
import com.java.chat.chatapp.data.repository.AuthRepository;
import com.java.chat.chatapp.data.repository.DatabaseRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class DashboardViewModel extends BaseViewModel {

    private final AuthRepository authRepository;
    private final DatabaseRepository databaseRepository;
    private String myUserID;

    private final FirebaseReferenceValueObserver fbRefNotificationsObserver = new FirebaseReferenceValueObserver();
    private final FirebaseAuthStateObserver fbAuthStateObserver = new FirebaseAuthStateObserver();
    private final FirebaseReferenceConnectedObserver fbRefConnectedObserver = new FirebaseReferenceConnectedObserver();

    private final MutableLiveData<List<UserNotification>> _userNotificationsList = new MutableLiveData<>();
    public LiveData<List<UserNotification>> userNotificationsList = _userNotificationsList;

    @Inject
    public DashboardViewModel(AuthRepository authRepository, DatabaseRepository databaseRepository) {
        this.authRepository = authRepository;
        this.databaseRepository = databaseRepository;
    }

    public void setMyUserID(String myUserID) {
        this.myUserID = myUserID;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        fbRefNotificationsObserver.clear();
        fbRefConnectedObserver.clear();
        fbAuthStateObserver.clear();
    }

    public void setupAuthObserver() {
        authRepository.observeAuthState(fbAuthStateObserver, result -> {
            if (result.states == Result.States.SUCCESS) {
                if (result.data != null) {
                    myUserID = result.data.getUid();
                }
                startObservingNotifications();
                fbRefConnectedObserver.start(myUserID);
            } else {
                fbRefConnectedObserver.clear();
                stopObservingNotifications();
            }
        });
    }

    private void startObservingNotifications() {
        databaseRepository.loadAndObserveUserNotifications(myUserID, fbRefNotificationsObserver, result -> {
            if (result.states == Result.States.SUCCESS) {
                if (result.data != null) {
                    _userNotificationsList.setValue(result.data);
                }
            }
        });
    }

    private void stopObservingNotifications() {
        fbRefNotificationsObserver.clear();
    }
}
