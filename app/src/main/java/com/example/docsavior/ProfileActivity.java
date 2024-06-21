package com.example.docsavior;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.documentfile.provider.DocumentFile;

import org.json.JSONArray;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileActivity extends AppCompatActivity {

    ImageView imgUserAvatar;
    TextView tvUsername;
    Button btnAddfriend;
    Button btnMessage;
    ListView gvPosts;
    ProfileAdapter profileAdapter;
    ArrayList<String> stringArrayList;

    ImageButton btnGoToDetails;

    private static final int REQUEST_CODE_OPEN_DOCUMENT = 1;
    private String fileData = "";
    private String fileName = "";
    private String fileExtension = "";

    private User user = null;
    private String username = "";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        findViewByIds();

        initVariables();

        setOnClickListeners();

        // TODO: get the user's info then display it on the layout
        getUserInfo();

        // TODO: get the user's posts then display it on the ListView
    }

    private void findViewByIds() {
        imgUserAvatar = findViewById(R.id.imgUserAvatar);
        tvUsername = findViewById(R.id.tvUsername);
        btnAddfriend = findViewById(R.id.btnAddfriend);
        btnMessage = findViewById(R.id.btnMessage);
        gvPosts = findViewById(R.id.gvPosts);
        btnGoToDetails = findViewById(R.id.btnGoToDetails);
    }

    private void setOnClickListeners() {
        imgUserAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // request for permission


                openFileChooser();
            }
        });

        btnAddfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: send the add friend signal via API to add a record to friend request table, then change the text of the button
            }
        });

        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: open ChatDetailActivity then do the things
            }
        });
    }

    private void initVariables() {
        stringArrayList = new ArrayList<>();
        profileAdapter = new ProfileAdapter(this, R.layout.item_profile, stringArrayList);
        gvPosts.setAdapter(profileAdapter);

        // retrieve the isMyInfo from NewsFeedFragment
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            username = extras.getString(ApplicationInfo.KEY_TO_PROFILE_ACTIVITY);
        }
    }

    private void getUserInfo() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<User> call = apiService.getUserInfo(username);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                try {
                    if (response.isSuccessful()) {
                        user = response.body();
                        // after getting the user info, assign it to layout
                        assignUserInfoToLayout();
                    } else {
                        Toast.makeText(ProfileActivity.this, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR100: ", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void assignUserInfoToLayout() {
        try {
            // set username
            tvUsername.setText(user.getUsername());

            if (!user.getAvatarData().isEmpty()) {
                // create jsonArray to store fileData
                JSONArray jsonArray = new JSONArray(user.getAvatarData());
                // convert jsonArray to byteArray
                byte[] byteArray = new byte[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    int temp = (int)jsonArray.get(i);
                    byteArray[i] = (byte)temp;
                }

                // convert byteArray to bitmap
                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                // set the avatar
                imgUserAvatar.setImageBitmap(Bitmap.createScaledBitmap(bmp, imgUserAvatar.getWidth(), imgUserAvatar.getHeight(), false));
            }
        } catch (Exception ex) {
            Log.e("ERROR230: ", ex.getMessage());
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select a Image"), REQUEST_CODE_OPEN_DOCUMENT);
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN_DOCUMENT && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    try {
                        DocumentFile documentFile = DocumentFile.fromSingleUri(this, uri);
                        if (documentFile != null && documentFile.isFile()) {
                            // Read content as InputStream
                            InputStream inputStream = getContentResolver().openInputStream(uri);
                            byte[] bytes = new byte[inputStream.available()];
                            inputStream.read(bytes);
                            inputStream.close();

                            // create jsonArray to store byte array in it
                            JSONArray jsonArray = new JSONArray();
                            for (byte i : bytes) {
                                jsonArray.put(i);
                            }
                            // convert jsonArray to string for posting it
                            fileData = jsonArray.toString();

                            // get the file's full name
                            String fileFullName = documentFile.getName();
                            // split the fileFullName to get file's name and file's extension
                            String[] splitString = fileFullName.split("\\.");
                            fileName = splitString[0];
                            fileExtension = splitString[1];

                            // post the avatar to database
                            postAvatar(fileData, fileName, fileExtension);
                        }
                    } catch (Exception e) {
                        Log.e("ERROR123: ", e.getMessage());
                    }
                }
            }
        }
    }

    private void postAvatar(String fileData, String fileName, String fileExtension) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Detail> call = apiService.postAvatar(ApplicationInfo.username, fileData, fileName, fileExtension);

        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                try {
                    if (response.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Update avatar successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, response.code() + response.errorBody().string(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR1: ", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}