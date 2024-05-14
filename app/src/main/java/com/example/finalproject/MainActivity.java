package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.http.HttpEngine;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

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

                    }
                }
        );
    }
}