package com.example.docsavior;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class FriendAdapter extends ArrayAdapter<Friend> {
    private final Activity context;
    private ImageView profileImg;
    private TextView username;
    private ImageButton btnAccept;
    private ImageButton btnDecline;

    public FriendAdapter(Activity context, int layoutID, List<Friend> objects) {
        super(context, layoutID, objects);
        this.context = context;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_friend, null, false);
        }
        // Get item
        Friend fr = getItem(position);

        // Get view
        findViewByIds(convertView);

        // set click listeners
        setOnClickListeners();

        // image
        username.setText(fr.getUsername());
        return convertView;
    }

    private void findViewByIds(View convertView) {
        profileImg = convertView.findViewById(R.id.profileImg);
        username = convertView.findViewById(R.id.tvUsername);
        btnAccept = convertView.findViewById(R.id.btnAccept);
        btnDecline = convertView.findViewById(R.id.btnDecline);
    }

    private void setOnClickListeners() {
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
