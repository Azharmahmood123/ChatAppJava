package com.java.chat.chatapp.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.java.chat.chatapp.R;
import com.java.chat.chatapp.base.BaseViewModel;
import com.java.chat.chatapp.base.Event;
import com.java.chat.chatapp.base.Result;
import com.java.chat.chatapp.data.model.Login;
import com.java.chat.chatapp.data.model.LoginFormState;
import com.java.chat.chatapp.data.repository.AuthRepository;
import com.java.chat.chatapp.utils.Utils;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class LoginViewModel extends BaseViewModel {

    private final MutableLiveData<Event<FirebaseUser>> mLoggedInEvent = new MutableLiveData<>();
    private final MutableLiveData<LoginFormState> mLoginFormState = new MutableLiveData<>();

    public LiveData<Event<FirebaseUser>> loggedInEvent = mLoggedInEvent;
    public LiveData<LoginFormState> loginFormState = mLoginFormState;

    private final AuthRepository authRepository;

    @Inject
    public LoginViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    public void loginPressed(String email, String password) {
        mLoginFormState.setValue(new LoginFormState(0, 0, false));

        if (Utils.isEmailValid(email)) {
            mLoginFormState.setValue(new LoginFormState(R.string.invalid_email, 0, true));
            return;
        }

        if (Utils.isPasswordValid(password)) {
            mLoginFormState.setValue(new LoginFormState(0, R.string.invalid_password, true));
            return;
        }

        Login login = new Login(email, password);
        authRepository.loginUser(login, result -> {
            onResult(null, result);
            if (result.states == Result.States.SUCCESS) {
                if (result.data != null) {
                    mLoggedInEvent.setValue(new Event<>(result.data));
                }
            }
            if (result.states == Result.States.SUCCESS || result.states == Result.States.ERROR) {
                mLoginFormState.setValue(new LoginFormState(0, 0, true));
            }
        });
    }
}
