package com.example.finalproject;

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
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.notification_item, null, false);
        }
        // Get item
        Notification noti = getItem(position);
        // Get view
        ImageView profileImg = (ImageView) convertView.findViewById(R.id.profileImg);
        TextView notiTitle = (TextView) convertView.findViewById(R.id.notiTitle);
        TextView notiDes = (TextView) convertView.findViewById(R.id.notiDes);

        // image
        notiTitle.setText(noti.getNotiTitle());
        notiDes.setText(noti.getNotiDes());

        return convertView;
    }
}
