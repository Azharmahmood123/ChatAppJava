package com.java.chat.chatapp.ui.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.Nullable;

import com.java.chat.chatapp.R;
import com.java.chat.chatapp.base.BaseFragment;
import com.java.chat.chatapp.base.Event;
import com.java.chat.chatapp.data.model.UserInfo;
import com.java.chat.chatapp.databinding.FragmentSettingsBinding;
import com.java.chat.chatapp.ui.dashboard.DashboardActivity;
import com.java.chat.chatapp.utils.AppSettings;
import com.java.chat.chatapp.utils.FileConverterUtil;
import com.java.chat.chatapp.utils.Utils;
import com.squareup.picasso.Picasso;

import dagger.hilt.android.AndroidEntryPoint;
import jp.wasabeef.picasso.transformations.BlurTransformation;

@AndroidEntryPoint
public class SettingsFragment extends BaseFragment<FragmentSettingsBinding> {

    private SettingsViewModel settingsViewModel;
    private final int selectImageIntentRequestCode = 1003;

    @Override
    protected FragmentSettingsBinding getViewBinding(ViewGroup container) {
        return FragmentSettingsBinding.inflate(getLayoutInflater(), container, false);
    }

    @Override
    protected void initializeControls() {
        settingsViewModel = createViewModel(SettingsViewModel.class);
        settingsViewModel.setMyUserID(AppSettings.getUserId());
        settingsViewModel.init();
    }

    @Override
    protected void attachListeners() {
        binding.logoutButton.setOnClickListener(v -> settingsViewModel.logoutUserPressed());
        binding.changeImageButton.setOnClickListener(v -> startSelectImageIntent());
        binding.changeStatusButton.setOnClickListener(v -> showEditStatusDialog());
    }

    @Override
    protected void observeViewModel() {
        settingsViewModel.dataLoading.observe(getViewLifecycleOwner(), new Event.EventObserver<>(value -> {
            DashboardActivity dashboardActivity = ((DashboardActivity) getActivity());
            if (dashboardActivity != null) {
                dashboardActivity.showGlobalProgressBar(value);
            }
        }));
        settingsViewModel.errorText.observe(this, new Event.EventObserver<>(
                Utils::showToast
        ));

        settingsViewModel.logoutEvent.observe(getViewLifecycleOwner(), new Event.EventObserver<>(value -> {
            if (value) {
                DashboardActivity dashboardActivity = ((DashboardActivity) getActivity());
                if (dashboardActivity != null) {
                    dashboardActivity.logoutUser();
                }
            }
        }));

        settingsViewModel.userInfo.observe(getViewLifecycleOwner(), this::setUserInfo);
    }

    private void setUserInfo(UserInfo userInfo) {
        String url = userInfo.profileImageUrl;
        if (url.isEmpty()) {
            binding.userProfileImage.setBackgroundResource(R.drawable.ic_baseline_person_24);
        } else {
            Picasso.get().load(url).error(R.drawable.ic_baseline_error_24)
                    .transform(new BlurTransformation(binding.blurredUserImage.getContext(), 15, 1))
                    .into(binding.blurredUserImage);
            Picasso.get().load(url).error(R.drawable.ic_baseline_error_24)
                    .into(binding.userProfileImage);
        }

        binding.nameText.setText(userInfo.displayName);
        binding.statusText.setText(userInfo.status);
    }

    private void startSelectImageIntent() {
        Intent selectImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
        selectImageIntent.setType("image/*");
        startActivityForResult(selectImageIntent, selectImageIntentRequestCode);
    }

    private void showEditStatusDialog() {
        EditText editText = new EditText(mActivity);
        new AlertDialog.Builder(requireActivity())
                .setTitle("Status:")
                .setView(editText)
                .setPositiveButton("Ok", (dialog, which) -> {
                    String textInput = editText.getText().toString().trim();
                    if (!textInput.isEmpty() && textInput.length() <= 40) {
                        settingsViewModel.changeUserStatus(textInput);
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                })
                .show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == selectImageIntentRequestCode) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                byte[] imageBytes = FileConverterUtil.convertFileToByteArray(requireActivity(), uri);
                if (imageBytes != null) {
                    settingsViewModel.changeUserImage(imageBytes);
                }
            }
        }
    }
}
