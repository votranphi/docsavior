package com.example.docsavior;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FriendAdapter extends ArrayAdapter<Friend> {
    private final Activity context;
    private ImageView profileImg;
    private TextView tvUsername;
    private ImageButton btnAccept;
    private ImageButton btnDecline;

    public FriendAdapter(Activity context, int layoutID, List<Friend> objects) {
        super(context, layoutID, objects);
        this.context = context;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_friend, null, false);
        }
        // Get item
        Friend fr = getItem(position);

        // Get view
        findViewByIds(convertView);

        // set click listeners
        setOnClickListeners();

        // image
        tvUsername.setText(fr.getUsername());
        return convertView;
    }

    private void findViewByIds(View convertView) {
        profileImg = convertView.findViewById(R.id.profileImg);
        tvUsername = convertView.findViewById(R.id.tvUsername);
        btnAccept = convertView.findViewById(R.id.btnAccept);
        btnDecline = convertView.findViewById(R.id.btnDecline);
    }

    private void setOnClickListeners() {
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: call delete friend request and call post friend
            }
        });

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                deleteFriendRequest(ApplicationInfo.username, tvUsername.getText().toString());
            }
        });
    }

    private void deleteFriendRequest(String username, String requester) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Detail> call = apiService.deleteFriendRequest(username, requester);

        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                try {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Declined " + requester + " friend request!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR1: ", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(context, "FAILURE: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
