package com.example.docsavior;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUpUserInfoActivity extends AppCompatActivity {

    public final static String KEY_TO_OTP_VERIFICATION_ACITVITY = "username_email_phoneNumber_password_fullName_birthDay_gender";
    private EditText edAccountName;
    private Button edDateOfBirth;
    private CheckBox cbMale;
    private CheckBox cbFemale;
    private Button btnContinue;
    private ArrayList<String> userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_user_info);

        findViewByIds();

        setOnClickListeners();

        initVariables();
    }

    private void findViewByIds() {
        edAccountName = findViewById(R.id.edAccountName);
        edDateOfBirth = findViewById(R.id.edDateOfBirth);
        cbMale = findViewById(R.id.cbMale);
        cbFemale = findViewById(R.id.cbFemale);
        btnContinue = findViewById(R.id.btnContinue);
    }

    private void setOnClickListeners() {
        //This is for the datePicker
        edDateOfBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDatePickerDialog();
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edAccountName.getText().toString().isEmpty() || edDateOfBirth.getText().toString().equals(getString(R.string.choose_your_date_of_birth)) || (!cbFemale.isChecked() && !cbMale.isChecked())) {
                    Toast.makeText(SignUpUserInfoActivity.this, "Please enter full fields above!", Toast.LENGTH_LONG);
                    return;
                }

                Toast.makeText(SignUpUserInfoActivity.this, "OTP is sending to your email!", Toast.LENGTH_LONG).show();
                postCreateOrRefreshOTP(userInfo.get(0), userInfo.get(1));
            }
        });

        cbFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbMale.setChecked(false);
            }
        });

        cbMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cbFemale.setChecked(false);
            }
        });
    }

    private void initVariables()
    {
        // retrieve the userInfo from SignUpActivity
        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            userInfo = null;
        } else {
            userInfo = extras.getStringArrayList(SignUpActivity.KEY_TO_SIGN_UP_USER_INFO_ACTIVITY);
        }
    }

    private void postCreateOrRefreshOTP(String username, String email) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Detail> call = apiService.postCreateOrRefreshOTP(username, email);

        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                try {
                    if (response.isSuccessful()) {
                        // male is 1 and female is 0
                        boolean gender = cbMale.isChecked();

                        // array list to put to OTPVerificationActivity
                        ArrayList<String> arrayListToPut = new ArrayList<>();

                        // put things to it
                        arrayListToPut.add(userInfo.get(0));
                        arrayListToPut.add(userInfo.get(1));
                        arrayListToPut.add(userInfo.get(2));
                        arrayListToPut.add(userInfo.get(3));
                        arrayListToPut.add(edAccountName.getText().toString());
                        arrayListToPut.add(edDateOfBirth.getText().toString());
                        arrayListToPut.add(String.valueOf(gender));

                        Intent myIntent = new Intent(SignUpUserInfoActivity.this, OTPVerificationActivity.class);
                        myIntent.putStringArrayListExtra(KEY_TO_OTP_VERIFICATION_ACITVITY, arrayListToPut);
                        startActivity(myIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_top);
                    } else {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        Toast.makeText(SignUpUserInfoActivity.this, jsonObject.get("detail").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR106: ", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(SignUpUserInfoActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openDatePickerDialog()
    {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                edDateOfBirth.setText(dayOfMonth + "-" + (month + 1) + "-" + year);
            }
        }, year, month, day);

        dialog.show();
    }
}