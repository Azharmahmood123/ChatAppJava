package com.java.chat.chatapp.ui.messages;

import static com.java.chat.chatapp.utils.Utils.showToast;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.java.chat.chatapp.R;
import com.java.chat.chatapp.base.BaseFragment;
import com.java.chat.chatapp.base.Event;
import com.java.chat.chatapp.base.OnItemClickListener;
import com.java.chat.chatapp.data.model.Message;
import com.java.chat.chatapp.data.model.UserInfo;
import com.java.chat.chatapp.databinding.FragmentMessagesBinding;
import com.java.chat.chatapp.ui.dashboard.DashboardActivity;
import com.java.chat.chatapp.utils.AppSettings;
import com.java.chat.chatapp.utils.Utils;
import com.squareup.picasso.Picasso;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MessagesFragment extends BaseFragment<FragmentMessagesBinding> implements OnItemClickListener<Message> {

    private MessagesViewModel messagesViewModel;
    private MessagesAdapter messagesAdapter;
    private RecyclerView.AdapterDataObserver listAdapterObserver;

    public String otherUserID = "";
    public String chatID = "";

    @Override
    protected FragmentMessagesBinding getViewBinding(ViewGroup container) {
        return FragmentMessagesBinding.inflate(getLayoutInflater(), container, false);
    }

    @Override
    protected void initializeControls() {
        listAdapterObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                binding.messagesRecyclerView.scrollToPosition(positionStart);
            }
        };
        messagesAdapter = new MessagesAdapter(AppSettings.getUserId(), this);
        messagesAdapter.registerAdapterDataObserver(listAdapterObserver);

        binding.messagesRecyclerView.setAdapter(messagesAdapter);
        binding.messagesRecyclerView.setItemAnimator(null);

        messagesViewModel = createViewModel(MessagesViewModel.class);
        messagesViewModel.setUsersIDs(AppSettings.getUserId(), otherUserID, chatID);
        messagesViewModel.init();
    }

    @Override
    protected void attachListeners() {
        binding.sendBtn.setOnClickListener(v -> {
            String newMessage = binding.editTextMessage.getText().toString();
            if (newMessage.isEmpty()) {
                showToast("Please enter message.");
                return;
            }
            messagesViewModel.sendMessagePressed(newMessage);
            binding.editTextMessage.setText("");
        });
        binding.ivBack.setOnClickListener(v -> {
            DashboardActivity dashboardActivity = ((DashboardActivity) getActivity());
            if (dashboardActivity != null) {
                dashboardActivity.onBackPressed();
            }
        });
    }

    @Override
    protected void observeViewModel() {
        messagesViewModel.dataLoading.observe(getViewLifecycleOwner(), new Event.EventObserver<>(value -> {
            DashboardActivity dashboardActivity = ((DashboardActivity) getActivity());
            if (dashboardActivity != null) {
                dashboardActivity.showGlobalProgressBar(value);
            }
        }));
        messagesViewModel.errorText.observe(getViewLifecycleOwner(), new Event.EventObserver<>(
                Utils::showToast
        ));

        messagesViewModel.otherUser.observe(getViewLifecycleOwner(), this::setUserInfo);

        messagesViewModel.messagesList.observe(getViewLifecycleOwner(), messagesList -> messagesAdapter.submitList(messagesList));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        messagesAdapter.unregisterAdapterDataObserver(listAdapterObserver);
    }

    @Override
    public void onItemClick(String clickTag, Message message, int position) {

    }

    private void setUserInfo(UserInfo userInfo) {
        String url = userInfo.profileImageUrl;
        if (url.isEmpty()) {
            binding.userProfileImage.setBackgroundResource(R.drawable.ic_baseline_person_24);
        } else {
            Picasso.get().load(url).error(R.drawable.ic_baseline_error_24)
                    .into(binding.userProfileImage);
        }

        if (userInfo.online) {
            binding.onlineView.setVisibility(View.VISIBLE);
            binding.onlineStatusText.setText(getString(R.string.online));
        } else {
            binding.onlineView.setVisibility(View.GONE);
            binding.onlineStatusText.setText(getString(R.string.offline));
        }

        binding.otherUserNameText.setText(userInfo.displayName);
    }
}
