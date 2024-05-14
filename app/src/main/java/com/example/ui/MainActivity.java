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
        setContentView(R.layout.new_feed);

        ListView lv = (ListView) findViewById(R.id.lvPost);
        ArrayList<Newfeed> names = new ArrayList<Newfeed>();
         names.add(new Newfeed("MinhPhuc", "Tuyen tap tu tien", "www.uit.edu.vn", 12,1,15));
        names.add(new Newfeed("MinhPhuc", "Tuyen tap tu tien", "www.uit.edu.vn", 2,19,1231));
        names.add(new Newfeed("MinhPhuc", "Tuyen tap tu tien", "www.uit.edu.vn", 0,500,0));

         NewfeedAdapter adapter = new NewfeedAdapter(this, R.layout.lookup_post_user_item, names);
         lv.setAdapter(adapter);


    }
}