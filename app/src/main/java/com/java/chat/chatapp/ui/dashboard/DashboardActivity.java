package com.java.chat.chatapp.ui.dashboard;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.badge.BadgeDrawable;
import com.google.firebase.database.FirebaseDatabase;
import com.java.chat.chatapp.R;
import com.java.chat.chatapp.base.BaseActivity;
import com.java.chat.chatapp.databinding.ActivityDashboardBinding;
import com.java.chat.chatapp.ui.chats.ChatsFragment;
import com.java.chat.chatapp.ui.login.LoginActivity;
import com.java.chat.chatapp.ui.notifications.NotificationsFragment;
import com.java.chat.chatapp.ui.settings.SettingsFragment;
import com.java.chat.chatapp.ui.users.UsersFragment;
import com.java.chat.chatapp.utils.AppSettings;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class DashboardActivity extends BaseActivity<ActivityDashboardBinding> {


    private DashboardViewModel dashboardViewModel;

    private BadgeDrawable notificationsBadge;

    @Override
    protected ActivityDashboardBinding getViewBinding() {
        return ActivityDashboardBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initializeControls() {
        dashboardViewModel = createViewModel(DashboardViewModel.class);
        dashboardViewModel.setMyUserID(AppSettings.getUserId());
        dashboardViewModel.setupAuthObserver();

        notificationsBadge = binding.bottomNavigationView.getOrCreateBadge(R.id.navigation_notifications);
        notificationsBadge.setVisible(false);
        showChatsFragment();
    }

    @Override
    protected void attachListeners() {
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_chats) {
                showChatsFragment();
                return true;
            }
            if (item.getItemId() == R.id.navigation_notifications) {
                showNotificationsFragment();
                return true;
            }
            if (item.getItemId() == R.id.navigation_users) {
                showUsersFragment();
                return true;
            }
            if (item.getItemId() == R.id.navigation_settings) {
                showSettingsFragment();
                return true;
            }
            return false;
        });

        getSupportFragmentManager().addFragmentOnAttachListener((fragmentManager, fragment) -> {
            if (fragment instanceof ChatsFragment
                    || fragment instanceof NotificationsFragment
                    || fragment instanceof UsersFragment
                    || fragment instanceof SettingsFragment) {
                showNavbar();
            } else {
                hideNavbar();
            }
        });

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment fragment = getCurrentTopFragment(DashboardActivity.this);
            if (fragment instanceof ChatsFragment
                    || fragment instanceof NotificationsFragment
                    || fragment instanceof UsersFragment
                    || fragment instanceof SettingsFragment) {
                showNavbar();
            } else {
                hideNavbar();
            }
        });

    }

    @Override
    protected void observeViewModel() {
        dashboardViewModel.userNotificationsList.observe(this, userNotifications -> {
            if (userNotifications.size() > 0) {
                notificationsBadge.setNumber(userNotifications.size());
                notificationsBadge.setVisible(true);
            } else {
                notificationsBadge.setVisible(false);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseDatabase.getInstance().goOffline();
    }

    @Override
    protected void onResume() {
        FirebaseDatabase.getInstance().goOnline();
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack();
        } else {
            Fragment fragment = getCurrentTopFragment(DashboardActivity.this);
            if (fragment instanceof ChatsFragment) {
                finish();
            } else {
                showChatsFragment();
                binding.bottomNavigationView.setSelectedItemId(R.id.navigation_chats);
            }
        }
    }

    private void showChatsFragment() {
        ChatsFragment fragment = new ChatsFragment();
        replaceFragment(R.id.fragment, fragment, false, false);
    }

    private void showNotificationsFragment() {
        NotificationsFragment fragment = new NotificationsFragment();
        replaceFragment(R.id.fragment, fragment, false, false);
    }

    private void showUsersFragment() {
        UsersFragment fragment = new UsersFragment();
        replaceFragment(R.id.fragment, fragment, false, false);
    }

    private void showSettingsFragment() {
        SettingsFragment fragment = new SettingsFragment();
        replaceFragment(R.id.fragment, fragment, false, false);
    }

    public void showGlobalProgressBar(@NonNull Boolean show) {
        if (show) binding.viewProgress.setVisibility(View.VISIBLE);
        else binding.viewProgress.setVisibility(View.GONE);
    }

    public void logoutUser() {
        AppSettings.setUserID("");
        startSpecificActivity(LoginActivity.class);
        finish();
    }

    private void hideNavbar() {
        binding.bottomNavigationView.setVisibility(View.GONE);
    }

    private void showNavbar() {
        binding.bottomNavigationView.setVisibility(View.VISIBLE);
    }
}