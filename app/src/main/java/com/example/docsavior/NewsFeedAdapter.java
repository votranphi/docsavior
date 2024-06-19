package com.example.docsavior;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class NewsFeedAdapter extends ArrayAdapter<NewsFeed> {
    private final Activity context;
    private List<NewsFeed> newsFeedList = new ArrayList<>();
    private List<TextView> usernames = new ArrayList<>();
    private List<TextView> postDescriptions = new ArrayList<>();
    private List<TextView> postContents = new ArrayList<>(); // Phuc will add this in the item later
    private List<TextView> documentNames = new ArrayList<>();
    private List<ImageButton> btnLikes = new ArrayList<>();
    private List<ImageButton> btnDislikes = new ArrayList<>();
    private List<ImageButton> btnComments = new ArrayList<>();
    private List<ImageButton> btnSaves = new ArrayList<>();
    private List<TextView> likeNumbers = new ArrayList<>();
    private List<TextView> dislikeNumbers = new ArrayList<>();
    private List<TextView> commentNumbers = new ArrayList<>();
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

        // add nf to newsFeedList
        newsFeedList.add(nf);

        // findViewByIds
        findViewByIds(convertView);

        // setOnClickListeners
        setOnClickListeners(position);

        usernames.get(position).setText(nf.getUsername());
        postDescriptions.get(position).setText(nf.getPostDescription());
        // postContents.get(position).setText(nf.getPostContent());
        documentNames.get(position).setText(nf.getFileName() + "." + nf.getFileExtension());
        likeNumbers.get(position).setText(String.valueOf(nf.getLikeNumber()));
        dislikeNumbers.get(position).setText(String.valueOf(nf.getDislikeNumber()));
        commentNumbers.get(position).setText(String.valueOf(nf.getCommentNumber()));

        return convertView;
    }

    private void findViewByIds(View convertView) {
        usernames.add(convertView.findViewById(R.id.tvUsername));
        postDescriptions.add(convertView.findViewById(R.id.tvPostDesciption));
        // postContents.add(convertView.findViewById(R.id.tvPostContent));
        documentNames.add(convertView.findViewById(R.id.tvDocumentName));
        btnLikes.add(convertView.findViewById(R.id.btnLike));
        btnDislikes.add(convertView.findViewById(R.id.btnDislike));
        btnComments.add(convertView.findViewById(R.id.btnComment));
        btnSaves.add(convertView.findViewById(R.id.btnSave));
        likeNumbers.add(convertView.findViewById(R.id.tvLikeNumber));
        dislikeNumbers.add(convertView.findViewById(R.id.tvDislikeNumber));
        commentNumbers.add(convertView.findViewById(R.id.tvCommentNumber));
    }

    private void setOnClickListeners(int position) {
        documentNames.get(position).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get the post first
                NewsFeed post = newsFeedList.get(position);

                // convert data from string to byte array
                byte[] byteArray = post.getFileData().getBytes();

                // save file to Download
                writeFileToDownloads(byteArray, post.getFileName() + "." + post.getFileExtension());
            }
        });

        btnLikes.get(position).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: code API to increase like number of this post by 1 then call it in here
            }
        });

        btnDislikes.get(position).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: code API to increase dislike number of this post by 1 then call it in here
            }
        });

        btnComments.get(position).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: open the comment activity then let the user comment, code API to increase comment number of this post by 1 then call it in there if user really comment
            }
        });

        btnSaves.get(position).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: save the post to see later
            }
        });
    }

    private void writeFileToDownloads(byte[] array, String fileFullName)
    {
        try
        {
            // get the path to Downloads folder plus file's name
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + "/" + fileFullName;
            File file = new File(path);
            // create the file if it is not existed
            if (!file.exists()) {
                file.createNewFile();
            }

            // write the file from byte array
            FileOutputStream stream = new FileOutputStream(path);
            stream.write(array);

            // notify user that the file is successfully downloaded
            Toast.makeText(context, "File's successfully downloaded to Downloads!", Toast.LENGTH_SHORT).show();
        } catch (Exception ex)
        {
            Log.e("ERROR1: ", ex.getMessage());
        }
    }
}
