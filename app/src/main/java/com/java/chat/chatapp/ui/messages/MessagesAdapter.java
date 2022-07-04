package com.java.chat.chatapp.ui.messages;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.java.chat.chatapp.base.BaseViewHolder;
import com.java.chat.chatapp.base.OnItemClickListener;
import com.java.chat.chatapp.data.model.Message;
import com.java.chat.chatapp.databinding.ListItemMessageReceivedBinding;
import com.java.chat.chatapp.databinding.ListItemMessageSentBinding;
import com.java.chat.chatapp.utils.Utils;

import java.util.List;

public class MessagesAdapter extends ListAdapter<Message, RecyclerView.ViewHolder> {

    private static final DiffUtil.ItemCallback<Message> differCallback = new DiffUtil.ItemCallback<Message>() {
        @Override
        public boolean areItemsTheSame(@NonNull Message oldItem, @NonNull Message newItem) {
            return oldItem.hashCode() == newItem.hashCode();
        }

        @SuppressLint("DiffUtilEquals")
        @Override
        public boolean areContentsTheSame(@NonNull Message oldItem, @NonNull Message newItem) {
            return oldItem.toString().equals(newItem.toString());
        }
    };

    private final OnItemClickListener<Message> onItemClickListener;
    private final String myUserID;

    private final static int holderTypeMessageReceived = 1;
    private final static int holderTypeMessageSent = 2;

    public MessagesAdapter(String myUserID, OnItemClickListener<Message> onItemClickListener) {
        super(differCallback);
        this.myUserID = myUserID;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).senderID.equals(myUserID) ? holderTypeMessageSent : holderTypeMessageReceived;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == holderTypeMessageSent) {
            ListItemMessageSentBinding binding = ListItemMessageSentBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new SenderViewHolder(binding, getCurrentList());
        }
        ListItemMessageReceivedBinding binding = ListItemMessageReceivedBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ReceiverViewHolder(binding, getCurrentList());
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == holderTypeMessageSent) {
            SenderViewHolder holder1 = (SenderViewHolder) holder;
            holder1.onBind(getItem(position));
        } else {
            ReceiverViewHolder holder1 = (ReceiverViewHolder) holder;
            holder1.onBind(getItem(position));
        }
    }

    public class SenderViewHolder extends BaseViewHolder<Message> {

        private final ListItemMessageSentBinding binding;
        private final List<Message> currentList;

        public SenderViewHolder(@NonNull ListItemMessageSentBinding binding, List<Message> currentList) {
            super(binding.getRoot());
            this.binding = binding;
            this.currentList = currentList;
        }

        @Override
        public void onBind(@NonNull Message item) {
            binding.messageText.setText(item.text);
            String timeAgo = Utils.getTimeAgoFormat(item.epochTimeMs);
            binding.timeText.setText(timeAgo);
            Utils.bindShouldMessageShowTimeText(binding.timeText, item, currentList);
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick("itemView", item, getAdapterPosition()));
        }
    }

    public class ReceiverViewHolder extends BaseViewHolder<Message> {

        private final ListItemMessageReceivedBinding binding;
        private final List<Message> currentList;

        public ReceiverViewHolder(@NonNull ListItemMessageReceivedBinding binding, List<Message> currentList) {
            super(binding.getRoot());
            this.binding = binding;
            this.currentList = currentList;
        }

        @Override
        public void onBind(@NonNull Message item) {
            binding.messageText.setText(item.text);
            String timeAgo = Utils.getTimeAgoFormat(item.epochTimeMs);
            binding.timeText.setText(timeAgo);
            Utils.bindShouldMessageShowTimeText(binding.timeText, item, currentList);
            itemView.setOnClickListener(v -> onItemClickListener.onItemClick("itemView", item, getAdapterPosition()));
        }
    }

}
