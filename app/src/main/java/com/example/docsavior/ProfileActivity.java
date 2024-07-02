package com.example.docsavior;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

    private ImageView imgUserAvatar;
    private TextView tvFullname;
    private Button btnAddfriend;
    private Button btnListFriend;
    private Button btnDecline;
    private Button btnMessage;
    private RecyclerView gvPosts;
    private NewsfeedAdapter newsFeedAdapter;
    private ArrayList<Newsfeed> newsfeedArrayList;

    private ImageButton btnGoToDetails;
    private ImageButton btnClose;

    private static final int REQUEST_CODE_OPEN_DOCUMENT = 1;
    private String fileData = "";
    private String fileName = "";
    private String fileExtension = "";

    private User user = null;
    private String username = "";
    private boolean isMyProfile = false;

    public static String KEY_TO_PROFILE_DETAIL_ACTIVITY = "username_fullname_email_status_gender_birthdate_phone";

    private TextView tvNothing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        findViewByIds();

        initVariables();

        setOnClickListeners();

        // get the user's info then display it on the layout
        getUserInfo();

        // get the user's posts then display it on the ListView
        getMyPost(username);

        // init isMyProfile variable
        isMyProfile = ApplicationInfo.username.equals(username);

        // check if this is my profile's view
        if (isMyProfile) {
            // if it's mine
            btnMessage.setVisibility(View.GONE);
            btnAddfriend.setVisibility(View.GONE);
            btnDecline.setVisibility(View.GONE);
            btnListFriend.setVisibility(View.VISIBLE);
        } else {
            // if it isn't mine
            btnListFriend.setVisibility(View.GONE);

            // check and set the function of the btnAddFriend
            checkAndSetTheBtnAddFriend();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(ApplicationInfo.activityLifecycleManager);
        }
    }

    private void findViewByIds() {
        imgUserAvatar = findViewById(R.id.imgUserAvatar);
        tvFullname = findViewById(R.id.tvFullname);
        btnAddfriend = findViewById(R.id.btnAddfriend);
        btnListFriend = findViewById(R.id.btnListFriend);
        btnDecline = findViewById(R.id.btnDecline);
        btnMessage = findViewById(R.id.btnMessage);
        gvPosts = findViewById(R.id.gvPosts);
        btnGoToDetails = findViewById(R.id.btnGoToDetails);
        tvNothing = findViewById(R.id.tvNothing);
        btnClose = findViewById(R.id.btnClose);
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
                String btnText = btnAddfriend.getText().toString();
                if (btnText.equals("Accept")) {
                    // call API to delete friend request from the database
                    deleteFriendRequest(ApplicationInfo.username, username);
                    // call API to add friend in the database
                    postNewFriend(username);

                    // call API to post notification
                    postNotification(username, 4, -1, ApplicationInfo.username);

                    btnDecline.setVisibility(View.GONE);
                    btnAddfriend.setText("Unfriend");
                } else if (btnText.equals("Cancel request")) {
                    // call API to delete friend request from database
                    deleteFriendRequest(username, ApplicationInfo.username);

                    // delete notification from database
                    deleteNotification(username, 3, -1, ApplicationInfo.username);

                    btnAddfriend.setText("Add friend");
                } else if (btnText.equals("Unfriend")) {
                    // call API to delete friend
                    deleteFriend(username, ApplicationInfo.username);

                    btnAddfriend.setText("Add friend");
                } else { // btnText.equals("Add friend")
                    // call API to post friend request
                    postFriendRequest(username);

                    // call API to post notification
                    postNotification(username, 3, -1, ApplicationInfo.username);

                    btnAddfriend.setText("Cancel request");

                    btnDecline.setVisibility(View.GONE);
                }
            }
        });

        btnListFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open FriendListActivity then show the FriendList
                Intent myIntent = new Intent(ProfileActivity.this, FriendListActivity.class);
                startActivity(myIntent);
            }
        });

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call API to delete friend request from the database
                deleteFriendRequest(ApplicationInfo.username, username);

                // call API to post notification
                postNotification(username, 5, -1, ApplicationInfo.username);

                btnDecline.setVisibility(View.GONE);

                btnAddfriend.setText("Add friend");
            }
        });

        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open ChatDetailActivity then do the things
                Intent myIntent = new Intent(ProfileActivity.this, ChatDetailActivity.class);
                myIntent.putExtra(ApplicationInfo.KEY_TO_CHAT_DETAIL_ACTIVITY, username);
                startActivity(myIntent);
            }
        });

        btnGoToDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(ProfileActivity.this, ProfileDetailActivity.class);
                // prepare ArrayList to put
                ArrayList<String> arrayToPut = new ArrayList<>();
                arrayToPut.add(user.getUsername());
                arrayToPut.add(user.getFullName());
                arrayToPut.add(user.getEmail());
                arrayToPut.add(String.valueOf(user.getIsActive()));
                arrayToPut.add(String.valueOf(user.getGender()));
                arrayToPut.add(user.getBirthDate());
                arrayToPut.add(user.getPhoneNumber());
                // put array list to intent
                myIntent.putStringArrayListExtra(KEY_TO_PROFILE_DETAIL_ACTIVITY, arrayToPut);
                // start activity
                startActivity(myIntent);
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initVariables() {
        newsfeedArrayList = new ArrayList<>();
        newsFeedAdapter = new NewsfeedAdapter(this, newsfeedArrayList);
        gvPosts.setAdapter(newsFeedAdapter);
        gvPosts.setLayoutManager(new LinearLayoutManager(this));

        // retrieve the isMyInfo from NewsfeedFragment
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
            tvFullname.setText(user.getFullName());

            // set avatar
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
                imgUserAvatar.post(new Runnable() {
                    @Override
                    public void run() {
                        imgUserAvatar.setImageBitmap(Bitmap.createScaledBitmap(bmp, imgUserAvatar.getWidth(), imgUserAvatar.getHeight(), false));
                    }
                });
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

        User user = new User(ApplicationInfo.username, fileData, fileName, fileExtension);

        Call<Detail> call = apiService.postAvatar(user);

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

    private void getMyPost(String username) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<Newsfeed>> call = apiService.getMyPost(username);

        call.enqueue(new Callback<List<Newsfeed>>() {
            @Override
            public void onResponse(Call<List<Newsfeed>> call, Response<List<Newsfeed>> response) {
                try {
                    if (response.isSuccessful()) {
                        List<Newsfeed> responseList = response.body();

                        // add the elements in responseList to newsfeedArrayList
                        for (Newsfeed i : responseList) {
                            newsfeedArrayList.add(i);
                            // update the ListView every one post
                            newsFeedAdapter.notifyDataSetChanged();
                        }

                        // set the visibility of "NOTHING TO SHOW" to GONE
                        tvNothing.setVisibility(View.GONE);
                    } else if (response.code() == 600) {
                        // set the visibility of "NOTHING TO SHOW" to VISIBLE
                        tvNothing.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(ProfileActivity.this, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR100: ", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<List<Newsfeed>> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postFriendRequest(String username) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Detail> call = apiService.postFriendRequest(username, ApplicationInfo.username);

        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                try {
                    if (response.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Sent friend request to " + username + "!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR1: ", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "FAILURE: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void postNotification(String username, Integer type,  Integer idPost, String interacter) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Detail> call = apiService.postNotification(username, type, idPost, interacter);

        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                try {
                    if (response.isSuccessful()) {
                        // do nothing
                    } else {
                        Toast.makeText(ProfileActivity.this, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR106: ", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkAndSetTheBtnAddFriend() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Requester> call = apiService.getAllFriendRequests(ApplicationInfo.username);

        call.enqueue(new Callback<Requester>() {
            @Override
            public void onResponse(Call<Requester> call, Response<Requester> response) {
                try {
                    if (response.isSuccessful()) {
                        // check if this user sent "me" a friend request
                        for (String i : response.body().getRequesters()) {
                            if (username.equals(i)) {
                                btnAddfriend.setVisibility(View.VISIBLE);
                                btnDecline.setVisibility(View.VISIBLE);
                                btnMessage.setVisibility(View.GONE);
                                btnAddfriend.setText("Accept");
                                return;
                            }
                        }

                        // check if I've sent this user a friend request
                        check1();
                    } else {
                        Toast.makeText(ProfileActivity.this, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR1: ", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Requester> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "FAILURE: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void check1() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Requester> call = apiService.getAllFriendRequests(username);

        call.enqueue(new Callback<Requester>() {
            @Override
            public void onResponse(Call<Requester> call, Response<Requester> response) {
                try {
                    if (response.isSuccessful()) {
                        // check if I've sent this user a friend request
                        for (String i : response.body().getRequesters()) {
                            if (ApplicationInfo.username.equals(i)) {
                                btnAddfriend.setVisibility(View.VISIBLE);
                                btnDecline.setVisibility(View.GONE);
                                btnMessage.setVisibility(View.GONE);
                                btnAddfriend.setText("Cancel request");
                                return;
                            }
                        }

                        // check if both are friend
                        check2();
                    } else {
                        Toast.makeText(ProfileActivity.this, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR1: ", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Requester> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "FAILURE: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void check2() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Friends> call = apiService.getAllFriends(username);

        call.enqueue(new Callback<Friends>() {
            @Override
            public void onResponse(Call<Friends> call, Response<Friends> response) {
                try {
                    if (response.isSuccessful()) {
                        // check if both are friend
                        for (String i : response.body().getFriends()) {
                            if (ApplicationInfo.username.equals(i)) {
                                btnAddfriend.setVisibility(View.VISIBLE);
                                btnDecline.setVisibility(View.GONE);
                                btnMessage.setVisibility(View.VISIBLE);
                                btnAddfriend.setText("Unfriend");
                                return;
                            }
                        }

                        btnAddfriend.setVisibility(View.VISIBLE);
                        btnDecline.setVisibility(View.GONE);
                        btnMessage.setVisibility(View.GONE);
                        btnAddfriend.setText("Add friend");
                    } else {
                        Toast.makeText(ProfileActivity.this, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR1: ", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Friends> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "FAILURE: " + t.getMessage(), Toast.LENGTH_LONG).show();
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
                        // do nothing
                    } else {
                        Toast.makeText(ProfileActivity.this, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR1: ", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "FAILURE: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void postNewFriend(String username) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Detail> call = apiService.postNewFriend(ApplicationInfo.username, username);

        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                try {
                    if (response.isSuccessful()) {
                        Toast.makeText(ProfileActivity.this, "Accepted " + username + " friend request!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ProfileActivity.this, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR1: ", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "FAILURE: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteNotification(String username, Integer type,  Integer idPost, String interacter) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Detail> call = apiService.deleteNotification(username, type, idPost, interacter);

        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                try {
                    if (response.isSuccessful()) {
                        // do nothing
                    } else {
                        Toast.makeText(ProfileActivity.this, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR106: ", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteFriend(String username, String usernameFriend) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Detail> call = apiService.deleteFriend(username, usernameFriend);

        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                try {
                    if (response.isSuccessful()) {
                        // do nothing
                    } else {
                        Toast.makeText(ProfileActivity.this, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR106: ", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}