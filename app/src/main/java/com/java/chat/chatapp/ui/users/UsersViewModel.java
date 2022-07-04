package com.java.chat.chatapp.ui.users;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.java.chat.chatapp.base.BaseViewModel;
import com.java.chat.chatapp.data.model.User;
import com.java.chat.chatapp.data.repository.DatabaseRepository;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class UsersViewModel extends BaseViewModel {

    private final DatabaseRepository databaseRepository;
    private String myUserID;

    private final MutableLiveData<List<User>> updatedUsersList = new MutableLiveData<>();
    public MediatorLiveData<List<User>> usersList = new MediatorLiveData<>();

    @Inject
    public UsersViewModel(DatabaseRepository databaseRepository) {
        this.databaseRepository = databaseRepository;
    }

    public void setMyUserID(String myUserID) {
        this.myUserID = myUserID;
    }

    public void init() {
        usersList.addSource(updatedUsersList, users -> {
            List<User> filteredList = users.stream().filter(user -> !user.info.id.equals(myUserID)).collect(Collectors.toList());
            usersList.setValue(filteredList);
        });
        loadUsers();
    }

    private void loadUsers() {
        databaseRepository.loadUsers(result -> {
            onResult(updatedUsersList, result);
        });
    }
}
