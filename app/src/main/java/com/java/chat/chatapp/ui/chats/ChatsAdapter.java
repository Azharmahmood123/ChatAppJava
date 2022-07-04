package com.java.chat.chatapp.ui.chats;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.java.chat.chatapp.R;
import com.java.chat.chatapp.base.BaseViewHolder;
import com.java.chat.chatapp.base.OnItemClickListener;
import com.java.chat.chatapp.data.model.ChatWithUserInfo;
import com.java.chat.chatapp.databinding.ListItemChatsBinding;
import com.java.chat.chatapp.utils.AppSettings;
import com.java.chat.chatapp.utils.Utils;
import com.squareup.picasso.Picasso;

public class ChatsAdapter extends ListAdapter<ChatWithUserInfo, ChatsAdapter.ChatsViewHolder> {

    private static final DiffUtil.ItemCallback<ChatWithUserInfo> differCallback = new DiffUtil.ItemCallback<ChatWithUserInfo>() {
        @Override
        public boolean areItemsTheSame(@NonNull ChatWithUserInfo oldItem, @NonNull ChatWithUserInfo newItem) {
            return oldItem.hashCode() == newItem.hashCode();
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull ChatWithUserInfo oldItem, @NonNull ChatWithUserInfo newItem) {
            return oldItem.toString().equals(newItem.toString());
        }
    };

    private final OnItemClickListener<ChatWithUserInfo> onItemClickListener;

    public ChatsAdapter(OnItemClickListener<ChatWithUserInfo> onItemClickListener) {
        super(differCallback);
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemChatsBinding binding = ListItemChatsBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ChatsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatsViewHolder holder, int position) {
        ChatWithUserInfo user = getItem(position);
        holder.onBind(user);
    }

    public class ChatsViewHolder extends BaseViewHolder<ChatWithUserInfo> {

        private final ListItemChatsBinding binding;

        public ChatsViewHolder(@NonNull ListItemChatsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        public void onBind(@NonNull ChatWithUserInfo item) {
            if (item.mUserInfo.profileImageUrl.isEmpty()) {
                binding.userProfileImage.setBackgroundResource(R.drawable.ic_baseline_person_24);
            } else {
                Picasso.get().load(item.mUserInfo.profileImageUrl).error(R.drawable.ic_baseline_error_24)
                        .into(binding.userProfileImage);
            }

            binding.onlineView.setVisibility(item.mUserInfo.online ? View.VISIBLE : View.GONE);

            binding.displayNameText.setText(item.mUserInfo.displayName);

            String timeAgo = Utils.getTimeAgoFormat(item.mChat.lastMessage.epochTimeMs);
            binding.timeText.setText(timeAgo);

            String messageText = item.mChat.lastMessage.senderID.equals(AppSettings.getUserId()) ? "You " + item.mChat.lastMessage.text : item.mChat.lastMessage.text;
            binding.messageText.setText(messageText);

            if (!item.mChat.lastMessage.senderID.equals(AppSettings.getUserId()) && !item.mChat.lastMessage.seen) {
                binding.notSeenView.setVisibility(View.VISIBLE);
                binding.messageText.setTextAppearance(R.style.MessageNotSeen);
            } else {
                binding.notSeenView.setVisibility(View.INVISIBLE);
                binding.messageText.setTextAppearance(R.style.MessageSeen);
            }

            itemView.setOnClickListener(v -> onItemClickListener.onItemClick("itemView", item, getAdapterPosition()));
        }
    }

}
