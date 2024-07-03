package com.example.docsavior;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;

public class StringToImageViewAsync extends AsyncTask<String, Long, Void> {
    private Context context;
    private String avatarData;
    private ImageView imageView;
    private boolean isPostImage;

    private byte[] byteArray;

    public StringToImageViewAsync(Context context, String avatarData, ImageView imageView, boolean isPostImage) {
        this.context = context;
        this.avatarData = avatarData;
        this.imageView = imageView;
        this.isPostImage = isPostImage;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            // create jsonArray to store avatarData
            JSONArray jsonArray = new JSONArray(avatarData);

            // convert jsonArray to byteArray
            byteArray = new byte[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                int temp = (int)jsonArray.get(i);
                byteArray[i] = (byte)temp;
            }

            publishProgress();
        } catch (Exception ex) {
            Log.e("ERROR345", ex.getMessage());
        }

        return null;
    }

    @Override
    protected void onProgressUpdate(Long ...values) {
        super.onProgressUpdate();

        // try catch is to prevent application from stopping due to
        // cannot run Glide...load... because of the stopping of an Activity (context)
        // in other words, there is a bug, but it's not important,
        // because the Activity is already destroyed when the bug appears
        try {
            if (isPostImage) {
                Glide.with(context).load(byteArray).placeholder(R.drawable.loading).into(imageView);
            } else {
                Glide.with(context).load(byteArray).placeholder(R.drawable.user_icon_black).into(imageView);
            }
        } catch (Exception ex) {
            // do nothing
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

    }
}
