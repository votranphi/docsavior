package com.example.ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

public class PostDetailAdapter extends ArrayAdapter<PostDetail> {
    private final Activity context;
    public PostDetailAdapter(Activity context, int layoutID, List<PostDetail> objects) {
        super(context, layoutID, objects);
        this.context = context;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.post_detail_item, null, false);
        }
        // Get item
        PostDetail pd = getItem(position);
        // Get view
        TextView username = (TextView) convertView.findViewById(R.id.tvUsername);
        TextView comment = (TextView) convertView.findViewById(R.id.tvComment);


        username.setText(pd.getUsername());
        comment.setText(pd.getComment());


        return convertView;
    }
}
