package com.example.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lookup_post_user);

        ListView lv = (ListView) findViewById(R.id.lvLookupHistory);
        ArrayList<LookupHistory> names = new ArrayList<LookupHistory>();
         names.add(new LookupHistory("tai lieu xac chet thong ke"));
         names.add(new LookupHistory("bi kiep tu tien"));

         LookupHistoryAdapter adapter = new LookupHistoryAdapter(this, R.layout.lookup_post_user_item, names);
         lv.setAdapter(adapter);


    }
}