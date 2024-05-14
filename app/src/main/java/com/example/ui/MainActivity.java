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
        setContentView(R.layout.post_detail);

        ListView lv = (ListView) findViewById(R.id.lvComment);
        ArrayList<PostDetail> names = new ArrayList<PostDetail>();
        names.add(new PostDetail("MinhPhuc", "Tai lieu hay vc", "www.uit.edu.vn"));
        names.add(new PostDetail("MinhPhuc1", "?????", "www.uit.edu.vn"));
        names.add(new PostDetail("MinhPhuc2", "tai lieu nhu qq", "www.uit.edu.vn"));
        names.add(new PostDetail("MinhPhuc3", "xin ban dung dang tai lieu nua", "www.uit.edu.vn"));
        names.add(new PostDetail("MinhPhuc4", ":')", "www.uit.edu.vn"));
        names.add(new PostDetail("MinhPhuc5", "thoi chiu roi", "www.uit.edu.vn"));
        names.add(new PostDetail("MinhPhuc6", "Thiet nghi ban nen giu tai lieu nay cho rieng minh ban thoi", "www.uit.edu.vn"));
        names.add(new PostDetail("MinhPhuc7", "nice", "www.uit.edu.vn"));
        names.add(new PostDetail("MinhPhuc8", "Hay qua ban oi", "www.uit.edu.vn"));
        names.add(new PostDetail("MinhPhuc9", "cmj v", "www.uit.edu.vn"));
        names.add(new PostDetail("MinhPhuc10", "wtf", "www.uit.edu.vn"));



        PostDetailAdapter adapter = new PostDetailAdapter(this, R.layout.post_detail_item, names);
        lv.setAdapter(adapter);


    }
}