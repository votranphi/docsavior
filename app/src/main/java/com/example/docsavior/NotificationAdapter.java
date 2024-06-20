package com.example.docsavior;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class NotificationAdapter extends ArrayAdapter<Notification> {
    private final Activity context;
    public NotificationAdapter(Activity context, int layoutID, List<Notification> objects) {
        super(context, layoutID, objects);
        this.context = context;
    }
    @SuppressLint("SetTextI18n")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_notification, null, false);
        }
        // Get item
        Notification noti = getItem(position);
        // Get view
        ImageView profileImg = (ImageView) convertView.findViewById(R.id.profileImg);
        TextView tvContent = (TextView) convertView.findViewById(R.id.tvContent);

        if (noti.getType().equals(0))
        {
            tvContent.setText(noti.getUsernameInteract()  + " has liked your post (id: " + noti.getIdPost() + ")");
        }
        else if(noti.getType().equals(1))
        {
            tvContent.setText(noti.getUsernameInteract()  + " leaved a comment at your post (id: " + noti.getIdPost() +")");
        }
        else if(noti.getType().equals(2))
        {
            tvContent.setText(noti.getUsernameInteract() + " has sent you a friend request");
        }


        return convertView;
    }
}
