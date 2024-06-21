package com.example.docsavior;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class LookUpPostUserActivity extends AppCompatActivity {

    private ImageButton btnClose;
    private EditText edLookup;
    private ImageButton btnFind;
    private ListView lvLookupHistory;
    private LookupHistoryAdapter lookupHistoryAdapter;
    private ArrayList<LookupHistory> lookupHistoryArrayList;

    private int lookupType = 0; // 0 is lookup in newsfeed, 1 is look up in chat, 2 is lookup in friend

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_look_up_post_user);

        findViewByIds();

        initVariables();

        setOnClickListeners();
    }

    private void findViewByIds() {
        btnClose = findViewById(R.id.btnClose);
        edLookup = findViewById(R.id.edLookup);
        btnFind = findViewById(R.id.btnFind);
        lvLookupHistory = findViewById(R.id.lvLookupHistory);
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
                // TODO: call API to post to lookup history
                // TODO: call API to get the lookup result, then start LookUpResultActivity to display it out
            }
        });
    }

    private void initVariables() {
        lookupHistoryArrayList = new ArrayList<>();
        lookupHistoryAdapter = new LookupHistoryAdapter(this, R.layout.item_lookup_post_user, lookupHistoryArrayList);
        lvLookupHistory.setAdapter(lookupHistoryAdapter);
    }
}