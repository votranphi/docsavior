package com.example.docsavior;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FriendListActivity extends AppCompatActivity {

    private ImageButton btnClose;
    private RecyclerView rcvFriendList;
    private TextView tvNothing;

    private FriendAdapter friendAdapter;
    private ArrayList<Friend> friendArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_list);

        findViewByIds();

        setOnClickListeners();

        initVariables();

        getAllMyFriends();
    }

    private void findViewByIds() {
        btnClose = findViewById(R.id.btnClose);
        rcvFriendList = findViewById(R.id.rcvFriendList);
        tvNothing = findViewById(R.id.tvNothing);
    }

    private void setOnClickListeners() {
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
            }
        });
    }

    private void initVariables() {
        friendArrayList = new ArrayList<>();
        friendAdapter = new FriendAdapter(this, friendArrayList, 0);

        rcvFriendList.setHasFixedSize(true);
        rcvFriendList.setItemViewCacheSize(20);
        friendAdapter.setHasStableIds(true);

        rcvFriendList.setAdapter(friendAdapter);
        rcvFriendList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void getAllMyFriends() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Friends> call = apiService.getAllFriends(ApplicationInfo.username);

        call.enqueue(new Callback<Friends>() {
            @Override
            public void onResponse(Call<Friends> call, Response<Friends> response) {
                try {
                    if (response.isSuccessful()) {
                        Friends friends = response.body();
                        if (friends.getFriends().length == 0) {
                            tvNothing.setVisibility(View.VISIBLE);
                        } else {
                            tvNothing.setVisibility(View.GONE);

                            assignFriendsToListView(friends);
                        }
                    } else {
                        Toast.makeText(FriendListActivity.this, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR1: ", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Friends> call, Throwable t) {
                Toast.makeText(FriendListActivity.this, "FAILURE: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void assignFriendsToListView(Friends friends) {
        try {
            for (int i = 0; i < friends.getFriends().length; i++) {
                getAvatarDataThenAddToArrayList(friends.getFriends()[i]);
            }
        } catch (Exception ex) {
            Log.e("ERROR2: ", ex.getMessage());
        }
    }

    private void getAvatarDataThenAddToArrayList(String username) {
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
                        Friend friend = new Friend(response.body().getDetail(), username);
                        friendArrayList.add(friend);
                        friendAdapter.notifyItemInserted(friendArrayList.size() - 1);
                    } else {
                        Toast.makeText(FriendListActivity.this, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR106: ", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(FriendListActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}