package com.example.docsavior;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>  {
    private Context context;
    private List<Message> messageList;
    private List<Integer> isMyMessage = new ArrayList<>(); // 1 if message at position is my message, 0 if it's not

    private String myAvatarData = null;
    private String friendAvatarData = null;

    public MessageAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (message == null) {
            return;
        }

        if (message.getSender().equals(ApplicationInfo.username)) {
            isMyMessage.add(1);

            holder.tvMessageMy.setText(message.getContent());

            setMessageDateTime(holder.tvDateTimeMy, message.getTime());

            holder.llMy.setVisibility(View.VISIBLE);
            holder.llFriend.setVisibility(View.GONE);
        } else {
            isMyMessage.add(0);

            holder.tvMessageFriend.setText(message.getContent());

            setMessageDateTime(holder.tvDateTimeFriend, message.getTime());

            holder.llMy.setVisibility(View.GONE);
            holder.llFriend.setVisibility(View.VISIBLE);
        }

        if (myAvatarData == null && friendAvatarData == null) {
            getMyAvatarBitmap(ApplicationInfo.username, holder.imgProfileMy);
            if (isMyMessage.get(position) == 1) {
                getFriendAvatarBitmap(message.getUsername(), holder.imgProfileFriend);
            } else { // if (isMyMessage.get(position) == 0)
                getFriendAvatarBitmap(message.getSender(), holder.imgProfileFriend);
            }
        } else {
            if (isMyMessage.get(position) == 1) {
                setImage(holder.imgProfileMy, myAvatarData);
            } else { // if (isMyMessage.get(position) == 0)
                setImage(holder.imgProfileFriend, friendAvatarData);
            }
        }

        setOnClickListeners(holder, position);
    }

    private void setOnClickListeners(MessageAdapter.ViewHolder holder, int position) {
        holder.imgProfileMy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, ProfileActivity.class);
                myIntent.putExtra(ApplicationInfo.KEY_TO_PROFILE_ACTIVITY, ApplicationInfo.username);
                context.startActivity(myIntent);
            }
        });

        holder.imgProfileFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, ProfileActivity.class);
                myIntent.putExtra(ApplicationInfo.KEY_TO_PROFILE_ACTIVITY, messageList.get(position).getSender());
                context.startActivity(myIntent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout llMy;
        LinearLayout llFriend;

        ImageView imgProfileMy;
        TextView tvMessageMy;
        TextView tvDateTimeMy;

        ImageView imgProfileFriend;
        TextView tvMessageFriend;
        TextView tvDateTimeFriend;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            llMy = itemView.findViewById(R.id.llMy);
            llFriend = itemView.findViewById(R.id.llFriend);

            imgProfileMy = itemView.findViewById(R.id.imgProfileMy);
            tvMessageMy = itemView.findViewById(R.id.tvMessageMy);
            tvDateTimeMy = itemView.findViewById(R.id.tvDateTimeMy);

            imgProfileFriend = itemView.findViewById(R.id.imgProfileFriend);
            tvMessageFriend = itemView.findViewById(R.id.tvMessageFriend);
            tvDateTimeFriend = itemView.findViewById(R.id.tvDateTimeFriend);
        }
    }

    private void setMessageDateTime(TextView textView, long time) {
        Date date = new Date(time * 1000);

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        String dateFormatted = formatter.format(date);

        textView.setText(dateFormatted);
    }

    private void getMyAvatarBitmap(String username, ImageView imageView) {
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
                        myAvatarData = response.body().getDetail();

                        setImage(imageView, myAvatarData);
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

    private void getFriendAvatarBitmap(String username, ImageView imageView) {
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
                        friendAvatarData = response.body().getDetail();

                        setImage(imageView, friendAvatarData);
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
