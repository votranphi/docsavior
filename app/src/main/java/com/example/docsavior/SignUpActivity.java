package com.example.docsavior;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUpActivity extends AppCompatActivity {
    private EditText edUsername;
    private EditText edEmail;
    private EditText edPhoneNumber;
    private EditText edPassword;
    private Button btnSignUp;

    private EditText edPasswordConfirm;

    public final static String KEY_TO_SIGN_UP_USER_INFO_ACTIVITY = "username_email_phoneNumber_password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        findViewByIds();

        setOnClickListeners();
    }

    private void findViewByIds() {
        edUsername = findViewById(R.id.edUsername);
        edEmail = findViewById(R.id.edEmail);
        edPhoneNumber = findViewById(R.id.edPhoneNumber);
        edPassword = findViewById(R.id.edPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        edPasswordConfirm = findViewById(R.id.edPasswordConfirm);
    }

    private void setOnClickListeners() {
        btnSignUp.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (edUsername.getText().toString().isEmpty() || edEmail.getText().toString().isEmpty() || edPhoneNumber.getText().toString().isEmpty() || edPassword.getText().toString().isEmpty() || edPasswordConfirm.getText().toString().isEmpty()) {
                            Toast.makeText(SignUpActivity.this, "Please enter full fields above!", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (edUsername.getText().toString().contains(" ")) {
                            Toast.makeText(SignUpActivity.this, "Your username cannot have space!", Toast.LENGTH_LONG).show();
                            return;
                        }

                        if (!edPassword.getText().toString().equals(edPasswordConfirm.getText().toString()))
                        {
                            Toast.makeText(SignUpActivity.this, "Confirm password is incorrect!", Toast.LENGTH_LONG).show();
                            return;
                        }

                        postCheckSignUpInfo(edUsername.getText().toString(), edEmail.getText().toString(), edPhoneNumber.getText().toString());
                    }
                }
        );
    }

    private void postCheckSignUpInfo(String username, String email, String phoneNumber) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Detail> call = apiService.postCheckSignUpInfo(username, email, phoneNumber);

        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                try {
                    if (response.isSuccessful()) {
                        // start SignUpUserInfoActivity when the information is ok

                        // create intent to start SignUpUserInfoActivity
                        Intent myIntent = new Intent(SignUpActivity.this, SignUpUserInfoActivity.class);
                        // create ArrayList of user's info to put to SignUpUserInfoActivity
                        ArrayList<String> userInfo = new ArrayList<>();
                        userInfo.add(edUsername.getText().toString());
                        userInfo.add(edEmail.getText().toString());
                        userInfo.add(edPhoneNumber.getText().toString());
                        userInfo.add(edPassword.getText().toString());
                        // put it in the intent
                        myIntent.putStringArrayListExtra(KEY_TO_SIGN_UP_USER_INFO_ACTIVITY, userInfo);
                        // start activity
                        startActivity(myIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        Toast.makeText(SignUpActivity.this, jsonObject.get("detail").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR101: ", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
