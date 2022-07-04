package com.java.chat.chatapp.ui.notifications;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.java.chat.chatapp.base.BaseFragment;
import com.java.chat.chatapp.base.Event;
import com.java.chat.chatapp.base.OnItemClickListener;
import com.java.chat.chatapp.data.model.UserInfo;
import com.java.chat.chatapp.databinding.FragmentNotificationsBinding;
import com.java.chat.chatapp.ui.dashboard.DashboardActivity;
import com.java.chat.chatapp.utils.AppSettings;
import com.java.chat.chatapp.utils.Utils;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NotificationsFragment extends BaseFragment<FragmentNotificationsBinding> implements OnItemClickListener<UserInfo> {


    private NotificationsViewModel notificationsViewModel;
    private NotificationsAdapter notificationsAdapter;

    @Override
    protected FragmentNotificationsBinding getViewBinding(ViewGroup container) {
        return FragmentNotificationsBinding.inflate(getLayoutInflater(), container, false);
    }

    @Override
    protected void initializeControls() {
        notificationsViewModel = createViewModel(NotificationsViewModel.class);
        notificationsViewModel.setMyUserID(AppSettings.getUserId());
        notificationsViewModel.init();

        notificationsAdapter = new NotificationsAdapter(this);
        binding.usersRecyclerView.setAdapter(notificationsAdapter);
    }

    @Override
    protected void observeViewModel() {
        notificationsViewModel.dataLoading.observe(getViewLifecycleOwner(), new Event.EventObserver<>(value -> {
            DashboardActivity dashboardActivity = ((DashboardActivity) getActivity());
            if (dashboardActivity != null) {
                dashboardActivity.showGlobalProgressBar(value);
            }
        }));
        notificationsViewModel.errorText.observe(getViewLifecycleOwner(), new Event.EventObserver<>(
                Utils::showToast
        ));
        notificationsViewModel.usersInfoList.observe(getViewLifecycleOwner(), usersList -> notificationsAdapter.submitList(usersList));
    }

    @Override
    public void onItemClick(@NonNull String clickTag, UserInfo userInfo, int position) {
        switch (clickTag) {
            case "acceptButton":
                notificationsViewModel.acceptNotificationPressed(userInfo);
                break;
            case "declineButton":
                notificationsViewModel.declineNotificationPressed(userInfo);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + clickTag);
        }
    }
}
