package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewFeedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_feed);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.3.131:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService retrofitAPI = retrofit.create(ApiService.class);

        Call<List<Newfeed>> call = retrofitAPI.getAllPosts();

        call.enqueue(new Callback<List<Newfeed>>() {
            @Override
            public void onResponse(Call<List<Newfeed>> call, Response<List<Newfeed>> response) {
                if (response.isSuccessful()) {
                    ListView lvPost = findViewById(R.id.lvPost);
                    ArrayList<Newfeed> posts = new ArrayList<>();

                    for (Newfeed i : response.body()) {
                        posts.add(new Newfeed(i.getId(), i.getUsername(), i.getPostDescription(), i.getPostContent(), i.getLikeNumber(), i.getDislikeNumber(), i.getCommentNumber()));
                    }

                    NewfeedAdapter newfeedAdapter = new NewfeedAdapter(NewFeedActivity.this, R.layout.new_feed_item, posts);

                    lvPost.setAdapter(newfeedAdapter);
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        Toast.makeText(NewFeedActivity.this, jsonObject.get("detail").toString(), Toast.LENGTH_SHORT).show();
                    } catch (Exception ex) {

                    }
                }
            }

            @Override
            public void onFailure(Call<List<Newfeed>> call, Throwable t) {
                Toast.makeText(NewFeedActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
