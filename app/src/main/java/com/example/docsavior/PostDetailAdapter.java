package com.example.docsavior;

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

public class PostDetailAdapter extends ArrayAdapter<PostDetail> {
    private final Activity context;
    public PostDetailAdapter(Activity context, int layoutID, List<PostDetail> objects) {
        super(context, layoutID, objects);
        this.context = context;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_post_detail, null, false);
        }
        // Get item
        PostDetail pd = getItem(position);

        // findViewByIds
        TextView username = (TextView) convertView.findViewById(R.id.tvUsername);
        TextView comment = (TextView) convertView.findViewById(R.id.tvComment);
        ImageView profileImgs = (ImageView) convertView.findViewById(R.id.profileImg);
        TextView tvDateTime = convertView.findViewById(R.id.tvDateTime);

        // initVariables
        username.setText(pd.getUsername());
        getUserInfoAndSetAvatar(profileImgs, pd.getUsername());
        comment.setText(pd.getComment());

        // set comment's datetime
        setCommentDateTime(tvDateTime, pd.getTime());

        // setOnClickListener
        profileImgs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, ProfileActivity.class);
                myIntent.putExtra(ApplicationInfo.KEY_TO_PROFILE_ACTIVITY, pd.getUsername());
                context.startActivity(myIntent);
            }
        });

        return convertView;
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

    private void setCommentDateTime(TextView textView, long time) {
        Date date = new Date(time * 1000);

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        String dateFormatted = formatter.format(date);

        textView.setText(dateFormatted);
    }
}
