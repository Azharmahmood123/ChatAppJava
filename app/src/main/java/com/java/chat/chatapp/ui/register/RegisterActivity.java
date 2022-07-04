package com.java.chat.chatapp.ui.register;

import android.view.View;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;

import com.java.chat.chatapp.R;
import com.java.chat.chatapp.base.BaseActivity;
import com.java.chat.chatapp.base.Event;
import com.java.chat.chatapp.databinding.ActivityRegisterBinding;
import com.java.chat.chatapp.ui.dashboard.DashboardActivity;
import com.java.chat.chatapp.ui.login.LoginActivity;
import com.java.chat.chatapp.utils.AppSettings;
import com.java.chat.chatapp.utils.Utils;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RegisterActivity extends BaseActivity<ActivityRegisterBinding> {

    private RegisterViewModel registerViewModel;

    @Override
    protected ActivityRegisterBinding getViewBinding() {
        return ActivityRegisterBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initializeControls() {
        registerViewModel = createViewModel(RegisterViewModel.class);
    }

    @Override
    protected void attachListeners() {
        binding.btnCreate.setOnClickListener(this);
        binding.tvLoginHere.setOnClickListener(this);
        binding.editTextDisplayName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (Utils.isDisplayNameValid(binding.editTextDisplayName.getText().toString())) {
                    binding.editTextDisplayName.setError(getString(R.string.invalid_name));
                } else {
                    binding.editTextDisplayName.setError(null);
                }
            }
        });
        binding.editTextEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (Utils.isEmailValid(binding.editTextEmail.getText().toString())) {
                    binding.editTextEmail.setError(getString(R.string.invalid_email));
                } else {
                    binding.editTextEmail.setError(null);
                }
            }
        });
        binding.editTextPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (Utils.isPasswordValid(binding.editTextPassword.getText().toString())) {
                    binding.editTextPassword.setError(getString(R.string.invalid_password));
                } else {
                    binding.editTextPassword.setError(null);
                }
            }
        });
        binding.editTextPhone.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                if (Utils.isPhoneValid(binding.editTextPhone.getText().toString())) {
                    binding.editTextPhone.setError(getString(R.string.invalid_password));
                } else {
                    binding.editTextPhone.setError(null);
                }
            }
        });
        binding.editTextPhone.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                registerPressed();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void observeViewModel() {
        registerViewModel.dataLoading.observe(this, new Event.EventObserver<>(
                this::showGlobalProgressBar
        ));
        registerViewModel.errorText.observe(this, new Event.EventObserver<>(
                Utils::showToast
        ));
        registerViewModel.registerFormState.observe(this, registerState -> {
            if (registerState.nameError != 0) {
                binding.editTextDisplayName.setError(getString(registerState.nameError));
            } else {
                binding.editTextDisplayName.setError(null);
            }

            if (registerState.emailError != 0) {
                binding.editTextEmail.setError(getString(registerState.emailError));
            } else {
                binding.editTextEmail.setError(null);
            }

            if (registerState.passwordError != 0) {
                binding.editTextPassword.setError(getString(registerState.passwordError));
            } else {
                binding.editTextPassword.setError(null);
            }

            if (registerState.phoneError != 0) {
                binding.editTextPhone.setError(getString(registerState.phoneError));
            } else {
                binding.editTextPhone.setError(null);
            }

            binding.btnCreate.setEnabled(registerState.enableButton);
        });
        registerViewModel.accountCreatedEvent.observe(this, new Event.EventObserver<>(
                user -> {
                    AppSettings.setUserID(user.getUid());
                    startSpecificActivity(DashboardActivity.class);
                    finish();
                }
        ));
    }

    @Override
    public void onClick(View view) {
        if (view == binding.btnCreate) {
            registerPressed();
        } else if (view == binding.tvLoginHere) {
            startSpecificActivity(LoginActivity.class);
            finish();
        }
    }

    private void registerPressed() {
        Utils.hideKeyboard(RegisterActivity.this);
        String displayName = binding.editTextDisplayName.getText().toString();
        String email = binding.editTextEmail.getText().toString();
        String password = binding.editTextPassword.getText().toString();
        String phone = binding.editTextPhone.getText().toString();
        registerViewModel.registerPressed(displayName, email, password, phone);
    }

    private void showGlobalProgressBar(@NonNull Boolean show) {
        if (show) binding.viewProgress.setVisibility(View.VISIBLE);
        else binding.viewProgress.setVisibility(View.GONE);
    }
}