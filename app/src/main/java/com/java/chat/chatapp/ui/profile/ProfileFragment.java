package com.java.chat.chatapp.ui.profile;

import android.view.View;
import android.view.ViewGroup;

import com.java.chat.chatapp.R;
import com.java.chat.chatapp.base.BaseFragment;
import com.java.chat.chatapp.base.Event;
import com.java.chat.chatapp.databinding.FragmentProfileBinding;
import com.java.chat.chatapp.ui.dashboard.DashboardActivity;
import com.java.chat.chatapp.utils.AppSettings;
import com.squareup.picasso.Picasso;

import dagger.hilt.android.AndroidEntryPoint;
import jp.wasabeef.picasso.transformations.BlurTransformation;

@AndroidEntryPoint
public class ProfileFragment extends BaseFragment<FragmentProfileBinding> {

    public String otherUserID;
    private ProfileViewModel profileViewModel;

    @Override
    protected FragmentProfileBinding getViewBinding(ViewGroup container) {
        return FragmentProfileBinding.inflate(getLayoutInflater(), container, false);
    }

    @Override
    protected void initializeControls() {
        profileViewModel = createViewModel(ProfileViewModel.class);
        profileViewModel.setUsersIDs(AppSettings.getUserId(), otherUserID);
        profileViewModel.init();
    }

    @Override
    protected void attachListeners() {
        binding.ivBack.setOnClickListener(this);
        binding.btnAddFriendButton.setOnClickListener(this);
        binding.btnRemoveFriendButton.setOnClickListener(this);
        binding.btnAcceptRequestButton.setOnClickListener(this);
        binding.btnDeclineRequestButton.setOnClickListener(this);
    }

    @Override
    protected void observeViewModel() {
        profileViewModel.dataLoading.observe(getViewLifecycleOwner(), new Event.EventObserver<>(value -> {
            DashboardActivity dashboardActivity = ((DashboardActivity) getActivity());
            if (dashboardActivity != null) {
                dashboardActivity.showGlobalProgressBar(value);
            }
        }));

        profileViewModel.otherUser.observe(getViewLifecycleOwner(), user -> {
            if (user.info.profileImageUrl.isEmpty()) {
                binding.ivUserImage.setBackgroundResource(R.drawable.ic_baseline_person_24);
            } else {
                Picasso.get().load(user.info.profileImageUrl).error(R.drawable.ic_baseline_error_24)
                        .transform(new BlurTransformation(binding.blurredUserImage.getContext(), 15, 1))
                        .into(binding.blurredUserImage);
                Picasso.get().load(user.info.profileImageUrl).error(R.drawable.ic_baseline_error_24)
                        .into(binding.ivUserImage);
            }
            binding.tvName.setText(user.info.displayName);
            binding.tvStatus.setText(user.info.status);
        });

        profileViewModel.layoutState.observe(getViewLifecycleOwner(), layoutState -> {
            hideAllButtons();
            switch (layoutState) {
                case NOT_FRIEND:
                    binding.btnAddFriendButton.setVisibility(View.VISIBLE);
                    break;
                case IS_FRIEND:
                    binding.btnRemoveFriendButton.setVisibility(View.VISIBLE);
                    break;
                case REQUEST_SENT:
                    binding.btnRequestSentButton.setVisibility(View.VISIBLE);
                    break;
                default:
                    binding.btnAcceptRequestButton.setVisibility(View.VISIBLE);
                    binding.btnDeclineRequestButton.setVisibility(View.VISIBLE);
                    break;
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view == binding.ivBack) {
            DashboardActivity dashboardActivity = ((DashboardActivity) getActivity());
            if (dashboardActivity != null) {
                dashboardActivity.onBackPressed();
            }
        } else if (view == binding.btnAddFriendButton) {
            profileViewModel.addFriendPressed();
        } else if (view == binding.btnRemoveFriendButton) {
            profileViewModel.removeFriendPressed();
        } else if (view == binding.btnAcceptRequestButton) {
            profileViewModel.acceptFriendRequestPressed();
        } else if (view == binding.btnDeclineRequestButton) {
            profileViewModel.declineFriendRequestPressed();
        }
    }

    private void hideAllButtons() {
        binding.btnAddFriendButton.setVisibility(View.GONE);
        binding.btnRemoveFriendButton.setVisibility(View.GONE);
        binding.btnRequestSentButton.setVisibility(View.GONE);
        binding.btnAcceptRequestButton.setVisibility(View.GONE);
        binding.btnDeclineRequestButton.setVisibility(View.GONE);
    }
}
