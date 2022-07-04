package com.java.chat.chatapp.data.remote;

import static com.java.chat.chatapp.utils.FirebaseUtil.wrapSnapshotToClass;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.java.chat.chatapp.base.Result;
import com.java.chat.chatapp.base.ResultCallback;
import com.java.chat.chatapp.data.model.Chat;
import com.java.chat.chatapp.data.model.Message;
import com.java.chat.chatapp.data.model.User;
import com.java.chat.chatapp.data.model.UserFriend;
import com.java.chat.chatapp.data.model.UserNotification;
import com.java.chat.chatapp.data.model.UserRequest;
import com.java.chat.chatapp.data.remote.observer.FirebaseReferenceChildObserver;
import com.java.chat.chatapp.data.remote.observer.FirebaseReferenceValueObserver;
import com.java.chat.chatapp.utils.FirebaseUtil;

import java.util.List;

import javax.inject.Inject;

public class FirebaseDataSource {
    public final FirebaseDatabase dbInstance;

    @Inject
    public FirebaseDataSource() {
        this.dbInstance = FirebaseDatabase.getInstance();
    }

    //private region

    @NonNull
    private DatabaseReference refToPath(String path) {
        return dbInstance.getReference().child(path);
    }

    private ValueEventListener attachValueListenerToTaskCompletion(TaskCompletionSource<DataSnapshot> src) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                src.setResult(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                src.setException(new Exception(error.getMessage()));
            }
        };
    }

    private <T> ValueEventListener attachValueListenerToBlock(Class<T> resultClassName, ResultCallback<T> b) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (wrapSnapshotToClass(resultClassName, snapshot) == null) {
                    b.invoke(Result.Error(snapshot.getKey()));
                } else {
                    b.invoke(Result.Success(wrapSnapshotToClass(resultClassName, snapshot)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                b.invoke(Result.Error(error.getMessage()));
            }
        };
    }

    private <T> ValueEventListener attachValueListenerToBlockWithList(Class<T> resultClassName, ResultCallback<List<T>> b) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                b.invoke(Result.Success(FirebaseUtil.wrapSnapshotToArrayList(resultClassName, snapshot)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                b.invoke(Result.Error(error.getMessage()));
            }
        };
    }

    private <T> ChildEventListener attachChildListenerToBlock(Class<T> resultClassName, ResultCallback<T> b) {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                b.invoke(Result.Success(wrapSnapshotToClass(resultClassName, snapshot)));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                b.invoke(Result.Error(error.getMessage()));
            }
        };
    }

    //endregion

    //region update

    public void updateUserProfileImageUrl(String userID, String url) {
        String pathString = "users/" + userID + "/info/profileImageUrl";
        refToPath(pathString).setValue(url);
    }

    public void updateUserStatus(String userID, String status) {
        String pathString = "users/" + userID + "/info/status";
        refToPath(pathString).setValue(status);
    }

    public void updateLastMessage(String chatID, Message message) {
        String pathString = "chats/" + chatID + "/lastMessage";
        refToPath(pathString).setValue(message);
    }

    public void updateNewFriend(@NonNull UserFriend myUser, @NonNull UserFriend otherUser) {
        String pathString1 = "users/" + myUser.userID + "/friends/" + otherUser.userID;
        refToPath(pathString1).setValue(otherUser);
        String pathString2 = "users/" + otherUser.userID + "/friends/" + myUser.userID;
        refToPath(pathString2).setValue(myUser);
    }

    public void updateNewSentRequest(String userID, @NonNull UserRequest userRequest) {
        String pathString = "users/" + userID + "/sentRequests/" + userRequest.userID;
        refToPath(pathString).setValue(userRequest);
    }

    public void updateNewNotification(String otherUserID, @NonNull UserNotification userNotification) {
        String pathString = "users/" + otherUserID + "/notifications/" + userNotification.userID;
        refToPath(pathString).setValue(userNotification);
    }

    public void updateNewUser(@NonNull User user) {
        String pathString = "users/" + user.info.id;
        refToPath(pathString).setValue(user);
    }

    public void updateNewChat(@NonNull Chat chat) {
        String pathString = "chats/" + chat.info.id;
        refToPath(pathString).setValue(chat);
    }

    public void pushNewMessage(String messagesID, Message message) {
        String pathString = "messages/" + messagesID;
        refToPath(pathString).push().setValue(message);
    }

    //endregion

    //region Remove

    public void removeNotification(String userID, String notificationID) {
        String pathString = "users/" + userID + "/notifications/" + notificationID;
        refToPath(pathString).setValue(null);
    }

    public void removeFriend(String userID, String friendID) {
        String pathString1 = "users/" + userID + "/friends/" + friendID;
        refToPath(pathString1).setValue(null);
        String pathString2 = "users/" + friendID + "/friends/" + userID;
        refToPath(pathString2).setValue(null);
    }

    public void removeSentRequest(String userID, String sentRequestID) {
        String pathString = "users/" + userID + "/sentRequests/" + sentRequestID;
        refToPath(pathString).setValue(null);
    }

    public void removeChat(String chatID) {
        String pathString = "chats/" + chatID;
        refToPath(pathString).setValue(null);
    }

    public void removeMessages(String messagesID) {
        String pathString = "messages/" + messagesID;
        refToPath(pathString).setValue(null);
    }

    //endregion

    //region Load Single

    public Task<DataSnapshot> loadUserTask(String userID) {
        TaskCompletionSource<DataSnapshot> src = new TaskCompletionSource<>();
        ValueEventListener listener = attachValueListenerToTaskCompletion(src);
        String pathString = "users/" + userID;
        refToPath(pathString).addListenerForSingleValueEvent(listener);
        return src.getTask();
    }

    public Task<DataSnapshot> loadUserInfoTask(String userID) {
        TaskCompletionSource<DataSnapshot> src = new TaskCompletionSource<>();
        ValueEventListener listener = attachValueListenerToTaskCompletion(src);
        String pathString = "users/" + userID + "/info";
        refToPath(pathString).addListenerForSingleValueEvent(listener);
        return src.getTask();
    }

    public Task<DataSnapshot> loadUsersTask() {
        TaskCompletionSource<DataSnapshot> src = new TaskCompletionSource<>();
        ValueEventListener listener = attachValueListenerToTaskCompletion(src);
        refToPath("users").addListenerForSingleValueEvent(listener);
        return src.getTask();
    }

    public Task<DataSnapshot> loadFriendsTask(String userID) {
        TaskCompletionSource<DataSnapshot> src = new TaskCompletionSource<>();
        ValueEventListener listener = attachValueListenerToTaskCompletion(src);
        String pathString = "users/" + userID + "/friends";
        refToPath(pathString).addListenerForSingleValueEvent(listener);
        return src.getTask();
    }

    public Task<DataSnapshot> loadChatTask(String chatID) {
        TaskCompletionSource<DataSnapshot> src = new TaskCompletionSource<>();
        ValueEventListener listener = attachValueListenerToTaskCompletion(src);
        String pathString = "chats/" + chatID;
        refToPath(pathString).addListenerForSingleValueEvent(listener);
        return src.getTask();
    }

    public Task<DataSnapshot> loadNotificationsTask(String userID) {
        TaskCompletionSource<DataSnapshot> src = new TaskCompletionSource<>();
        ValueEventListener listener = attachValueListenerToTaskCompletion(src);
        String pathString = "users/" + userID + "/notifications";
        refToPath(pathString).addListenerForSingleValueEvent(listener);
        return src.getTask();
    }

    //endregion

    // region value observers
    public <T> void attachUserNotificationsObserver(Class<T> resultClassName, String userID, @NonNull FirebaseReferenceValueObserver firebaseReferenceValueObserver, ResultCallback<List<T>> b) {
        ValueEventListener listener = attachValueListenerToBlockWithList(resultClassName, b);
        String pathString = "users/" + userID + "/notifications";
        firebaseReferenceValueObserver.start(listener, refToPath(pathString));
    }

    public <T> void attachUserObserver(Class<T> resultClassName, String userID, @NonNull FirebaseReferenceValueObserver refObs, ResultCallback<T> b) {
        ValueEventListener listener = attachValueListenerToBlock(resultClassName, b);
        String pathString = "users/" + userID;
        refObs.start(listener, refToPath(pathString));
    }

    public <T> void attachUserInfoObserver(Class<T> resultClassName, String userID, @NonNull FirebaseReferenceValueObserver refObs, ResultCallback<T> b) {
        ValueEventListener listener = attachValueListenerToBlock(resultClassName, b);
        String pathString = "users/" + userID + "/info";
        refObs.start(listener, refToPath(pathString));
    }

    public <T> void attachMessagesObserver(Class<T> resultClassName, String chatID, @NonNull FirebaseReferenceChildObserver refObs, ResultCallback<T> b) {
        ChildEventListener listener = attachChildListenerToBlock(resultClassName, b);
        String pathString = "messages/" + chatID;
        refObs.start(listener, refToPath(pathString));
    }

    public <T> void attachChatObserver(Class<T> resultClassName, String chatID, @NonNull FirebaseReferenceValueObserver refObs, ResultCallback<T> b) {
        ValueEventListener listener = attachValueListenerToBlock(resultClassName, b);
        String pathString = "chats/" + chatID;
        refObs.start(listener, refToPath(pathString));
    }
}
