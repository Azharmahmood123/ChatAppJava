package com.java.chat.chatapp.data.model;

public class RegisterFormState {
    public int nameError;
    public int emailError;
    public int passwordError;
    public int phoneError;
    public boolean enableButton;

    public RegisterFormState(int nameError, int emailError, int passwordError, int phoneError, boolean enableButton) {
        this.nameError = nameError;
        this.emailError = emailError;
        this.passwordError = passwordError;
        this.phoneError = phoneError;
        this.enableButton = enableButton;
    }
}
