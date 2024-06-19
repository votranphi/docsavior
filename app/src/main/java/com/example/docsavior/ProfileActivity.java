package com.example.docsavior;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    ImageView imgUserAvatar;
    TextView tvUsername;
    Button btnAddfriend;
    Button btnMessage;
    ListView gvPosts;
    ProfileAdapter profileAdapter;
    ArrayList<String> stringArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        findViewByIds();

        initVariables();

        setOnClickListeners();

        // TODO: get the user's info then display it on the layout
        // TODO: get the user's posts then display it on the ListView
    }

    private void findViewByIds() {
        imgUserAvatar = findViewById(R.id.imgUserAvatar);
        tvUsername = findViewById(R.id.tvUsername);
        btnAddfriend = findViewById(R.id.btnAddfriend);
        btnMessage = findViewById(R.id.btnMessage);
        gvPosts = findViewById(R.id.gvPosts);
    }

    private void setOnClickListeners() {
        btnAddfriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initVariables() {
        stringArrayList = new ArrayList<>();
        profileAdapter = new ProfileAdapter(this, R.layout.item_profile, stringArrayList);
    }
}