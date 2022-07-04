package com.java.chat.chatapp.ui.chats;

import static com.java.chat.chatapp.utils.FirebaseUtil.convertTwoUserIDs;
import static com.java.chat.chatapp.utils.LiveDataUtil.addNewItem;
import static com.java.chat.chatapp.utils.LiveDataUtil.updateItemAt;

import androidx.annotation.NonNull;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.java.chat.chatapp.base.BaseViewModel;
import com.java.chat.chatapp.base.Result;
import com.java.chat.chatapp.data.model.Chat;
import com.java.chat.chatapp.data.model.ChatWithUserInfo;
import com.java.chat.chatapp.data.model.UserFriend;
import com.java.chat.chatapp.data.model.UserInfo;
import com.java.chat.chatapp.data.remote.observer.FirebaseReferenceValueObserver;
import com.java.chat.chatapp.data.repository.DatabaseRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ChatsViewModel extends BaseViewModel {

    private final DatabaseRepository databaseRepository;
    private String myUserID;

    private final List<FirebaseReferenceValueObserver> firebaseReferenceObserverList = new ArrayList<>();
    private final MutableLiveData<ChatWithUserInfo> _updatedChatWithUserInfo = new MutableLiveData<>();

    public MediatorLiveData<List<ChatWithUserInfo>> chatsList = new MediatorLiveData<>();

    @Inject
    public ChatsViewModel(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    public void setMyUserID(String myUserID) {
        this.myUserID = myUserID;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        firebaseReferenceObserverList.forEach(FirebaseReferenceValueObserver::clear);
    }

    public void init() {
        chatsList.addSource(_updatedChatWithUserInfo, newChat -> {
            List<ChatWithUserInfo> prevChatsList = chatsList.getValue();
            List<ChatWithUserInfo> newChatsList;
            if (prevChatsList != null) {
                ChatWithUserInfo chat = prevChatsList.stream().filter(it ->
                        it.mChat.info.id.equals(newChat.mChat.info.id)
                ).findFirst().orElse(null);
                newChatsList = chat == null ? addNewItem(prevChatsList, newChat) : updateItemAt(prevChatsList, newChat, prevChatsList.indexOf(chat));
            } else {
                newChatsList = new ArrayList<>();
                newChatsList.add(newChat);
            }
            chatsList.setValue(newChatsList);
        });
        setupChats();
    }

    private void setupChats() {
        loadFriends();
    }

    private void loadFriends() {
        databaseRepository.loadFriends(myUserID, result -> {
            onResult(null, result);
            if (result.states == Result.States.SUCCESS) {
                List<UserFriend> userFriendList = result.data;
                if (userFriendList != null) {
                    userFriendList.forEach(this::loadUserInfo);
                }
            }
        });
    }

    private void loadUserInfo(@NonNull UserFriend userFriend) {
        databaseRepository.loadUserInfo(userFriend.userID, result -> {
            onResult(null, result);
            if (result.states == Result.States.SUCCESS) {
                UserInfo it = result.data;
                if (it != null) {
                    loadAndObserveChat(it);
                }
            }
        });
    }

    private void loadAndObserveChat(@NonNull UserInfo userInfo) {
        FirebaseReferenceValueObserver observer = new FirebaseReferenceValueObserver();
        firebaseReferenceObserverList.add(observer);
        databaseRepository.loadAndObserveChat(convertTwoUserIDs(myUserID, userInfo.id), observer, result -> {
            if (result.states == Result.States.SUCCESS) {
                Chat it = result.data;
                if (it != null) {
                    _updatedChatWithUserInfo.setValue(new ChatWithUserInfo(it, userInfo));
                }
            } else if (result.states == Result.States.ERROR) {
                List<ChatWithUserInfo> prevChatList = chatsList.getValue();
                if (prevChatList != null) {
                    List<ChatWithUserInfo> newList = new ArrayList<>(prevChatList);
                    newList.removeIf(chatWithUserInfo -> result.errorMessage != null && result.errorMessage.contains(chatWithUserInfo.mUserInfo.id));
                    chatsList.setValue(newList);
                }
            }
        });
    }
}
