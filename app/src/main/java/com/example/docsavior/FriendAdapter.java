package com.example.docsavior;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
    private Context context;
    private RecyclerViewInterface recyclerViewInterface;

    private List<Friend> friendList;

    private int displayType = 0; // 0 is friend request (FriendFragment), 1 is found user (InUserLookUp), 2 is my friend (MyFriend)

    public FriendAdapter(Context context, List<Friend> friendList, RecyclerViewInterface recyclerViewInterface, int displayType) {
        this.context = context;
        this.friendList = friendList;
        this.recyclerViewInterface = recyclerViewInterface;
        this.displayType = displayType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View friendView = inflater.inflate(R.layout.item_friend, parent, false);
        ViewHolder viewHolder = new ViewHolder(friendView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get item
        Friend fr = friendList.get(position);

        // set click listeners
        setOnClickListeners(holder, position);

        // set username
        holder.tvUsername.setText(fr.getUsername());

        // set avatar
        setAvatar(holder.profileImg, fr.getAvatarData());
    }

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImg;
        TextView tvUsername;
        ImageButton btnAccept;
        ImageButton btnDecline;
        Button btnAddFriend;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImg = itemView.findViewById(R.id.profileImg);
            tvUsername = itemView.findViewById(R.id.tvUsername);

            if (displayType == 1) {
                btnAddFriend = itemView.findViewById(R.id.btnAddFriend);

                // set VISIBILITY of btnAccept and btnDeclines to GONE
                itemView.findViewById(R.id.btnAccept).setVisibility(View.GONE);
                itemView.findViewById(R.id.btnDecline).setVisibility(View.GONE);
            } else if (displayType == 0) {
                btnAccept = itemView.findViewById(R.id.btnAccept);
                btnDecline = itemView.findViewById(R.id.btnDecline);

                // set VISIBILITY of btnAddFriend to GONE
                itemView.findViewById(R.id.btnAddFriend).setVisibility(View.GONE);
            } else {
                itemView.findViewById(R.id.btnAccept).setVisibility(View.GONE);
                itemView.findViewById(R.id.btnDecline).setVisibility(View.GONE);
                itemView.findViewById(R.id.btnAddFriend).setVisibility(View.GONE);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        recyclerViewInterface.startProfileActivity(getAdapterPosition());
                    }
                }
            });
        }
    }

    private void setOnClickListeners(ViewHolder holder, int position) {
        if (displayType == 1) {
            holder.btnAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // call API to post the friend request to database
                    postFriendRequest(holder.tvUsername.getText().toString());

                    // set the text of the button to "Cancel request"
                    holder.btnAddFriend.setText("Cancel request");

                    // call API to post notification
                    postNotification(holder.tvUsername.getText().toString(), 3, -1, ApplicationInfo.username);
                }
            });
        } else if (displayType == 0) {
            holder.btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // remove the Friend and its attribute base on the position
                    friendList.remove(position);
                    notifyDataSetChanged();

                    // call API to delete friend request from the database
                    deleteFriendRequest(holder.tvUsername.getText().toString(), false);
                    // call API to add friend in the database
                    postNewFriend(holder.tvUsername.getText().toString());

                    // call API to post notification
                    postNotification(holder.tvUsername.getText().toString(), 4, -1, ApplicationInfo.username);
                }
            });

            holder.btnDecline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // remove the Friend and its attribute base on the position
                    friendList.remove(position);
                    notifyDataSetChanged();

                    // call API to delete friend request from the database
                    deleteFriendRequest(holder.tvUsername.getText().toString(), true);

                    // call API to post notification
                    postNotification(holder.tvUsername.getText().toString(), 5, -1, ApplicationInfo.username);
                }
            });
        }

        holder.tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent myIntent = new Intent(context, ProfileActivity.class);
                 myIntent.putExtra(ApplicationInfo.KEY_TO_PROFILE_ACTIVITY, holder.tvUsername.getText().toString());
                 context.startActivity(myIntent);
            }
        });

        holder.profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, ProfileActivity.class);
                myIntent.putExtra(ApplicationInfo.KEY_TO_PROFILE_ACTIVITY, holder.tvUsername.getText().toString());
                context.startActivity(myIntent);
            }
        });
    }

    private void setAvatar(ImageView imageView, String avatarData) {
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
            Log.e("ERROR261: ", ex.getMessage());
        }
    }

    private void postFriendRequest(String username) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Detail> call = apiService.postFriendRequest(username, ApplicationInfo.username);

        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                try {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Sent friend request to " + username + "!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR1: ", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(context, "FAILURE: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void deleteFriendRequest(String requester, boolean isDeclined) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Detail> call = apiService.deleteFriendRequest(ApplicationInfo.username, requester);

        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                try {
                    if (response.isSuccessful()) {
                        if (isDeclined) {
                            Toast.makeText(context, "Declined " + requester + " friend request!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR1: ", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(context, "FAILURE: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void postNewFriend(String username) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Detail> call = apiService.postNewFriend(ApplicationInfo.username, username);

        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                try {
                    if (response.isSuccessful()) {
                        Toast.makeText(context, "Accepted " + username + " friend request!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR1: ", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(context, "FAILURE: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
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
}
