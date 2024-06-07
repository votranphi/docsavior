package com.example.docsavior;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;

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
        setContentView(R.layout.new_feed_navigation);

        // Khởi tạo ViewPager2 cho 5 fragment, khởi tạo thanh điều hướng bottomNavigation
        ViewPager2 viewPager2 = findViewById(R.id.viewPager);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);


        //Gắn adapter cho ViewPager2
        ViewPager2Adapter viewPager2Adapter = new ViewPager2Adapter(this);
        viewPager2.setAdapter(viewPager2Adapter);

        // Chức năng cho ViewPager2 mỗi khi lướt tới 1 viewpager nào đó sẽ đồng bộ với thanh điều hướng tuương ứng
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.btnHome).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.btnChat).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.btnFriend).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.btnNotification).setChecked(true);
                        break;
                    case 4:
                        bottomNavigationView.getMenu().findItem(R.id.btnSetting).setChecked(true);
                        break;
                }
            }
        });

        // Chức năng cho thanh điều hướng mỗi khi ấn 1 button nào đó sẽ đồng bộ với ViewPager2 tương ứng
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                int itemId = menuItem.getItemId();

                if (itemId == R.id.btnHome) {
                    viewPager2.setCurrentItem(0);
                } else if (itemId == R.id.btnChat) {
                    viewPager2.setCurrentItem(1);
                } else if (itemId == R.id.btnFriend) {
                    viewPager2.setCurrentItem(2);
                } else if (itemId == R.id.btnNotification) {
                    viewPager2.setCurrentItem(3);
                } else if (itemId == R.id.btnSetting) {
                    viewPager2.setCurrentItem(4);
                }
                return true;
            }
        });









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
