package com.java.chat.chatapp.ui.users;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.java.chat.chatapp.R;
import com.java.chat.chatapp.base.BaseViewHolder;
import com.java.chat.chatapp.base.OnItemClickListener;
import com.java.chat.chatapp.data.model.User;
import com.java.chat.chatapp.databinding.ListItemUserBinding;
import com.squareup.picasso.Picasso;

public class UsersAdapter extends ListAdapter<User, UsersAdapter.UsersViewHolder> {

    private static final DiffUtil.ItemCallback<User> differCallback = new DiffUtil.ItemCallback<User>() {
        @Override
        public boolean areItemsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem.hashCode() == newItem.hashCode();
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull User oldItem, @NonNull User newItem) {
            return oldItem.toString().equals(newItem.toString());
        }
    };

    private final OnItemClickListener<User> onItemClickListener;

    public UsersAdapter(OnItemClickListener<User> onItemClickListener) {
        super(differCallback);
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemUserBinding binding = ListItemUserBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new UsersViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        User user = getItem(position);
        holder.onBind(user);
    }

    public class UsersViewHolder extends BaseViewHolder<User> {

        private final ListItemUserBinding binding;

        public UsersViewHolder(@NonNull ListItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @Override
        public void onBind(@NonNull User item) {
            if (item.info.profileImageUrl.isEmpty()) {
                binding.userProfileImage.setBackgroundResource(R.drawable.ic_baseline_person_24);
            } else {
                Picasso.get().load(item.info.profileImageUrl).error(R.drawable.ic_baseline_error_24)
                        .into(binding.userProfileImage);
            }
            binding.displayNameText.setText(item.info.displayName);
            binding.statusText.setText(item.info.status);

            itemView.setOnClickListener(v -> onItemClickListener.onItemClick("itemView", item, getAdapterPosition()));
        }
    }

}
