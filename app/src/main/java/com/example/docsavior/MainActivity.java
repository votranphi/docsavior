package com.example.docsavior;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

/*    private EditText edMessageBody;
    private Button btnSend;

    private RecyclerView rcvMessage;

    private MyMessageAdapter myMessageAdapter;
    private List<MyMessage> mListMessage;*/


    @SuppressLint("WrongViewCast")
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
                .baseUrl("http://192.168.3.131:8080/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService retrofitAPI = retrofit.create(ApiService.class);

        Call<Detail> call = retrofitAPI.postLoginInfo(username, password);

        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                if (response.isSuccessful()) {
                    Intent myIntent = new Intent(MainActivity.this, NewFeedActivity.class);
                    Toast.makeText(MainActivity.this, response.body().getDetail(), Toast.LENGTH_SHORT).show();
                    MainActivity.this.startActivity(myIntent);
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
     This is main activity for Chat Screen

     edMessageBody = findViewById(R.id.edMessageBody);
        btnSend = findViewById(R.id.btnSendMessage);
        rcvMessage = findViewById(R.id.rcvMessage);

        mListMessage = new ArrayList<>();
        myMessageAdapter = new MyMessageAdapter();
        myMessageAdapter.setData(mListMessage);

        rcvMessage.setAdapter(myMessageAdapter);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });



    }

    @SuppressLint("NotifyDataSetChanged")
    private void sendMessage() {
        String strMessage = edMessageBody.getText().toString().trim();
        if (TextUtils.isEmpty(strMessage)) {
            return;
        }

        mListMessage.add(new MyMessage(strMessage));
        myMessageAdapter.notifyDataSetChanged();
        rcvMessage.scrollToPosition(mListMessage.size()-1);
        edMessageBody.setText("");

    }*/
}