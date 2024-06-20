package com.example.docsavior;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ChangePasswordActivity extends AppCompatActivity {

    private ImageButton btnClose;
    private EditText edOldPassword;
    private EditText edNewPassword;
    private EditText edConfirmNewPassword;
    private Button btnSettingChangePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        findViewByIds();

        setOnClickListeners();
    }

    private void findViewByIds() {
        btnClose = findViewById(R.id.btnClose);
        edOldPassword = findViewById(R.id.edOldPassword);
        edNewPassword = findViewById(R.id.edNewPassword);
        edConfirmNewPassword = findViewById(R.id.edConfirmNewPassword);
        btnSettingChangePassword = findViewById(R.id.btnSettingChangePassword);
    }

    private void setOnClickListeners() {
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSettingChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edOldPassword.getText().toString().isEmpty() || edNewPassword.getText().toString().isEmpty() || edConfirmNewPassword.getText().toString().isEmpty()) {
                    Toast.makeText(ChangePasswordActivity.this, "Please enter full fields above!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!edNewPassword.getText().toString().equals(edConfirmNewPassword.getText().toString())) {
                    Toast.makeText(ChangePasswordActivity.this, "Password confirm is incorrect!", Toast.LENGTH_SHORT).show();
                    return;
                }

                postChangePassword(edOldPassword.getText().toString(), edNewPassword.getText().toString());
            }
        });
    }

    private void postChangePassword(String oldPassword, String newPassword) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Detail> call = apiService.postChangePassword(ApplicationInfo.username, oldPassword, newPassword);

        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                try {
                    if (response.isSuccessful()) {
                        Toast.makeText(ChangePasswordActivity.this, response.body().getDetail(), Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        Toast.makeText(ChangePasswordActivity.this, jsonObject.get("detail").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR101: ", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(ChangePasswordActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}