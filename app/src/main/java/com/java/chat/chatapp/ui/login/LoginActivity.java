package com.java.chat.chatapp.ui.login;

import android.view.View;
import android.view.inputmethod.EditorInfo;

import com.java.chat.chatapp.R;
import com.java.chat.chatapp.base.BaseActivity;
import com.java.chat.chatapp.base.Event;
import com.java.chat.chatapp.databinding.ActivityLoginBinding;
import com.java.chat.chatapp.ui.dashboard.DashboardActivity;
import com.java.chat.chatapp.ui.register.RegisterActivity;
import com.java.chat.chatapp.utils.AppSettings;
import com.java.chat.chatapp.utils.Utils;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LoginActivity extends BaseActivity<ActivityLoginBinding> {

    private LoginViewModel loginViewModel;

    @Override
    protected ActivityLoginBinding getViewBinding() {
        return ActivityLoginBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initializeControls() {
        loginViewModel = createViewModel(LoginViewModel.class);
        binding.editTextEmail.setText("azhar@gmail.com");
        binding.editTextPassword.setText("111111");
    }

    @Override
    protected void attachListeners() {
        binding.btnLogin.setOnClickListener(this);
        binding.tvRegisterHere.setOnClickListener(this);
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
        binding.editTextPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loadPressed();
                return true;
            }
            return false;
        });
    }

    @Override
    protected void observeViewModel() {
        loginViewModel.dataLoading.observe(this, new Event.EventObserver<>(
                this::showGlobalProgressBar
        ));
        loginViewModel.errorText.observe(this, new Event.EventObserver<>(
                Utils::showToast
        ));
        loginViewModel.loginFormState.observe(this, loginState -> {
            if (loginState.emailError != 0) {
                binding.editTextEmail.setError(getString(loginState.emailError));
            } else {
                binding.editTextEmail.setError(null);
            }

            if (loginState.passwordError != 0) {
                binding.editTextPassword.setError(getString(loginState.passwordError));
            } else {
                binding.editTextPassword.setError(null);
            }

            binding.btnLogin.setEnabled(loginState.enableButton);
        });
        loginViewModel.loggedInEvent.observe(this, new Event.EventObserver<>(
                user -> {
                    AppSettings.setUserID(user.getUid());
                    startSpecificActivity(DashboardActivity.class);
                    finish();
                    Utils.showToast("login successful");
                }
        ));
    }

    @Override
    public void onClick(View view) {
        if (view == binding.btnLogin) {
            loadPressed();
        }else if (view == binding.tvRegisterHere) {
            startSpecificActivity(RegisterActivity.class);
            finish();
        }
    }

    private void loadPressed() {
        Utils.hideKeyboard(LoginActivity.this);
        String email = binding.editTextEmail.getText().toString();
        String password = binding.editTextPassword.getText().toString();
        loginViewModel.loginPressed(email, password);
    }

    private void showGlobalProgressBar(boolean show) {
        if (show) binding.viewProgress.setVisibility(View.VISIBLE);
        else binding.viewProgress.setVisibility(View.GONE);
    }
}