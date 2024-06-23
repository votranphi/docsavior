package com.example.docsavior;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    EditText edUsername;
    EditText edPassword;
    TextView tvRecoveryPassword;
    Button btnLogin;
    TextView tvCreateAccount;

    RelativeLayout relay;



    // Set animation for Splash
    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            relay.setVisibility(View.VISIBLE);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        handler.postDelayed(runnable, 3000); // Timeout for the splash

//        Intent intent = new Intent(this, OTPVerificationActivity.class);
//        startActivity(intent);
        findViewByIds();
        
        setOnClickListeners();

        // Log.e("PATH: ", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());


    }
    
    private void findViewByIds() {
        edUsername = findViewById(R.id.edUsername);
        edPassword = findViewById(R.id.edPassword);
        tvRecoveryPassword = findViewById(R.id.tvRecoveryPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvCreateAccount = findViewById(R.id.tvCreateAccount);
        relay = (RelativeLayout) findViewById(R.id.relay);
    }
    
    private void setOnClickListeners() {
        // start new activity if user press tvRecoveryPassword
        tvRecoveryPassword.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myIntent = new Intent(MainActivity.this, RecoveryPasswordActivity.class);
                        MainActivity.this.startActivity(myIntent);
                        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                    }
                }
        );

        // start new activity if user press tvCreateAccount
        tvCreateAccount.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myIntent = new Intent(MainActivity.this, SignUpActivity.class);
                        MainActivity.this.startActivity(myIntent);
                        overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
                    }
                }
        );

        // things to do if user pressed login button
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

    // POST username and password via API
    private void postLoginInfo(String username, String password) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Detail> call = apiService.postLoginInfo(username, password);

        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                if (response.isSuccessful()) {
                    // set the username of the user for later usage
                    ApplicationInfo.username = username;

                    // start NewsFeedActivity after logging in successfully
                    Intent myIntent = new Intent(MainActivity.this, NewsFeedActivity.class);
                    myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Toast.makeText(MainActivity.this, response.body().getDetail(), Toast.LENGTH_SHORT).show();
                    MainActivity.this.startActivity(myIntent);
                    overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        Toast.makeText(MainActivity.this, jsonObject.get("detail").toString(), Toast.LENGTH_SHORT).show();
                    } catch (Exception ex) {

                    }
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
//     This is main activity for Chat Screen
//
//     edMessageBody = findViewById(R.id.edMessageBody);
//        btnSend = findViewById(R.id.btnSendMessage);
//        rcvMessage = findViewById(R.id.rcvMessage);
//
//        mListMessage = new ArrayList<>();
//        myMessageAdapter = new MyMessageAdapter();
//        myMessageAdapter.setData(mListMessage);
//
//        rcvMessage.setAdapter(myMessageAdapter);
//        btnSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                sendMessage();
//            }
//        });
//
//
//
//    }
//
//    @SuppressLint("NotifyDataSetChanged")
//    private void sendMessage() {
//        String strMessage = edMessageBody.getText().toString().trim();
//        if (TextUtils.isEmpty(strMessage)) {
//            return;
//        }
//
//        mListMessage.add(new MyMessage(strMessage));
//        myMessageAdapter.notifyDataSetChanged();
//        rcvMessage.scrollToPosition(mListMessage.size()-1);
//        edMessageBody.setText("");
//
//    }*/


        //This is for the datePicker
//        edDateOfBirth = findViewById(R.id.edDateOfBirth);
//        edDateOfBirth.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openDatePickerDialog();
//            }
//        });

//    }
//    private void openDatePickerDialog()
//    {
//
//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//
//        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                edDateOfBirth.setText(String.valueOf(dayOfMonth)+ " - " + String.valueOf(month+1) + " - " + String.valueOf(year) );
//            }
//        }, year, month, day);
//        dialog.show();
//    }
}