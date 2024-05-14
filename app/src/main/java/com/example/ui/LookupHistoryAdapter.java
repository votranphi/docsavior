package com.example.ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class LookupHistoryAdapter extends ArrayAdapter<LookupHistory>
{
    private final Activity context;
    public LookupHistoryAdapter(Activity context, int layoutID, List<LookupHistory> objects) {
        super(context, layoutID, objects);
        this.context = context;
}

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.lookup_post_user_item, null, false);
        }
        // Get item
        LookupHistory lkHistory = getItem(position);
        // Get view
        TextView lkContent = (TextView) convertView.findViewById(R.id.tvLookupContent);

        // image
        lkContent.setText(lkHistory.getLookupContent());


        return convertView;
    }


    }
