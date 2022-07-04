package com.java.chat.chatapp.ui.notifications;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.java.chat.chatapp.R;
import com.java.chat.chatapp.base.BaseViewHolder;
import com.java.chat.chatapp.base.OnItemClickListener;
import com.java.chat.chatapp.data.model.UserInfo;
import com.java.chat.chatapp.databinding.ListItemNotificationBinding;
import com.squareup.picasso.Picasso;

public class NotificationsAdapter extends ListAdapter<UserInfo, NotificationsAdapter.NotificationsViewHolder> {

    private static final DiffUtil.ItemCallback<UserInfo> differCallback = new DiffUtil.ItemCallback<UserInfo>() {
        @Override
        public boolean areItemsTheSame(@NonNull UserInfo oldItem, @NonNull UserInfo newItem) {
            return oldItem.hashCode() == newItem.hashCode();
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull UserInfo oldItem, @NonNull UserInfo newItem) {
            return oldItem.toString().equals(newItem.toString());
        }
    };

    private final OnItemClickListener<UserInfo> onItemClickListener;

    public NotificationsAdapter(OnItemClickListener<UserInfo> onItemClickListener) {
        super(differCallback);
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemNotificationBinding binding = ListItemNotificationBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new NotificationsViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsViewHolder holder, int position) {
        UserInfo userInfo = getItem(position);
        holder.onBind(userInfo);
    }

    public class NotificationsViewHolder extends BaseViewHolder<UserInfo> {

        private final ListItemNotificationBinding binding;

        public NotificationsViewHolder(@NonNull ListItemNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        public void onBind(@NonNull UserInfo item) {
            if (item.profileImageUrl.isEmpty()) {
                binding.userProfileImage.setBackgroundResource(R.drawable.ic_baseline_person_24);
            } else {
                Picasso.get().load(item.profileImageUrl).error(R.drawable.ic_baseline_error_24)
                        .into(binding.userProfileImage);
            }
            binding.displayNameText.setText(item.displayName);

            binding.acceptButton.setOnClickListener(v -> onItemClickListener.onItemClick("acceptButton", item, getAdapterPosition()));
            binding.declineButton.setOnClickListener(v -> onItemClickListener.onItemClick("declineButton", item, getAdapterPosition()));

        }
    }

}
