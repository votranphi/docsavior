package com.example.docsavior;

import android.app.Application;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.permissionx.guolindev.PermissionX;
import com.permissionx.guolindev.callback.ExplainReasonCallback;
import com.permissionx.guolindev.callback.RequestCallback;
import com.permissionx.guolindev.request.ExplainScope;
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChatDetailActivity extends AppCompatActivity {

    ImageView btnFriendProfile;
    ZegoSendCallInvitationButton btnVideoCall;
    ZegoSendCallInvitationButton btnVoiceCall;
    ImageButton btnClose;
    TextView tvFriendUsername;
    TextView tvStatus;
    EditText edMessageBody;
    ImageButton btnSendMessage;
    TextView tvNothing;

    RecyclerView rcvMessage;
    MessageAdapter messageAdapter;
    ArrayList<Message> messageArrayList;

    private String username;

    private MessageLoader messageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_detail);

        findViewByIds();

        setOnClickListeners();

        initVariables();

        loadMessages(ApplicationInfo.username, username);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            registerActivityLifecycleCallbacks(ApplicationInfo.activityLifecycleManager);
        }
    }

    private void findViewByIds() {
        btnFriendProfile = findViewById(R.id.btnFriendProfile);
        btnVideoCall = findViewById(R.id.btnVideoCall);
        btnVoiceCall = findViewById(R.id.btnVoiceCall);
        btnClose = findViewById(R.id.btnClose);
        tvFriendUsername = findViewById(R.id.tvFriendUsername);
        tvStatus = findViewById(R.id.tvStatus);
        rcvMessage = findViewById(R.id.rcvMessage);
        edMessageBody = findViewById(R.id.edMessageBody);
        btnSendMessage = findViewById(R.id.btnSendMessage);
        tvNothing = findViewById(R.id.tvNothing);
    }

    private void setOnClickListeners() {
        btnFriendProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start ProfileActivity and do things

                Intent myIntent = new Intent(ChatDetailActivity.this, ProfileActivity.class);
                myIntent.putExtra(ApplicationInfo.KEY_TO_PROFILE_ACTIVITY, username);
                startActivity(myIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
            }
        });

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call API to add message and also add to RecyclerView

                if (edMessageBody.getText().toString().isEmpty()) {
                    Toast.makeText(ChatDetailActivity.this, "Please enter a message to send!", Toast.LENGTH_SHORT).show();
                    return;
                }

                postMessage(username, ApplicationInfo.username, edMessageBody.getText().toString());

                addMessageToRecyclerView(username, ApplicationInfo.username, edMessageBody.getText().toString());

                edMessageBody.setText("");
            }
        });

        btnVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start calling this user
                if (tvStatus.getText().toString().equals("Offline")) {
                    Toast.makeText(ChatDetailActivity.this, "This user is offline!", Toast.LENGTH_SHORT).show();
                    return;
                }

                setVideoCall(username);
            }
        });

        btnVoiceCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tvStatus.getText().toString().equals("Offline")) {
                    Toast.makeText(ChatDetailActivity.this, "This user is offline!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // start calling this user
                setVoiceCall(username);
            }
        });
    }

    private void initVariables() {
        messageArrayList = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, messageArrayList);
        rcvMessage.setAdapter(messageAdapter);
        rcvMessage.setLayoutManager(new LinearLayoutManager(this));

        // retrieve the username from ConversationFragment
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            username = extras.getString(ApplicationInfo.KEY_TO_CHAT_DETAIL_ACTIVITY);
            tvFriendUsername.setText(username);

            getAndSetFriendAvatar(btnFriendProfile, username);

            messageLoader = new MessageLoader(this, messageAdapter, messageArrayList, username, tvStatus);
        }
    }

    private void getAndSetFriendAvatar(ImageView imageView, String username) {
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
                        Toast.makeText(ChatDetailActivity.this, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR106: ", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(ChatDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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
                imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, imageView.getWidth(), imageView.getHeight(), false));
            }
        } catch (Exception ex) {
            Log.e("ERROR111: ", ex.getMessage());
        }
    }

    private void loadMessages(String username, String sender) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<Message>> call = apiService.getAllMyMessage(username, sender);

        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                try {
                    if (response.isSuccessful()) {
                        List<Message> messages = response.body();

                        if (messages.isEmpty()) {
                            tvNothing.setVisibility(View.VISIBLE);
                        } else {
                            tvNothing.setVisibility(View.GONE);

                            for (int i = 0; i < messages.size(); i++) {
                                postSeenToTrue(messages.get(i).getId(), i == messages.size() - 1);
                                messageArrayList.add(messages.get(i));
                                messageAdapter.notifyDataSetChanged();
                            }
                        }
                    } else {
                        Toast.makeText(ChatDetailActivity.this, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR100: ", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Toast.makeText(ChatDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postMessage(String username, String sender, String content) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Detail> call = apiService.postMessage(username, sender, content);

        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                try {
                    if (response.isSuccessful()) {
                        // do nothing
                    } else {
                        Toast.makeText(ChatDetailActivity.this, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR106: ", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(ChatDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addMessageToRecyclerView(String username, String sender, String content) {
        Message message = new Message(username, sender, content);
        messageArrayList.add(message);
        messageAdapter.notifyDataSetChanged();
    }

    private void postSeenToTrue(Integer id, boolean isLastMessage) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Detail> call = apiService.postSeenToTrue(id);

        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                try {
                    if (response.isSuccessful()) {
                        if (isLastMessage) {
                            messageLoader.start();
                        }
                    } else {
                        Toast.makeText(ChatDetailActivity.this, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR106: ", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(ChatDetailActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setVoiceCall(String targetUserID) {
        btnVoiceCall.setIsVideoCall(false);
        btnVoiceCall.setResourceID("zego_uikit_call");
        btnVoiceCall.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserID)));
    }

    private void setVideoCall(String targetUserID) {
        btnVideoCall.setIsVideoCall(true);
        btnVideoCall.setResourceID("zego_uikit_call");
        btnVideoCall.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserID)));
    }
}