package com.example.docsavior;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {
    private final Activity context;
    private List<Conversation> conversationList = new ArrayList<>();
    private ConversationInterface conversationInterface;
    public ConversationAdapter(Activity context, List<Conversation> conversationList, ConversationInterface conversationInterface) {
        this.context = context;
        this.conversationList = conversationList;
        this.conversationInterface = conversationInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View notificationView = inflater.inflate(R.layout.item_chat, parent, false);
        ViewHolder viewHolder = new ViewHolder(notificationView);
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        // Get item
        Conversation conversation = conversationList.get(position);

        if (conversation == null) {
            return;
        }

        getUserInfoAndSetAvatar(holder.btnFriendProfile, conversation.getUsername());

        holder.tvFriendUsername.setText(conversation.getUsername());

        setOnClickListeners(holder, position);

        holder.tvLastMessage.setText(conversation.getLastMessage());
        if (!conversation.getIsSeen()) {
            holder.tvLastMessage.setTypeface(Typeface.DEFAULT_BOLD);
        }
    }

    private void setOnClickListeners(ViewHolder holder, int position) {
        holder.btnFriendProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, ProfileActivity.class);
                myIntent.putExtra(ApplicationInfo.KEY_TO_PROFILE_ACTIVITY, conversationList.get(position).getUsername());
                context.startActivity(myIntent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return  conversationList.size();
    }

    @Override
    public long getItemId(int position) {
        return conversationList.get(position).hashCode();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView btnFriendProfile;
        TextView tvFriendUsername;
        TextView tvLastMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnFriendProfile = itemView.findViewById(R.id.btnFriendProfile);
            tvFriendUsername = itemView.findViewById(R.id.tvFriendUsername);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    conversationInterface.onItemClick(getLayoutPosition());
                }
            });
        }
    }

    private void getUserInfoAndSetAvatar(ImageView imageView, String username) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Detail> call = apiService.getAvatarData(username);

        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                try {
                    if (response.isSuccessful()) {
                        // setting the post's admin avatar after complete loading
                        setImage(imageView, response.body().getDetail());
                    } else {
                        Toast.makeText(context, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR106: ", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setImage(ImageView imageView, String avatarData) {
        if (!avatarData.isEmpty()) {
            StringToImageViewAsync stringToImageViewAsync = new StringToImageViewAsync(context, avatarData, imageView, false);
            stringToImageViewAsync.execute();
        }
    }
}
