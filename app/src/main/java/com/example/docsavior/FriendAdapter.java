package com.example.docsavior;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FriendAdapter extends ArrayAdapter<Friend> {
    private final Activity context;
    public FriendAdapter(Activity context, int layoutID, List<Friend> objects) {
        super(context, layoutID, objects);
        this.context = context;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.friend_item, null, false);
        }
        // Get item
        Friend fr = getItem(position);
        // Get view
        ImageView profileImg = (ImageView) convertView.findViewById(R.id.profileImg);
        TextView username = (TextView) convertView.findViewById(R.id.tvUsername);

        // image
        username.setText(fr.getUsername());
        return convertView;
    }
}
