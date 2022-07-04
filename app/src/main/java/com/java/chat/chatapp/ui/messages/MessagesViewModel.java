package com.java.chat.chatapp.ui.messages;

import static com.java.chat.chatapp.utils.LiveDataUtil.addNewItem;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.java.chat.chatapp.base.BaseViewModel;
import com.java.chat.chatapp.base.Result;
import com.java.chat.chatapp.data.model.Message;
import com.java.chat.chatapp.data.model.UserInfo;
import com.java.chat.chatapp.data.remote.observer.FirebaseReferenceChildObserver;
import com.java.chat.chatapp.data.remote.observer.FirebaseReferenceValueObserver;
import com.java.chat.chatapp.data.repository.DatabaseRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class MessagesViewModel extends BaseViewModel {

    private final DatabaseRepository databaseRepository;
    private String myUserID;
    private String otherUserID;
    private String chatID;

    private final FirebaseReferenceChildObserver fbRefMessagesChildObserver = new FirebaseReferenceChildObserver();
    private final FirebaseReferenceValueObserver fbRefUserInfoObserver = new FirebaseReferenceValueObserver();
    private final MutableLiveData<UserInfo> _otherUser = new MutableLiveData<>();
    private final MutableLiveData<Message> _addedMessage = new MutableLiveData<>();

    public MediatorLiveData<List<Message>> messagesList = new MediatorLiveData<>();
    public LiveData<UserInfo> otherUser = _otherUser;

    @Inject

    public MessagesViewModel(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    public void setUsersIDs(String myUserID, String otherUserID, String chatID) {
        this.myUserID = myUserID;
        this.otherUserID = otherUserID;
        this.chatID = chatID;
    }

    public void init() {
        setupChat();
        checkAndUpdateLastMessageSeen();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        fbRefUserInfoObserver.clear();
        fbRefMessagesChildObserver.clear();
    }

    private void checkAndUpdateLastMessageSeen() {
        databaseRepository.loadChat(chatID, result -> {
            if (result.states == Result.States.SUCCESS && result.data != null) {
                Message lastMessage = result.data.lastMessage;
                if (!lastMessage.seen && !lastMessage.senderID.equals(myUserID)) {
                    lastMessage.seen = true;
                    databaseRepository.updateChatLastMessage(chatID, lastMessage);
                }
            }
        });
    }

    private void setupChat() {
        databaseRepository.loadAndObserveUserInfo(otherUserID, fbRefUserInfoObserver, result -> {
            onResult(_otherUser, result);
            if (result.states == Result.States.SUCCESS && !fbRefMessagesChildObserver.isObserving()) {
                loadAndObserveNewMessages();
            }
        });
    }

    private void loadAndObserveNewMessages() {
        messagesList.addSource(_addedMessage, message -> {
            List<Message> prevMessagesList = messagesList.getValue();
            List<Message> newMessagesList;
            if (prevMessagesList != null) {
                newMessagesList = addNewItem(prevMessagesList, message);
            } else {
                newMessagesList = new ArrayList<>();
                newMessagesList.add(message);
            }
            messagesList.setValue(newMessagesList);
        });

//        databaseRepository.loadAndObserveMessagesAdded(chatID, fbRefMessagesChildObserver, result -> onResult(_addedMessage, result));
        databaseRepository.loadAndObserveMessagesAdded(chatID, fbRefMessagesChildObserver, result -> {
            Log.d("TAG", result.data.text);
            onResult(_addedMessage, result);
        });
    }

    public void sendMessagePressed(String newMessageText) {
        Message newMsg = new Message(myUserID, newMessageText);
        databaseRepository.updateNewMessage(chatID, newMsg);
        databaseRepository.updateChatLastMessage(chatID, newMsg);
    }
}
