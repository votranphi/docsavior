package com.example.docsavior;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ProfileAdapter extends ArrayAdapter<String> {

    private final Activity context;
    public ProfileAdapter(Activity context, int layoutID, List<String> objects) {
        super(context, layoutID, objects);
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.profile_item, null, false);
        }
        // Get item
        String posts = getItem(position);
        // Get view

        TextView tvPostDescription = (TextView) convertView.findViewById(R.id.tvPostDescription);
        tvPostDescription.setText(posts);
        return convertView;
    }

}
