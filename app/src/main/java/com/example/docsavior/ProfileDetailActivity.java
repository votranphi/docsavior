package com.example.docsavior;

import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ProfileDetailActivity extends AppCompatActivity {

    private EditText etUsername, etEmail, etBirthday, etPhone, etFullname;
    private RelativeLayout rltEditProfile;
    private ImageButton btnClose;
    private Spinner spGender;
    private ArrayList<String> userInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_detail);

        findViewByIds();

        initVariables();

        setOnClickListeners();

        // check if this is my profile's view
        if (ApplicationInfo.username.equals(userInfo.get(0))) {
            // if it's mine
        } else {
            // if it isn't mine
            rltEditProfile.setVisibility(View.GONE);
            etUsername.setFocusable(false);
            etUsername.setClickable(false);
            etFullname.setFocusable(false);
            etFullname.setClickable(false);
            etEmail.setFocusable(false);
            etEmail.setClickable(false);
            spGender.setClickable(false);
            spGender.setFocusable(false);
            etBirthday.setFocusable(false);
            etBirthday.setClickable(false);
            etPhone.setFocusable(false);
            etPhone.setClickable(false);
        }
    }

    private void findViewByIds() {
        //Edit text
        etUsername = findViewById(R.id.etUsername);
        etFullname = findViewById(R.id.etFullname);
        etEmail = findViewById(R.id.etEmail);
        etBirthday = findViewById(R.id.etBirthday);
        etPhone = findViewById(R.id.etPhone);
        spGender = findViewById(R.id.spGender);
        //Button
        btnClose = findViewById(R.id.btnClose);

        //Relative layout (clickable)
        rltEditProfile = findViewById(R.id.rltEditProfile);
    }

    private void setOnClickListeners() {
        // Set an OnClickListener for the edit profile button
        rltEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: call API to update user's infomation
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
        // retrieve the userInfo from ProfileActivity
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            userInfo = extras.getStringArrayList(ProfileActivity.KEY_TO_PROFILE_DETAIL_ACTIVITY);

            // set the EditTexts' text
            etUsername.setText(userInfo.get(0));
            etFullname.setText(userInfo.get(1));
            etEmail.setText(userInfo.get(2));
            if(userInfo.get(4).equals("Male"))
            {
                String[] items = new String[]{"Male", "Female"};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
                spGender.setAdapter(adapter);
            }
            else
            {
                String[] items = new String[]{"Female", "Male"};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
                spGender.setAdapter(adapter);
            }
            etBirthday.setText(userInfo.get(5));
            etPhone.setText(userInfo.get(6));
        }
    }
}