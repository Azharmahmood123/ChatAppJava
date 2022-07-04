package com.java.chat.chatapp.data.model;

public class LoginFormState {
    public int emailError;
    public int passwordError;
    public boolean enableButton;

    public LoginFormState(int emailError, int passwordError, boolean enableButton) {
        this.emailError = emailError;
        this.passwordError = passwordError;
        this.enableButton = enableButton;
    }
}
