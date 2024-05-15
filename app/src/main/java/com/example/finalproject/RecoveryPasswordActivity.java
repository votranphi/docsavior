package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RecoveryPasswordActivity extends AppCompatActivity {
    EditText edUsername;
    EditText edEmail;
    EditText edPhoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recovery_password);

        edUsername = findViewById(R.id.edUsername);
        edEmail = findViewById(R.id.edEmail);
        edPhoneNumber = findViewById(R.id.edPhoneNumber);

        Button btnRecoverPassword = findViewById(R.id.btnRecoverPassword);
        btnRecoverPassword.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (edUsername.getText().toString().isEmpty() || edEmail.getText().toString().isEmpty() || edPhoneNumber.getText().toString().isEmpty()) {
                            Toast.makeText(RecoveryPasswordActivity.this, "Please enter full fields above!", Toast.LENGTH_LONG);
                            return;
                        }

                        postRecoverPasswordInfo(edUsername.getText().toString(), edEmail.getText().toString(), edPhoneNumber.getText().toString());
                    }
                }
        );
    }

    private void postRecoverPasswordInfo(String username, String email, String phoneNumber) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.3.131:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService retrofitAPI = retrofit.create(ApiService.class);

        Call<Detail> call = retrofitAPI.postRecoverPasswordInfo(username, email, phoneNumber);

        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                if (response.isSuccessful()) {
                    Intent myIntent = new Intent(RecoveryPasswordActivity.this, MainActivity.class);
                    Toast.makeText(RecoveryPasswordActivity.this, response.body().getDetail(), Toast.LENGTH_SHORT).show();
                    RecoveryPasswordActivity.this.startActivity(myIntent);
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        Toast.makeText(RecoveryPasswordActivity.this, jsonObject.get("detail").toString(), Toast.LENGTH_SHORT).show();
                    } catch (Exception ex) {

                    }
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(RecoveryPasswordActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
