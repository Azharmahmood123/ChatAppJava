package com.java.chat.chatapp.ui.profile;

import static com.java.chat.chatapp.utils.FirebaseUtil.convertTwoUserIDs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.java.chat.chatapp.base.BaseViewModel;
import com.java.chat.chatapp.base.Result;
import com.java.chat.chatapp.base.ResultCallback;
import com.java.chat.chatapp.data.model.Chat;
import com.java.chat.chatapp.data.model.LayoutState;
import com.java.chat.chatapp.data.model.Message;
import com.java.chat.chatapp.data.model.User;
import com.java.chat.chatapp.data.model.UserFriend;
import com.java.chat.chatapp.data.model.UserNotification;
import com.java.chat.chatapp.data.model.UserRequest;
import com.java.chat.chatapp.data.remote.observer.FirebaseReferenceValueObserver;
import com.java.chat.chatapp.data.repository.DatabaseRepository;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ProfileViewModel extends BaseViewModel {

    private final DatabaseRepository databaseRepository;
    private String myUserID;
    private String otherUserID;

    private final FirebaseReferenceValueObserver firebaseReferenceObserver = new FirebaseReferenceValueObserver();
    private final MutableLiveData<User> _myUser = new MutableLiveData<>();
    private final MutableLiveData<User> _otherUser = new MutableLiveData<>();

    public LiveData<User> otherUser = _otherUser;
    public MediatorLiveData<LayoutState> layoutState = new MediatorLiveData<>();

    @Inject
    public ProfileViewModel(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    public void setUsersIDs(String myUserID, String otherUserID) {
        this.myUserID = myUserID;
        this.otherUserID = otherUserID;
    }

    public void init() {
        layoutState.addSource(_myUser, user -> {
            updateLayoutState(user, _otherUser.getValue());
        });
        setupProfile();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        firebaseReferenceObserver.clear();
    }

    private void updateLayoutState(User myUser, User otherUser) {
        if (myUser != null && otherUser != null) {
            if (myUser.friends.get(otherUser.info.id) != null) {
                layoutState.setValue(LayoutState.IS_FRIEND);
            } else if (myUser.notifications.get(otherUser.info.id) != null) {
                layoutState.setValue(LayoutState.ACCEPT_DECLINE);
            } else if (myUser.sentRequests.get(otherUser.info.id) != null) {
                layoutState.setValue(LayoutState.REQUEST_SENT);
            } else {
                layoutState.setValue(LayoutState.NOT_FRIEND);
            }
        }
    }

    private void setupProfile() {
        databaseRepository.loadUser(otherUserID, result -> {
            onResult(_otherUser, result);
            if (result.states == Result.States.SUCCESS) {
                databaseRepository.loadAndObserveUser(myUserID, firebaseReferenceObserver, result2 -> {
                    onResult(_myUser, result2);
                });
            }
        });
    }

    public void addFriendPressed() {
        User otherUser = _otherUser.getValue();
        if (otherUser != null) {
            UserRequest userRequest = new UserRequest();
            userRequest.userID = otherUser.info.id;
            databaseRepository.updateNewSentRequest(myUserID, userRequest);

            UserNotification userNotification = new UserNotification();
            userNotification.userID = myUserID;
            databaseRepository.updateNewNotification(otherUser.info.id, userNotification);
        }
    }

    public void removeFriendPressed() {
        User otherUser = _otherUser.getValue();
        if (otherUser != null) {
            databaseRepository.removeFriend(myUserID, otherUser.info.id);
            databaseRepository.removeChat(convertTwoUserIDs(myUserID, otherUser.info.id));
            databaseRepository.removeMessages(convertTwoUserIDs(myUserID, otherUser.info.id));
        }
    }

    public void acceptFriendRequestPressed() {
        User otherUser = _otherUser.getValue();
        if (otherUser != null) {
            databaseRepository.updateNewFriend(new UserFriend(myUserID), new UserFriend(otherUser.info.id));

            Chat newChat = new Chat();
            newChat.info.id = convertTwoUserIDs(myUserID, otherUser.info.id);
            Message message = new Message();
            message.seen = true;
            message.text = "Say Hello!";
            newChat.lastMessage = message;
            databaseRepository.updateNewChat(newChat);

            databaseRepository.removeNotification(myUserID, otherUser.info.id);
            databaseRepository.removeSentRequest(otherUser.info.id, myUserID);
        }
    }

    public void declineFriendRequestPressed() {
        User otherUser = _otherUser.getValue();
        if (otherUser != null) {
            databaseRepository.removeSentRequest(myUserID, otherUser.info.id);
            databaseRepository.removeNotification(myUserID, otherUser.info.id);
        }
    }
}
