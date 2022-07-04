package com.java.chat.chatapp.ui.register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.java.chat.chatapp.R;
import com.java.chat.chatapp.base.BaseViewModel;
import com.java.chat.chatapp.base.Event;
import com.java.chat.chatapp.base.Result;
import com.java.chat.chatapp.data.model.Register;
import com.java.chat.chatapp.data.model.RegisterFormState;
import com.java.chat.chatapp.data.model.User;
import com.java.chat.chatapp.data.repository.AuthRepository;
import com.java.chat.chatapp.data.repository.DatabaseRepository;
import com.java.chat.chatapp.utils.Utils;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RegisterViewModel extends BaseViewModel {

    private final MutableLiveData<RegisterFormState> mRegisterFormState = new MutableLiveData<>();
    private final MutableLiveData<Event<FirebaseUser>> mAccountCreatedEvent = new MutableLiveData<>();

    public LiveData<RegisterFormState> registerFormState = mRegisterFormState;
    public LiveData<Event<FirebaseUser>> accountCreatedEvent = mAccountCreatedEvent;

    private final AuthRepository authRepository;
    private final DatabaseRepository databaseRepository;

    @Inject
    public RegisterViewModel(AuthRepository authRepository, DatabaseRepository databaseRepository) {
        this.authRepository = authRepository;
        this.databaseRepository = databaseRepository;
    }

    public void registerPressed(String displayName, String email, String password, String phone) {
        mRegisterFormState.setValue(new RegisterFormState(0, 0, 0, 0, false));

        if (Utils.isDisplayNameValid(displayName)) {
            mRegisterFormState.setValue(new RegisterFormState(R.string.invalid_name, 0, 0, 0, true));
            return;
        }

        if (Utils.isEmailValid(email)) {
            mRegisterFormState.setValue(new RegisterFormState(0, R.string.invalid_email, 0, 0, true));
            return;
        }

        if (Utils.isPasswordValid(password)) {
            mRegisterFormState.setValue(new RegisterFormState(0, 0, R.string.invalid_password, 0, true));
            return;
        }

        if (Utils.isPhoneValid(phone)) {
            mRegisterFormState.setValue(new RegisterFormState(0, 0, 0, R.string.invalid_phone, true));
            return;
        }

        Register register = new Register(displayName, email, password, phone);
        authRepository.createUser(register, result -> {
            onResult(null, result);
            if (result.states == Result.States.SUCCESS) {
                if (result.data != null) {
                    mAccountCreatedEvent.setValue(new Event<>(result.data));
                    User user = new User();
                    user.info.id = result.data.getUid();
                    user.info.displayName = register.displayName;
                    user.info.email = register.email;
                    user.info.phone = register.phone;
                    databaseRepository.updateNewUser(user);
                }
            }
            if (result.states == Result.States.SUCCESS || result.states == Result.States.ERROR) {
                mRegisterFormState.setValue(new RegisterFormState(0, 0, 0, 0, true));
            }
        });
    }
}
