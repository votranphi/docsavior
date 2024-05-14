package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    EditText edUsername;
    EditText edPassword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // get components
        edUsername = findViewById(R.id.edUsername);
        edPassword = findViewById(R.id.edPassword);

        // start new activity if user press tvRecoveryPassword
        TextView tvRecoveryPassword = findViewById(R.id.tvRecoveryPassword);
        tvRecoveryPassword.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myIntent = new Intent(MainActivity.this, RecoveryPasswordActivity.class);
                        MainActivity.this.startActivity(myIntent);
                    }
                }
        );

        // start new activity if user press tvCreateAccount
        TextView tvCreateAccount = findViewById(R.id.tvCreateAccount);
        tvCreateAccount.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myIntent = new Intent(MainActivity.this, SignUpActivity.class);
                        MainActivity.this.startActivity(myIntent);
                    }
                }
        );

        // things to do if user pressed login button
        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (edUsername.getText().toString().isEmpty() || edPassword.getText().toString().isEmpty()) {
                            Toast.makeText(MainActivity.this, "Please enter username and password!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        postLoginInfo(edUsername.getText().toString(), edPassword.getText().toString());
                    }
                }
        );
    }

    private void postLoginInfo(String username, String password) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.117.86:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService retrofitAPI = retrofit.create(ApiService.class);

        Call<Detail> call = retrofitAPI.postLoginInfo(username, password);

        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                if (response.isSuccessful()) {
                    Intent myIntent = new Intent(MainActivity.this, NewFeedActivity.class);
                    Toast.makeText(MainActivity.this, "Login successfully!", Toast.LENGTH_SHORT).show();
                    MainActivity.this.startActivity(myIntent);
                } else {
                    // the code line below will bug if response.body().getDetail() is called
                    Toast.makeText(MainActivity.this, response.body().getDetail(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}