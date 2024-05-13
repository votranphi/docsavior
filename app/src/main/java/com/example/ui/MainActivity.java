package com.example.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import com.example.finalproject.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);


        ListView lvPost = (ListView) findViewById(R.id.lvPost);
        ArrayList<Notification> names = new ArrayList<Notification>();

        names.add(new Notification("xzczxc", "121212", "2323"));
        names.add(new Notification("xzczxc", "121212", "2323"));
        names.add(new Notification("xzczxc", "121212", "2323"));
        names.add(new Notification("xzczxc", "121212", "2323"));
        names.add(new Notification("xzczxc", "121212", "2323"));
        names.add(new Notification("xzczxc", "121212", "2323"));
        names.add(new Notification("xzczxc", "121212", "2323"));

        NotificationAdapter adapter = new NotificationAdapter(this, R.layout.notification_item, names);
        lvPost.setAdapter(adapter);
    }
}