package com.example.docsavior;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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

                // male is 1 and female is 0
                boolean gender = cbMale.isChecked();

                postSignUpInfo(userInfo.get(0), userInfo.get(1), userInfo.get(2), userInfo.get(3), edAccountName.getText().toString(), edDateOfBirth.getText().toString(), gender);
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
                if (response.isSuccessful()) {
                    Intent myIntent = new Intent(SignUpUserInfoActivity.this, MainActivity.class);
                    Toast.makeText(SignUpUserInfoActivity.this, response.body().getDetail(), Toast.LENGTH_SHORT).show();
                    SignUpUserInfoActivity.this.startActivity(myIntent);
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        Toast.makeText(SignUpUserInfoActivity.this, jsonObject.get("detail").toString(), Toast.LENGTH_SHORT).show();
                    } catch (Exception ex) {

                    }
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(SignUpUserInfoActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}