package com.java.chat.chatapp.ui.notifications;

import static com.java.chat.chatapp.utils.FirebaseUtil.convertTwoUserIDs;
import static com.java.chat.chatapp.utils.LiveDataUtil.addNewItem;
import static com.java.chat.chatapp.utils.LiveDataUtil.removeItem;

import androidx.annotation.NonNull;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.java.chat.chatapp.base.BaseViewModel;
import com.java.chat.chatapp.base.Result;
import com.java.chat.chatapp.data.model.Chat;
import com.java.chat.chatapp.data.model.Message;
import com.java.chat.chatapp.data.model.UserFriend;
import com.java.chat.chatapp.data.model.UserInfo;
import com.java.chat.chatapp.data.model.UserNotification;
import com.java.chat.chatapp.data.repository.DatabaseRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class NotificationsViewModel extends BaseViewModel {

    private final DatabaseRepository databaseRepository;
    private String myUserID;

    private final MutableLiveData<UserInfo> updatedUserInfo = new MutableLiveData<>();
    private final MutableLiveData<List<UserNotification>> userNotificationsList = new MutableLiveData<>();

    public MediatorLiveData<List<UserInfo>> usersInfoList = new MediatorLiveData<>();

    @Inject
    public NotificationsViewModel(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    public void setMyUserID(String myUserID) {
        this.myUserID = myUserID;
    }

    public void init() {
        usersInfoList.addSource(updatedUserInfo, userInfo -> {
            List<UserInfo> prevUsersList = usersInfoList.getValue();
            List<UserInfo> newUserList;
            if (prevUsersList != null) {
                newUserList = addNewItem(prevUsersList, userInfo);
            }else{
                newUserList = new ArrayList<>();
                newUserList.add(userInfo);
            }
            usersInfoList.setValue(newUserList);
        });
        loadNotifications();
    }

    private void loadNotifications() {
        databaseRepository.loadNotifications(myUserID, result -> {
            onResult(userNotificationsList, result);
            if (result.states == Result.States.SUCCESS) {
                List<UserNotification> notificationList = result.data;
                if (notificationList != null) {
                    notificationList.forEach(this::loadUserInfo);
                }
            }
        });
    }

    private void loadUserInfo(@NonNull UserNotification userNotification) {
        databaseRepository.loadUserInfo(userNotification.userID, result -> onResult(updatedUserInfo, result));
    }

    private void updateNotification(UserInfo otherUserInfo, Boolean removeOnly) {
        List<UserNotification> notificationList = userNotificationsList.getValue();
        if (notificationList != null) {
            UserNotification userNotification = notificationList.stream().filter(it -> it.userID.equals(otherUserInfo.id)).findFirst().orElse(null);
            if (userNotification != null) {
                if (!removeOnly) {
                    databaseRepository.updateNewFriend(new UserFriend(myUserID), new UserFriend(otherUserInfo.id));
                    Chat newChat = new Chat();
                    newChat.info.id = convertTwoUserIDs(myUserID, otherUserInfo.id);
                    Message message = new Message();
                    message.seen = true;
                    message.text = "Say hello!";
                    newChat.lastMessage = message;
                    databaseRepository.updateNewChat(newChat);
                }
                databaseRepository.removeNotification(myUserID, otherUserInfo.id);
                databaseRepository.removeSentRequest(otherUserInfo.id, myUserID);

                List<UserInfo> prevUsersList = usersInfoList.getValue();
                if (prevUsersList != null) {
                    List<UserInfo> newUserList = removeItem(prevUsersList, otherUserInfo);
                    usersInfoList.setValue(newUserList);
                }
                List<UserNotification> prevNotificationsList = userNotificationsList.getValue();
                if (prevNotificationsList != null) {
                    List<UserNotification> newNotificationsList = removeItem(prevNotificationsList, userNotification);
                    userNotificationsList.setValue(newNotificationsList);
                }
            }
        }
    }

    public void acceptNotificationPressed(UserInfo userInfo) {
        updateNotification(userInfo, false);
    }

    public void declineNotificationPressed(UserInfo userInfo) {
        updateNotification(userInfo, true);
    }
}
