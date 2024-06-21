package com.example.docsavior;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
public class FriendsMessageAdapter extends RecyclerView.Adapter<FriendsMessageAdapter.FriendsMessageViewHolder>  {


    // Both my message and friend's message use the same Message.java
    private List<Message> mListMessage;

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Message> list) {
        this.mListMessage = list;
        notifyDataSetChanged();
    }

    @NonNull
    public FriendsMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friends_message_chat_detail, parent, false);
        return new FriendsMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendsMessageViewHolder holder, int position) {
        Message message = mListMessage.get(position);
        if (message == null) {
            return;
        }
        holder.tvFriendsMessage.setText(message.getMessage());
    }

    @Override
    public int getItemCount() {
        return mListMessage.size();
    }

    public static class FriendsMessageViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvFriendsMessage;
        private final ImageView imgFriendsMessage;
        public FriendsMessageViewHolder(@NonNull View itemView) {
            super(itemView);

            tvFriendsMessage = itemView.findViewById(R.id.tvFriendsMessage);
            imgFriendsMessage = itemView.findViewById(R.id.imgFriendsMessage);
        }
    }
}