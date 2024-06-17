package com.example.docsavior;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class NewsFeedAdapter extends ArrayAdapter<NewsFeed> {
    private final Activity context;
    public NewsFeedAdapter(Activity context, int layoutID, List<NewsFeed> objects) {
        super(context, layoutID, objects);
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_newsfeed, null, false);
        }
        // Get item
        NewsFeed nf = getItem(position);
        // Get view
        TextView username = (TextView) convertView.findViewById(R.id.tvUsername);
        TextView postDescription = (TextView) convertView.findViewById(R.id.tvPostDesciption);
        TextView postContent = (TextView) convertView.findViewById(R.id.imgPostcontent);
        TextView likeNumber = (TextView) convertView.findViewById(R.id.tvLikeNumber);
        TextView dislikeNumber = (TextView) convertView.findViewById(R.id.tvDislikeNumber);
        TextView commentNumber = (TextView) convertView.findViewById(R.id.tvCommentNumber);

        username.setText(nf.getUsername());
        postDescription.setText(nf.getPostDescription());
        postContent.setText(nf.getPostContent());
        likeNumber.setText(String.valueOf(nf.getLikeNumber()));
        dislikeNumber.setText(String.valueOf(nf.getDislikeNumber()));
        commentNumber.setText(String.valueOf(nf.getCommentNumber()));


        return convertView;
    }
}
