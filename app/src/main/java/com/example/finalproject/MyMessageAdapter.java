package com.example.finalproject;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyMessageAdapter extends RecyclerView.Adapter<MyMessageAdapter.MyMessageViewHolder>  {

    private List<MyMessage> mListMessage;

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<MyMessage> list) {
        this.mListMessage = list;
        notifyDataSetChanged();
    }

    @NonNull
    public MyMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_message, parent, false);
        return new MyMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyMessageViewHolder holder, int position) {
        MyMessage message = mListMessage.get(position);
        if (message == null) {
            return;
        }
        holder.tvMyMessage.setText(message.getMyMessage());
    }

    @Override
    public int getItemCount() {
        return mListMessage.size();
    }

    public static class MyMessageViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvMyMessage;
        public MyMessageViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMyMessage = itemView.findViewById(R.id.tvMyMessage);
        }
    }
}
