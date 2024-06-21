package com.example.docsavior;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LookUpPostUserActivity extends AppCompatActivity {

    private ImageButton btnClose;
    private EditText edLookup;
    private ImageButton btnFind;
    private ListView lvLookupHistory;
    private TextView tvNothing;
    private LookupHistoryAdapter lookupHistoryAdapter;
    private ArrayList<LookupHistory> lookupHistoryArrayList;

    private int lookupType = 0; // 0 is lookup in newsfeed, 1 is look up in chat, 2 is lookup in friend

    public static String KEY_TO_LOOK_UP_RESULT_ACTIVITY = "lookUpContent_lookUpType";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_up_post_user);

        findViewByIds();

        initVariables();

        setOnClickListeners();

        loadLookUpHistory();
    }

    private void findViewByIds() {
        btnClose = findViewById(R.id.btnClose);
        edLookup = findViewById(R.id.edLookup);
        btnFind = findViewById(R.id.btnFind);
        lvLookupHistory = findViewById(R.id.lvLookupHistory);
        tvNothing = findViewById(R.id.tvNothing);
    }

    private void setOnClickListeners() {
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edLookup.getText().toString().isEmpty()) {
                    Toast.makeText(LookUpPostUserActivity.this, "Please enter lookup content!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // call API to post to lookup history
                postLookUpHistory(edLookup.getText().toString(), lookupType);

                // start LookUpResultActivity then call the look up API
                Intent myIntent = new Intent(LookUpPostUserActivity.this, LookUpResultActivity.class);
                // prepare the string to put to LookUpResultActivity
                ArrayList<String> arrayToPut = new ArrayList<>();
                arrayToPut.add(edLookup.getText().toString());
                arrayToPut.add(String.valueOf(lookupType));
                // put it in the intent
                myIntent.putStringArrayListExtra(KEY_TO_LOOK_UP_RESULT_ACTIVITY, arrayToPut);
                // start activity
                startActivity(myIntent);
            }
        });

        lvLookupHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // start LookUpResultActivity then call the look up API
                Intent myIntent = new Intent(LookUpPostUserActivity.this, LookUpResultActivity.class);
                // prepare the string to put to LookUpResultActivity
                ArrayList<String> arrayToPut = new ArrayList<>();
                arrayToPut.add(lookupHistoryArrayList.get(position).getLookupContent());
                arrayToPut.add(String.valueOf(lookupType));
                // put it in the intent
                myIntent.putStringArrayListExtra(KEY_TO_LOOK_UP_RESULT_ACTIVITY, arrayToPut);
                // start activity
                startActivity(myIntent);
            }
        });
    }

    private void initVariables() {
        lookupHistoryArrayList = new ArrayList<>();
        lookupHistoryAdapter = new LookupHistoryAdapter(this, R.layout.item_lookup_post_user, lookupHistoryArrayList);
        lvLookupHistory.setAdapter(lookupHistoryAdapter);

        // retrieve the lookupType from Fragments
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            lookupType = extras.getInt(ApplicationInfo.KEY_TO_LOOK_UP_POST_USER_ACTIVITY);
        }
    }

    private void postLookUpHistory(String lookUpInfo, int lookUpType) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Detail> call = apiService.postLookUpHistory(ApplicationInfo.username, lookUpInfo, lookUpType);

        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                try {
                    if (!response.isSuccessful()) {
                        Toast.makeText(LookUpPostUserActivity.this, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR1: ", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(LookUpPostUserActivity.this, "FAILURE: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void loadLookUpHistory() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<LookUpInfos> call = null;
        if (lookupType == 0) {
            call = apiService.getPostLookUpHistory(ApplicationInfo.username);
        } else if (lookupType == 1) {
            // TODO: look up for my own conversation
        } else {
            call = apiService.getFriendLookUpHistory(ApplicationInfo.username);
        }

        call.enqueue(new Callback<LookUpInfos>() {
            @Override
            public void onResponse(Call<LookUpInfos> call, Response<LookUpInfos> response) {
                try {
                    if (response.isSuccessful()) {
                        LookUpInfos lookUpInfos = response.body();
                        if (lookUpInfos.getLookUpInfos().length == 0) {
                            tvNothing.setVisibility(View.VISIBLE);
                        } else {
                            tvNothing.setVisibility(View.GONE);

                            assignLookUpInfosToListView(lookUpInfos);
                        }
                    } else {
                        Toast.makeText(LookUpPostUserActivity.this, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR1: ", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<LookUpInfos> call, Throwable t) {
                Toast.makeText(LookUpPostUserActivity.this, "FAILURE: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void assignLookUpInfosToListView(LookUpInfos lookUpInfos) {
        try {
            for (int i = 0; i < lookUpInfos.getLookUpInfos().length; i++) {
                LookupHistory lookupHistory = new LookupHistory(lookUpInfos.getLookUpInfos()[i]);
                lookupHistoryArrayList.add(lookupHistory);
                lookupHistoryAdapter.notifyDataSetChanged();
            }
        } catch (Exception ex) {
            Log.e("ERROR2: ", ex.getMessage());
        }
    }
}