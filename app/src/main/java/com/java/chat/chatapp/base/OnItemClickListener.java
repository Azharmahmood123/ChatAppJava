package com.java.chat.chatapp.base;

public interface OnItemClickListener<Model> {
    void onItemClick(String clickTag, Model model, int position);
}
