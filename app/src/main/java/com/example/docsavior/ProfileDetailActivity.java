package com.example.docsavior;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileDetailActivity extends AppCompatActivity {

    private TextView tvPersonalInformation, tvUsername, tvEmail, tvStatus, tvGender, tvBirthday, tvPhone, tvEditProfile;
    private EditText etUsername, etEmail, etStatus, etGender, etBirthday, etPhone;
    private ImageView imgEditProfileIcon;
    private RelativeLayout rltEditProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);
        initViews();
    }

    private void initViews() {
        //Text view
        tvPersonalInformation = findViewById(R.id.tvPersonalInformation);
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail = findViewById(R.id.tvEmail);
        tvStatus = findViewById(R.id.tvStatus);
        tvGender = findViewById(R.id.tvGender);
        tvBirthday = findViewById(R.id.tvBirthday);
        tvPhone = findViewById(R.id.tvPhone);
        tvEditProfile = findViewById(R.id.tvEditProfile);

        //Edit text
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etStatus = findViewById(R.id.etStatus);
        etGender = findViewById(R.id.etGender);
        etBirthday = findViewById(R.id.etBirthday);
        etPhone = findViewById(R.id.etPhone);

        //Image
        imgEditProfileIcon = findViewById(R.id.imgEditProfileIcon);

        //Relative layout (clickable)
        rltEditProfile = findViewById(R.id.rltEditProfile);

        // Set an OnClickListener for the edit profile button
        rltEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}