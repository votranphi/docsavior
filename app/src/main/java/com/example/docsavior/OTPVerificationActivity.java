package com.example.docsavior;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;;

public class OTPVerificationActivity extends AppCompatActivity {

    private EditText edOtp1, edOtp2, edOtp3, edOtp4;
    private Button btnVerify;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        findViewByIds();

        setupOTPInputs();
    }

    private void findViewByIds()
    {
        edOtp1 = (EditText) findViewById(R.id.edOtp1);
        edOtp2 = (EditText) findViewById(R.id.edOtp2);
        edOtp3 = (EditText) findViewById(R.id.edOtp3);
        edOtp4 = (EditText) findViewById(R.id.edOtp4);
        btnVerify = (Button) findViewById(R.id.btnVerify);
    }

    private void setupOTPInputs() {
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

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verify OTP
            }
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

}