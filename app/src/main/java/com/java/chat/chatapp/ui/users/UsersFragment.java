package com.java.chat.chatapp.ui.users;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.java.chat.chatapp.R;
import com.java.chat.chatapp.base.BaseFragment;
import com.java.chat.chatapp.base.Event;
import com.java.chat.chatapp.base.OnItemClickListener;
import com.java.chat.chatapp.data.model.User;
import com.java.chat.chatapp.databinding.FragmentUsersBinding;
import com.java.chat.chatapp.ui.dashboard.DashboardActivity;
import com.java.chat.chatapp.ui.profile.ProfileFragment;
import com.java.chat.chatapp.utils.AppSettings;
import com.java.chat.chatapp.utils.Utils;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UsersFragment extends BaseFragment<FragmentUsersBinding> implements OnItemClickListener<User> {

    private UsersAdapter usersAdapter;
    private UsersViewModel usersViewModel;

    @Override
    protected FragmentUsersBinding getViewBinding(ViewGroup container) {
        return FragmentUsersBinding.inflate(getLayoutInflater(), container, false);
    }

    @Override
    protected void initializeControls() {
        usersViewModel = createViewModel(UsersViewModel.class);
        usersViewModel.setMyUserID(AppSettings.getUserId());
        usersViewModel.init();
        usersAdapter = new UsersAdapter(UsersFragment.this);
        binding.usersRecyclerView.setAdapter(usersAdapter);
    }

    @Override
    protected void observeViewModel() {
        usersViewModel.dataLoading.observe(getViewLifecycleOwner(), new Event.EventObserver<>(value -> {
            DashboardActivity dashboardActivity = ((DashboardActivity) getActivity());
            if (dashboardActivity != null) {
                dashboardActivity.showGlobalProgressBar(value);
            }
        }));
        usersViewModel.errorText.observe(getViewLifecycleOwner(), new Event.EventObserver<>(
                Utils::showToast
        ));

        usersViewModel.usersList.observe(getViewLifecycleOwner(), usersList -> usersAdapter.submitList(usersList));
    }

    @Override
    public void onItemClick(String clickTag, @NonNull User user, int position) {
        ProfileFragment fragment = new ProfileFragment();
        fragment.otherUserID = user.info.id;
        addFragment(R.id.fragment, fragment, true, true);
    }
}
