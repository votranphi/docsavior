package com.example.docsavior;

import static com.example.docsavior.ApplicationInfo.username;

import android.app.Activity;
import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsFeedAdapter extends RecyclerView.Adapter<NewsFeedAdapter.ViewHolder> {
    private Context context;
    private List<NewsFeed> newsFeedList;

    private List<Boolean> isLiked = new ArrayList<>();
    private List<Boolean> isDisliked = new ArrayList<>();


    public NewsFeedAdapter(Context context, List<NewsFeed> newsFeedList) {
        this.context = context;
        this.newsFeedList = newsFeedList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View newsfeedView = inflater.inflate(R.layout.item_newsfeed, parent, false);
        ViewHolder viewHolder = new ViewHolder(newsfeedView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        // Get item
        NewsFeed nf = newsFeedList.get(position);

        if (nf != null) {
            holder.username.setText(nf.getUsername());
            holder.postDescription.setText(nf.getPostDescription());
            holder.postContent.setText(nf.getPostContent());
            holder.documentName.setText(nf.getFileName() + "." + nf.getFileExtension());
            holder.likeNumber.setText(String.valueOf(nf.getLikeNumber()));
            holder.dislikeNumber.setText(String.valueOf(nf.getDislikeNumber()));
            holder.commentNumber.setText(String.valueOf(nf.getCommentNumber()));

            // set post's datetime
            setPostDateTime(holder.tvDateTime, nf.getTime());

            // set user's avatar
            getUserInfoAndSetAvatar(holder.profileImg, nf.getUsername());

            // set the post's image if the file is image type (jpg, png, jpeg,...)
            if (nf.getFileExtension().equals("jpg") || nf.getFileExtension().equals("png") || nf.getFileExtension().equals("jpeg")) {
                setImage(holder.imgPost, nf.getFileData());
            } else {
                holder.imgPost.setVisibility(View.GONE);
            }

            setOnClickListeners(holder, position);

            checkInteract(holder, position);
        }
    }

    @Override
    public int getItemCount() {
        return  newsFeedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView postDescription;
        TextView postContent;
        TextView documentName;
        ImageButton btnLike;
        ImageButton btnDislike;
        ImageButton btnComment;
        ImageButton btnSave;
        TextView likeNumber;
        TextView dislikeNumber;
        TextView commentNumber;

        ImageView profileImg;
        ImageView imgPost;
        TextView tvDateTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.tvUsername);
            postDescription = itemView.findViewById(R.id.tvPostDesciption);
            postContent = itemView.findViewById(R.id.tvPostContent);
            documentName = itemView.findViewById(R.id.tvDocumentName);
            btnLike = itemView.findViewById(R.id.btnLike);
            btnDislike = itemView.findViewById(R.id.btnDislike);
            btnComment = itemView.findViewById(R.id.btnComment);
            btnSave = itemView.findViewById(R.id.btnSave);
            likeNumber = itemView.findViewById(R.id.tvLikeNumber);
            dislikeNumber = itemView.findViewById(R.id.tvDislikeNumber);
            commentNumber = itemView.findViewById(R.id.tvCommentNumber);

            profileImg = itemView.findViewById(R.id.profileImg);
            imgPost = itemView.findViewById(R.id.imgPost);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);

            isLiked.add(false);
            isDisliked.add(false);
        }
    }

    private void setOnClickListeners(ViewHolder holder, int position) {
        holder.btnSave.setOnClickListener(new View.OnClickListener() {
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

        holder.profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start ProfileActivity then display user's info
                Intent myIntent = new Intent(context, ProfileActivity.class);
                myIntent.putExtra(ApplicationInfo.KEY_TO_PROFILE_ACTIVITY, holder.username.getText().toString());
                context.startActivity(myIntent);
            }
        });

        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Retrofit retrofit = new Retrofit.Builder().baseUrl(ApplicationInfo.apiPath).addConverterFactory(GsonConverterFactory.create()).build();
                ApiService apiService = retrofit.create(ApiService.class);
                if(isLiked.get(position)) // if already liked
                {
                    // call api to delete the like notification
                    deleteNotification(newsFeedList.get(position).getUsername(), 0, newsFeedList.get(position).getId(), username);

                    isLiked.set(position, false);
                    holder.btnLike.setImageResource(R.drawable.like_icon);
                    Call<Detail> callNewsfeed = apiService.postUnlike(newsFeedList.get(position).getId());
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

                    // call api to post the like notification
                    postNotification(newsFeedList.get(position).getUsername(), 0, newsFeedList.get(position).getId(), username);

                    holder.btnLike.setImageResource(R.drawable.like_icon_red);
                    isLiked.set(position, true);
                    Call<Detail> callNewsfeed = apiService.postLike(newsFeedList.get(position).getId());
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

                Call<Detail> callUserInteract = apiService.postInteract(username, newsFeedList.get(position).getId(), true);
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

        holder.btnDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Retrofit retrofit = new Retrofit.Builder().baseUrl(ApplicationInfo.apiPath).addConverterFactory(GsonConverterFactory.create()).build();

                ApiService apiService = retrofit.create(ApiService.class);

                if(isDisliked.get(position))
                {
                    // call api to delete the dislike notification
                    deleteNotification(newsFeedList.get(position).getUsername(), 1, newsFeedList.get(position).getId(), username);

                    isDisliked.set(position, false);
                    holder.btnDislike.setImageResource(R.drawable.dislike_icon);
                    Call<Detail> callNewsfeed = apiService.postUndislike(newsFeedList.get(position).getId());
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

                    // call api to post the dislike notification
                    postNotification(newsFeedList.get(position).getUsername(), 1, newsFeedList.get(position).getId(), username);

                    isDisliked.set(position, true);
                    holder.btnDislike.setImageResource(R.drawable.dislike_icon_red);
                    Call<Detail> callNewsfeed = apiService.postDislike(newsFeedList.get(position).getId());
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
                Call<Detail> callUserInteract = apiService.postInteract(username, newsFeedList.get(position).getId(), false);
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

        holder.btnComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open the comment activity then let the user comment, code API to increase comment number of this post by 1 then call it in there if user really comment
                Intent myIntent = new Intent(context, PostDetailActivity.class);
                // put the id array list
                myIntent.putExtra(ApplicationInfo.KEY_TO_POST_DETAIL_ACTIVITY, newsFeedList.get(position).getId());
                // start activity
                context.startActivity(myIntent);
            }
        });
    }

    private void checkInteract(ViewHolder holder, int position)
    {
        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApplicationInfo.apiPath).addConverterFactory(GsonConverterFactory.create()).build();
        ApiService apiService = retrofit.create(ApiService.class);
        Call<Detail> call = apiService.getInteract(username, newsFeedList.get(position).getId());
        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                try {
                    if (response.isSuccessful())
                    {
                        String res = response.body().getDetail();
                        if(res.equals("like"))
                        {
                            holder.btnLike.setImageResource(R.drawable.like_icon_red);
                            isLiked.set(position, true);
                            // add animation change from like to unlike
                        } else if (res.equals("dislike"))
                        {
                            holder.btnDislike.setImageResource(R.drawable.dislike_icon_red);
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
            if (file.exists()) {
                int i = 1;
                // Loop until the file is not exist
                do {
                    fileFullName = fileName + " (" + i + ")." + fileExtension;
                    pathToFile = pathToDownloads + "/" + fileName + " (" + i + ")." + fileExtension;
                    file = new File(pathToFile);
                    i++;
                } while (file.exists());
            }
            file.createNewFile();

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

        Call<Detail> call = apiService.getAvatarData(username);

        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                try {
                    if (response.isSuccessful()) {
                        // after getting the user's info, set the avatar
                        setImage(imageView, response.body().getDetail());
                    } else {
                        Toast.makeText(context, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR106: ", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
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
                imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, imageView.getWidth(), imageView.getHeight(), false));
                    }
                });
            }
        } catch (Exception ex) {
            Log.e("ERROR111: ", ex.getMessage());
        }
    }

    private void setPostDateTime(TextView textView, long time) {
        Date date = new Date(time * 1000);

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        String dateFormatted = formatter.format(date);

        textView.setText(dateFormatted);
    }

    private void postNotification(String username, Integer type,  Integer idPost, String interacter) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Detail> call = apiService.postNotification(username, type, idPost, interacter);

        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                try {
                    if (response.isSuccessful()) {
                        // do nothing
                    } else {
                        Toast.makeText(context, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR106: ", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteNotification(String username, Integer type,  Integer idPost, String interacter) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Detail> call = apiService.deleteNotification(username, type, idPost, interacter);

        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                try {
                    if (response.isSuccessful()) {
                        // do nothing
                    } else {
                        Toast.makeText(context, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR106: ", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
