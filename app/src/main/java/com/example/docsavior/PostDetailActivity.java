package com.example.docsavior;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostDetailActivity extends AppCompatActivity {

    private ImageButton btnClose;
    private ImageView profileImg;
    private TextView tvUsername;
    private TextView tvPostDesciption;
    private TextView tvPostContent;
    private ImageButton btnLike;
    private ImageButton btnDislike;
    private ImageButton btnDownload;
    private ListView lvComment;
    private EditText edComment;
    private ImageButton btnPost;

    private PostDetailAdapter postDetailAdapter;
    private ArrayList<PostDetail> postDetailArrayList;

    private ImageView imgPost;
    private TextView tvDocumentName;

    private NewsFeed newsFeed = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        findViewByIds();

        setOnClickListeners();

        initVariables();
    }

    private void findViewByIds() {
        btnClose = findViewById(R.id.btnClose);
        profileImg = findViewById(R.id.profileImg);
        tvUsername = findViewById(R.id.tvUsername);
        tvPostDesciption = findViewById(R.id.tvPostDesciption);
        tvPostContent = findViewById(R.id.tvPostContent);
        btnLike = findViewById(R.id.btnLike);
        btnDislike = findViewById(R.id.btnDislike);
        btnDownload = findViewById(R.id.btnDownload);
        lvComment = findViewById(R.id.lvComment);
        edComment = findViewById(R.id.edComment);
        btnPost = findViewById(R.id.btnPost);

        imgPost = findViewById(R.id.imgPost);
        tvDocumentName = findViewById(R.id.tvDocumentName);
    }

    private void setOnClickListeners() {
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initVariables() {
        postDetailArrayList = new ArrayList<>();
        postDetailAdapter = new PostDetailAdapter(this, R.layout.item_post_detail, postDetailArrayList);
        lvComment.setAdapter(postDetailAdapter);

        // retrieve the post's id from NewsFeedAdapter
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            int postId = extras.getInt(NewsFeedAdapter.KEY_TO_POST_DETAIL_ACTIVITY);

            // call API to get the newsfeed info again, because cannot put from previous activity
            getNewsfeedById(postId);
        }
    }

    private void loadPostInfo() {
        // set those TextViews
        tvUsername.setText(newsFeed.getUsername());
        tvPostDesciption.setText(newsFeed.getPostDescription());
        tvPostContent.setText(newsFeed.getPostContent());

        // load the image if the post is image
        if (newsFeed.getFileExtension().equals("jpg") || newsFeed.getFileExtension().equals("png") || newsFeed.getFileExtension().equals("jpeg")) {
            setImage(imgPost, newsFeed.getFileData());
        } else {
            imgPost.setVisibility(View.GONE);
        }

        // load the post's admin avatar
        getAndSetImage(profileImg, newsFeed.getUsername());
    }

    private void getNewsfeedById(int id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<NewsFeed> call = apiService.getNewsfeedById(id);

        call.enqueue(new Callback<NewsFeed>() {
            @Override
            public void onResponse(Call<NewsFeed> call, Response<NewsFeed> response) {
                try {
                    if (response.isSuccessful()) {
                        newsFeed = response.body();

                        // load post info after newsFeed is completely initialized
                        loadPostInfo();

                        // load post comment after newsFeed is completely initialized
                        loadPostComments();
                    } else {
                        Toast.makeText(PostDetailActivity.this, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR100: ", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<NewsFeed> call, Throwable t) {
                Toast.makeText(PostDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPostComments() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<Comment>> call = apiService.getPostComment(newsFeed.getId());

        call.enqueue(new Callback<List<Comment>>() {
            @Override
            public void onResponse(Call<List<Comment>> call, Response<List<Comment>> response) {
                try {
                    if (response.isSuccessful()) {
                        List<Comment> comments = response.body();

                        for (Comment i : comments) {
                            // call API to get avatarData
                            String avatarData = getAvatarData(i.getUsername());
                        }
                    } else {
                        Toast.makeText(PostDetailActivity.this, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR567: ", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<List<Comment>> call, Throwable t) {
                Toast.makeText(PostDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getAndSetImage(ImageView imageView, String username) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<String> call = apiService.getAvatarData(username);

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                try {
                    if (response.isSuccessful()) {
                        // setting the post's admin avatar after complete loading
                        setImage(imageView, response.body());
                    } else {
                        Toast.makeText(PostDetailActivity.this, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR106: ", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(PostDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setImage(ImageView imageView, String avatarData) {
        try {
            if (!avatarData.isEmpty()) {
                // create jsonArray to store avatarData
                JSONArray jsonArray = new JSONArray(avatarData);

                // convert jsonArray to byteArray
                byte[] byteArray = new byte[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    int temp = (int)jsonArray.get(i);
                    byteArray[i] = (byte)temp;
                }

                // convert byteArray to bitmap
                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                // set the avatar
                imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, 40, 40, false));
            }
        } catch (Exception ex) {
            Log.e("ERROR111: ", ex.getMessage());
        }
    }
}