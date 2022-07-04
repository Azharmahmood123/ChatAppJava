package com.java.chat.chatapp.data.repository;

import static com.java.chat.chatapp.utils.FirebaseUtil.wrapSnapshotToArrayList;
import static com.java.chat.chatapp.utils.FirebaseUtil.wrapSnapshotToClass;

import androidx.annotation.NonNull;

import com.java.chat.chatapp.base.Result;
import com.java.chat.chatapp.base.ResultCallback;
import com.java.chat.chatapp.data.model.Chat;
import com.java.chat.chatapp.data.model.Message;
import com.java.chat.chatapp.data.model.User;
import com.java.chat.chatapp.data.model.UserFriend;
import com.java.chat.chatapp.data.model.UserInfo;
import com.java.chat.chatapp.data.model.UserNotification;
import com.java.chat.chatapp.data.model.UserRequest;
import com.java.chat.chatapp.data.remote.FirebaseDataSource;
import com.java.chat.chatapp.data.remote.observer.FirebaseReferenceChildObserver;
import com.java.chat.chatapp.data.remote.observer.FirebaseReferenceValueObserver;

import java.util.List;

import javax.inject.Inject;

import kotlin.Unit;

public class DatabaseRepository {
    private final FirebaseDataSource firebaseDatabaseService;

    @Inject
    public DatabaseRepository(FirebaseDataSource firebaseDatabaseService) {
        this.firebaseDatabaseService = firebaseDatabaseService;
    }

    //region update

    public void updateUserStatus(String userID, String status) {
        firebaseDatabaseService.updateUserStatus(userID, status);
    }

    public void updateNewMessage(String messagesID, Message message) {
        firebaseDatabaseService.pushNewMessage(messagesID, message);
    }

    public void updateNewUser(User user) {
        firebaseDatabaseService.updateNewUser(user);
    }

    public void updateNewFriend(UserFriend myUser, UserFriend otherUser) {
        firebaseDatabaseService.updateNewFriend(myUser, otherUser);
    }

    public void updateNewSentRequest(String userID, UserRequest userRequest) {
        firebaseDatabaseService.updateNewSentRequest(userID, userRequest);
    }

    public void updateNewNotification(String otherUserID, UserNotification userNotification) {
        firebaseDatabaseService.updateNewNotification(otherUserID, userNotification);
    }

    public void updateChatLastMessage(String chatID, Message message) {
        firebaseDatabaseService.updateLastMessage(chatID, message);
    }

    public void updateNewChat(Chat chat) {
        firebaseDatabaseService.updateNewChat(chat);
    }

    public void updateUserProfileImageUrl(String userID, String url) {
        firebaseDatabaseService.updateUserProfileImageUrl(userID, url);
    }

    //endregion

    //region Remove
    public void removeNotification(String userID, String notificationID) {
        firebaseDatabaseService.removeNotification(userID, notificationID);
    }

    public void removeFriend(String userID, String friendID) {
        firebaseDatabaseService.removeFriend(userID, friendID);
    }

    public void removeSentRequest(String otherUserID, String myUserID) {
        firebaseDatabaseService.removeSentRequest(otherUserID, myUserID);
    }

    public void removeChat(String chatID) {
        firebaseDatabaseService.removeChat(chatID);
    }

    public void removeMessages(String messagesID) {
        firebaseDatabaseService.removeMessages(messagesID);
    }

    //endregion

    //region Load Single

    public void loadUser(String userID, @NonNull ResultCallback<User> b) {
        b.invoke(Result.Loading());
        firebaseDatabaseService.loadUserTask(userID)
                .addOnSuccessListener(dataSnapshot -> b.invoke(Result.Success(wrapSnapshotToClass(User.class, dataSnapshot))))
                .addOnFailureListener(e -> b.invoke(Result.Error(e.getMessage())));
    }

    public void loadUserInfo(String userID, ResultCallback<UserInfo> b) {
        firebaseDatabaseService.loadUserInfoTask(userID)
                .addOnSuccessListener(dataSnapshot -> b.invoke(Result.Success(wrapSnapshotToClass(UserInfo.class, dataSnapshot))))
                .addOnFailureListener(e -> b.invoke(Result.Error(e.getMessage())));
    }

    public void loadChat(String chatID, @NonNull ResultCallback<Chat> b) {
        b.invoke(Result.Loading());
        firebaseDatabaseService.loadChatTask(chatID).addOnSuccessListener(dataSnapshot -> b.invoke(Result.Success(wrapSnapshotToClass(Chat.class, dataSnapshot))))
                .addOnFailureListener(e -> b.invoke(Result.Error(e.getMessage())));
    }

    //region load list

    public void loadUsers(@NonNull ResultCallback<List<User>> b) {
        b.invoke(Result.Loading());
        firebaseDatabaseService.loadUsersTask().addOnSuccessListener(dataSnapshot -> {
            List<User> usersList = wrapSnapshotToArrayList(User.class, dataSnapshot);
            b.invoke(Result.Success(usersList));
        }).addOnFailureListener(e -> {
            b.invoke(Result.Error(e.getMessage()));
        });
    }

    public void loadFriends(String userID, @NonNull ResultCallback<List<UserFriend>> b) {
        b.invoke(Result.Loading());
        firebaseDatabaseService.loadFriendsTask(userID).addOnSuccessListener(dataSnapshot -> {
            List<UserFriend> friendsList = wrapSnapshotToArrayList(UserFriend.class, dataSnapshot);
            b.invoke(Result.Success(friendsList));
        }).addOnFailureListener(e -> b.invoke(Result.Error(e.getMessage())));
    }

    public void loadNotifications(String userID, @NonNull ResultCallback<List<UserNotification>> b) {
        b.invoke(Result.Loading());
        firebaseDatabaseService.loadNotificationsTask(userID).addOnSuccessListener(dataSnapshot -> {
            List<UserNotification> notificationsList = wrapSnapshotToArrayList(UserNotification.class, dataSnapshot);
            b.invoke(Result.Success(notificationsList));
        }).addOnFailureListener(e -> b.invoke(Result.Error(e.getMessage())));
    }

    //endregion

    //#region Load and Observe

    public void loadAndObserveUserNotifications(String userID, FirebaseReferenceValueObserver observer, ResultCallback<List<UserNotification>> b) {
        firebaseDatabaseService.attachUserNotificationsObserver(UserNotification.class, userID, observer, b);
    }

    public void loadAndObserveUser(String userID, FirebaseReferenceValueObserver observer, ResultCallback<User> b) {
        firebaseDatabaseService.attachUserObserver(User.class, userID, observer, b);
    }

    public void loadAndObserveUserInfo(String userID, FirebaseReferenceValueObserver observer, ResultCallback<UserInfo> b) {
        firebaseDatabaseService.attachUserInfoObserver(UserInfo.class, userID, observer, b);
    }

    public void loadAndObserveMessagesAdded(String chatID, FirebaseReferenceChildObserver observer, ResultCallback<Message> b) {
        firebaseDatabaseService.attachMessagesObserver(Message.class, chatID, observer, b);
    }

    public void loadAndObserveChat(String chatID, FirebaseReferenceValueObserver observer, ResultCallback<Chat> b) {
        firebaseDatabaseService.attachChatObserver(Chat.class, chatID, observer, b);
    }

    //endregion
}
