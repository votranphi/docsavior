package com.example.docsavior;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;;import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OTPVerificationActivity extends AppCompatActivity {

    private EditText edOtp1, edOtp2, edOtp3, edOtp4;
    private Button btnVerify;
    private TextView tvResendOTP;
    private ArrayList<String> userInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);

        findViewByIds();

        setOnClickListeners();

        addTextChangedListeners();

        initVariables();
    }

    private void findViewByIds()
    {
        edOtp1 = (EditText) findViewById(R.id.edOtp1);
        edOtp2 = (EditText) findViewById(R.id.edOtp2);
        edOtp3 = (EditText) findViewById(R.id.edOtp3);
        edOtp4 = (EditText) findViewById(R.id.edOtp4);
        btnVerify = (Button) findViewById(R.id.btnVerify);
        tvResendOTP = findViewById(R.id.tvResendOTP);
    }

    private void setOnClickListeners() {
        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edOtp1.getText().toString().isEmpty() || edOtp2.getText().toString().isEmpty() || edOtp3.getText().toString().isEmpty() || edOtp4.getText().toString().isEmpty()) {
                    Toast.makeText(OTPVerificationActivity.this, "Please enter 4 otp digits!", Toast.LENGTH_SHORT);
                    return;
                }

                String otp = edOtp1.getText().toString() + edOtp2.getText().toString() + edOtp3.getText().toString() + edOtp4.getText().toString();

                postCheckOTP(userInfo.get(0), otp);
            }
        });
    }

    private void initVariables() {
        // retrieve the userInfo from SignUpActivity
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            userInfo = null;
        } else {
            userInfo = extras.getStringArrayList(SignUpUserInfoActivity.KEY_TO_OTP_VERIFICATION_ACITVITY);
        }
    }

    private void postCheckOTP(String username, String otp) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Detail> call = apiService.postCheckOTP(username, otp);

        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                try {
                    if (response.isSuccessful()) {
                        boolean gender = userInfo.get(6).equals("true");
                        postSignUpInfo(userInfo.get(0), userInfo.get(1), userInfo.get(2), userInfo.get(3), userInfo.get(4), userInfo.get(5), gender);

                        Toast.makeText(OTPVerificationActivity.this, response.body().getDetail(), Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        Toast.makeText(OTPVerificationActivity.this, jsonObject.get("detail").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR106: ", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(OTPVerificationActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void postSignUpInfo(String username, String email, String phoneNumber, String password, String fullName, String birthDay, boolean gender) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Detail> call = apiService.postSignUpInfo(username, email, phoneNumber, password, fullName, birthDay, gender);

        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                try {
                    if (response.isSuccessful()) {
                        Intent myIntent = new Intent(OTPVerificationActivity.this, MainActivity.class);
                        Toast.makeText(OTPVerificationActivity.this, response.body().getDetail(), Toast.LENGTH_SHORT).show();
                        OTPVerificationActivity.this.startActivity(myIntent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        Toast.makeText(OTPVerificationActivity.this, response.body().getDetail(), Toast.LENGTH_SHORT).show();
                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        Toast.makeText(OTPVerificationActivity.this, jsonObject.get("detail").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR101: ", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(OTPVerificationActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addTextChangedListeners() {
        edOtp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1) {
                    edOtp2.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        edOtp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1) {
                    edOtp3.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        edOtp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1) {
                    edOtp4.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        edOtp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() == 1) {
                    hideKeyboard(edOtp4);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}