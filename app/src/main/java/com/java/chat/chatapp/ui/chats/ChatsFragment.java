package com.java.chat.chatapp.ui.chats;

import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.java.chat.chatapp.R;
import com.java.chat.chatapp.base.BaseFragment;
import com.java.chat.chatapp.base.Event;
import com.java.chat.chatapp.base.OnItemClickListener;
import com.java.chat.chatapp.data.model.ChatWithUserInfo;
import com.java.chat.chatapp.databinding.FragmentChatsBinding;
import com.java.chat.chatapp.ui.dashboard.DashboardActivity;
import com.java.chat.chatapp.ui.messages.MessagesFragment;
import com.java.chat.chatapp.utils.AppSettings;
import com.java.chat.chatapp.utils.FirebaseUtil;
import com.java.chat.chatapp.utils.Utils;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ChatsFragment extends BaseFragment<FragmentChatsBinding> implements OnItemClickListener<ChatWithUserInfo> {


    private ChatsViewModel chatsViewModel;
    private ChatsAdapter chatsAdapter;

    @Override
    protected FragmentChatsBinding getViewBinding(ViewGroup container) {
        return FragmentChatsBinding.inflate(getLayoutInflater(), container, false);
    }

    @Override
    protected void initializeControls() {
        chatsViewModel = createViewModel(ChatsViewModel.class);
        chatsViewModel.setMyUserID(AppSettings.getUserId());
        chatsViewModel.init();

        chatsAdapter = new ChatsAdapter(this);
        binding.chatsRecyclerView.setAdapter(chatsAdapter);
    }

    @Override
    protected void observeViewModel() {
        chatsViewModel.dataLoading.observe(getViewLifecycleOwner(), new Event.EventObserver<>(value -> {
            DashboardActivity dashboardActivity = ((DashboardActivity) getActivity());
            if (dashboardActivity != null) {
                dashboardActivity.showGlobalProgressBar(value);
            }
        }));
        chatsViewModel.errorText.observe(this, new Event.EventObserver<>(
                Utils::showToast
        ));
        chatsViewModel.chatsList.observe(getViewLifecycleOwner(), chatsList -> chatsAdapter.submitList(chatsList));
    }

    @Override
    public void onItemClick(String clickTag, @NonNull ChatWithUserInfo chatWithUserInfo, int position) {
        MessagesFragment fragment = new MessagesFragment();
        fragment.otherUserID = chatWithUserInfo.mUserInfo.id;
        fragment.chatID = FirebaseUtil.convertTwoUserIDs(AppSettings.getUserId(), chatWithUserInfo.mUserInfo.id);
        addFragment(R.id.fragment,fragment,true,true);
    }
}
