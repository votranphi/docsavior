package com.example.docsavior;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {
    private final Activity context;
    private List<Notification> notificationList;

    private NotificationInterface notificationInterface;

    public NotificationAdapter(Activity context, List<Notification> notificationList, NotificationInterface notificationInterface) {
        this.context = context;
        this.notificationList = notificationList;
        this.notificationInterface = notificationInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View notificationView = inflater.inflate(R.layout.item_notification, parent, false);
        ViewHolder viewHolder = new ViewHolder(notificationView);
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        // Get item
        Notification noti = notificationList.get(position);

        if (noti == null) {
            return;
        }

        // get user's avatar and set
        getUserInfoAndSetAvatar(holder.profileImg, noti.getInteracter());

        // set notification datetime
        setNotificationDateTime(holder.tvDateTime, noti.getTime());

        // set the notification's content
        holder.tvContent.setText(noti.getInteracter() + " " + noti.getNotificationContent());

        setOnClickListeners(holder, position);
    }

    private void setOnClickListeners(ViewHolder holder, int position) {
        holder.profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, ProfileActivity.class);
                myIntent.putExtra(ApplicationInfo.KEY_TO_PROFILE_ACTIVITY, notificationList.get(position).getInteracter());
                context.startActivity(myIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return  notificationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImg;
        TextView tvContent;
        TextView tvDateTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.profileImg);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notificationInterface.onItemClick(getLayoutPosition());
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

    private void setNotificationDateTime(TextView textView, long time) {
        Date date = new Date(time * 1000);

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        String dateFormatted = formatter.format(date);

        textView.setText(dateFormatted);
    }
}
