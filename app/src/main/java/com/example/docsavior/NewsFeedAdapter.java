package com.example.docsavior;

import static com.example.docsavior.ApplicationInfo.username;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsFeedAdapter extends ArrayAdapter<NewsFeed> {
    private final Activity context;
    private List<NewsFeed> newsFeedList = new ArrayList<>();
    private List<TextView> usernames = new ArrayList<>();
    private List<TextView> postDescriptions = new ArrayList<>();
    private List<TextView> postContents = new ArrayList<>(); // Phuc has added this
    private List<TextView> documentNames = new ArrayList<>();
    private List<ImageButton> btnLikes = new ArrayList<>();
    private List<ImageButton> btnDislikes = new ArrayList<>();
    private List<ImageButton> btnComments = new ArrayList<>();
    private List<ImageButton> btnSaves = new ArrayList<>();
    private List<TextView> likeNumbers = new ArrayList<>();
    private List<TextView> dislikeNumbers = new ArrayList<>();
    private List<TextView> commentNumbers = new ArrayList<>();

    private List<Boolean> isLiked = new ArrayList<>();
    private List<Boolean> isDisliked = new ArrayList<>();
    private List<ImageView> profileImgs = new ArrayList<>();
    private List<ImageView> imgPosts = new ArrayList<>();
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
        findViewByIds(convertView, position);

        // setOnClickListeners
        setOnClickListeners(position);

        checkInteract(position);
        usernames.get(position).setText(nf.getUsername());
        postDescriptions.get(position).setText(nf.getPostDescription());
        postContents.get(position).setText(nf.getPostContent());
        documentNames.get(position).setText(nf.getFileName() + "." + nf.getFileExtension());
        likeNumbers.get(position).setText(String.valueOf(nf.getLikeNumber()));
        dislikeNumbers.get(position).setText(String.valueOf(nf.getDislikeNumber()));
        commentNumbers.get(position).setText(String.valueOf(nf.getCommentNumber()));

        // set user's avatar
        getUserInfoAndSetAvatar(profileImgs.get(position), nf.getUsername());

        // set the post's image if the file is image type (jpg, png, jpeg,...)
        if (nf.getFileExtension().equals("jpg") || nf.getFileExtension().equals("png") || nf.getFileExtension().equals("jpeg")) {
            setImage(imgPosts.get(position), nf.getFileData());
        } else {
            imgPosts.get(position).setVisibility(View.GONE);
        }

        return convertView;
    }

    private void findViewByIds(View convertView, int position) {
        usernames.add(convertView.findViewById(R.id.tvUsername));
        postDescriptions.add(convertView.findViewById(R.id.tvPostDesciption));
        postContents.add(convertView.findViewById(R.id.tvPostContent));
        documentNames.add(convertView.findViewById(R.id.tvDocumentName));
        btnLikes.add(convertView.findViewById(R.id.btnLike));
        btnDislikes.add(convertView.findViewById(R.id.btnDislike));
        btnComments.add(convertView.findViewById(R.id.btnComment));
        btnSaves.add(convertView.findViewById(R.id.btnSave));
        likeNumbers.add(convertView.findViewById(R.id.tvLikeNumber));
        dislikeNumbers.add(convertView.findViewById(R.id.tvDislikeNumber));
        commentNumbers.add(convertView.findViewById(R.id.tvCommentNumber));
        isLiked.add(false);
        isDisliked.add(false);
        profileImgs.add(convertView.findViewById(R.id.profileImg));
        imgPosts.add(convertView.findViewById(R.id.imgPost));
    }

    private void checkInteract(int position)
    {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApplicationInfo.apiPath).addConverterFactory(GsonConverterFactory.create()).build();
        ApiService apiService = retrofit.create(ApiService.class);
        Call<Detail> call = apiService.getInteract(username, getItem(position).getId());
        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                try {
                    if (response.isSuccessful())
                    {
                        String res = response.body().getDetail();
                        if(res.equals("like"))
                        {
                            btnLikes.get(position).setImageResource(R.drawable.like_icon);
                            isLiked.set(position, true);
                            // add animation change from like to unlike
                        } else if (res.equals("dislike"))
                        {
                            btnDislikes.get(position).setImageResource(R.drawable.dislike_icon);
                            isDisliked.set(position, true);
                            // add animation change from dislike to undislike
                        }
                    }
                    else {
                        Toast.makeText(context, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                }
                catch (Throwable t)
                {
                    Log.e("ERROR100: ", t.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setOnClickListeners(int position) {
        documentNames.get(position).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    // get the post first
                    NewsFeed post = newsFeedList.get(position);

                    // create jsonArray to store fileData
                    JSONArray jsonArray = new JSONArray(post.getFileData());
                    // convert jsonArray to byteArray
                    byte[] byteArray = new byte[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        int temp = (int)jsonArray.get(i);
                        byteArray[i] = (byte)temp;
                    }

                    // save file to Download
                    writeFileToDownloads(byteArray, post.getFileName(), post.getFileExtension());
                } catch (Exception ex) {
                    Log.e("ERROR987: ", ex.getMessage());
                }
            }
        });

        profileImgs.get(position).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start ProfileActivity then display user's info
                Intent myIntent = new Intent(context, ProfileActivity.class);
                myIntent.putExtra(ApplicationInfo.KEY_TO_PROFILE_ACTIVITY, usernames.get(position).getText().toString());
                context.startActivity(myIntent);
            }
        });

        btnLikes.get(position).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Retrofit retrofit = new Retrofit.Builder().baseUrl(ApplicationInfo.apiPath).addConverterFactory(GsonConverterFactory.create()).build();
                ApiService apiService = retrofit.create(ApiService.class);
                if(isLiked.get(position)) // if already liked
                {
                    isLiked.set(position, false);
                    btnLikes.get(position).setImageResource(R.drawable.like_icon_white);
                    Call<Detail> callNewsfeed = apiService.postUnlike(getItem(position).getId());
                    callNewsfeed.enqueue(new Callback<Detail>() {
                        @Override
                        public void onResponse(Call<Detail> call, Response<Detail> response) {
                            try {
                                if (response.isSuccessful())
                                {
                                    //Toast.makeText(context, "Unlike successfully!", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Toast.makeText(context, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                                }
                            }
                            catch (Throwable t)
                            {
                                Log.e("ERROR100: ", t.getMessage());
                            }
                        }

                        @Override
                        public void onFailure(Call<Detail> call, Throwable t) {
                            Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    if(isDisliked.get(position))
                    {
                        Toast.makeText(context, "Cannot like and dislike at a same time", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    btnLikes.get(position).setImageResource(R.drawable.like_icon);
                    isLiked.set(position, true);
                    Call<Detail> callNewsfeed = apiService.postLike(getItem(position).getId());
                    callNewsfeed.enqueue(new Callback<Detail>() {
                        @Override
                        public void onResponse(Call<Detail> call, Response<Detail> response) {
                            try {
                                if (response.isSuccessful())
                                {
                                    //Toast.makeText(context, "Like successfully!", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Toast.makeText(context, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                                }
                            }
                            catch (Throwable t)
                            {
                                Log.e("ERROR100: ", t.getMessage());
                            }
                        }

                        @Override
                        public void onFailure(Call<Detail> call, Throwable t) {
                            Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

                Call<Detail> callUserInteract = apiService.postInteract(username,getItem(position).getId(), true);
                callUserInteract.enqueue(new Callback<Detail>() {
                    @Override
                    public void onResponse(Call<Detail> call, Response<Detail> response) {
                        try {
                            if (response.isSuccessful())
                            {
                                //Toast.makeText(context, "user_interact_like successfully!", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(context, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                            }
                        }
                        catch (Throwable t)
                        {
                            Log.e("ERROR100: ", t.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<Detail> call, Throwable t) {
                        Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        btnDislikes.get(position).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Retrofit retrofit = new Retrofit.Builder().baseUrl(ApplicationInfo.apiPath).addConverterFactory(GsonConverterFactory.create()).build();

                ApiService apiService = retrofit.create(ApiService.class);

                if(isDisliked.get(position))
                {
                    isDisliked.set(position, false);
                    btnDislikes.get(position).setImageResource(R.drawable.dislike_icon_white);
                    Call<Detail> callNewsfeed = apiService.postUndislike(getItem(position).getId());
                    callNewsfeed.enqueue(new Callback<Detail>() {
                        @Override
                        public void onResponse(Call<Detail> call, Response<Detail> response) {
                            try {
                                if (response.isSuccessful())
                                {
                                    //Toast.makeText(context, "Undislike successfully!", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Toast.makeText(context, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                                }
                            }
                            catch (Throwable t)
                            {
                                Log.e("ERROR100: ", t.getMessage());
                            }
                        }
                        @Override
                        public void onFailure(Call<Detail> call, Throwable t) {
                            Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else
                {
                    if (isLiked.get(position))
                    {
                        Toast.makeText(context, "Cannot like and dislike at a same time", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    isDisliked.set(position, true);
                    btnDislikes.get(position).setImageResource(R.drawable.dislike_icon);
                    Call<Detail> callNewsfeed = apiService.postDislike(getItem(position).getId());
                    callNewsfeed.enqueue(new Callback<Detail>() {
                        @Override
                        public void onResponse(Call<Detail> call, Response<Detail> response) {
                            try {
                                if (response.isSuccessful())
                                {
                                    //Toast.makeText(context, "Disike successfully!", Toast.LENGTH_LONG).show();
                                }
                                else {
                                    Toast.makeText(context, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                                }
                            }
                            catch (Throwable t)
                            {
                                Log.e("ERROR100: ", t.getMessage());
                            }
                        }

                        @Override
                        public void onFailure(Call<Detail> call, Throwable t) {
                            Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
                Call<Detail> callUserInteract = apiService.postInteract(username ,getItem(position).getId(), false);
                callUserInteract.enqueue(new Callback<Detail>() {
                    @Override
                    public void onResponse(Call<Detail> call, Response<Detail> response) {
                        try {
                            if (response.isSuccessful())
                            {
                                //Toast.makeText(context, "user_interact_dislike successfully!", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Toast.makeText(context, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                            }
                        }
                        catch (Throwable t)
                        {
                            Log.e("ERROR100: ", t.getMessage());
                        }
                    }

                    @Override
                    public void onFailure(Call<Detail> call, Throwable t) {
                        Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
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

    private void getUserInteract()
    {
        //
    }
    private void writeFileToDownloads(byte[] array, String fileName, String fileExtension)
    {
        try
        {
            // get the path to Downloads folder plus file's name
            String pathToDownloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
            String pathToFile = pathToDownloads + "/" + fileName + "." + fileExtension;
            File file = new File(pathToFile);

            // create the file if it is not existed
            String fileFullName = fileName + "." + fileExtension;
            if (!file.exists()) {
                file.createNewFile();
            } else {
                int i = 1;
                // Loop until the file is not exist
                do {
                    fileFullName = fileName + " (" + i + ")." + fileExtension;
                    pathToFile = pathToDownloads + "/" + fileName + " (" + i + ")." + fileExtension;
                    file = new File(pathToFile);
                    i++;
                } while (file.exists());
            }

            // write the file from byte array
            FileOutputStream stream = new FileOutputStream(pathToFile);
            stream.write(array);
            stream.close();

            // notify user that the file is successfully downloaded
            Toast.makeText(context, fileFullName + " successfully downloaded to Downloads!", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            Log.e("ERROR1: ", ex.getMessage());
        }
    }

    private void getUserInfoAndSetAvatar(ImageView imageView, String username) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<User> call = apiService.getUserInfo(username);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                try {
                    if (response.isSuccessful()) {
                        // after getting the user's info, set the avatar
                        setImage(imageView, response.body().getAvatarData());
                    } else {
                        Toast.makeText(context, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR106: ", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setImage(ImageView imageView, String avatarData) {
        try {
            if (!avatarData.isEmpty()) {
                // create jsonArray to store avatarData
                JSONArray jsonArray = new JSONArray(avatarData);

                // convert jsonArray to byteArray
                byte[] byteArray = new byte[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    int temp = (int)jsonArray.get(i);
                    byteArray[i] = (byte)temp;
                }

                // convert byteArray to bitmap
                Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                // set the avatar
                imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, imageView.getWidth(), imageView.getHeight(), false));
            }
        } catch (Exception ex) {
            Log.e("ERROR111: ", ex.getMessage());
        }
    }
}
