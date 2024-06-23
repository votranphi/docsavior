package com.example.docsavior;

import android.app.Activity;
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

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FriendAdapter extends ArrayAdapter<Friend> {
    private final Activity context;
    private List<ImageView> profileImgs = new ArrayList<>();
    private List<TextView> tvUsernames = new ArrayList<>();
    private List<ImageButton> btnAccepts = new ArrayList<>();
    private List<ImageButton> btnDeclines = new ArrayList<>();
    private List<Button> btnAddFriends = new ArrayList<>();
    private List<Friend> friendList = new ArrayList<>();

    private int displayType = 0; // 0 is friend request (FriendFragment), 1 is found user (InUserLookUp), 2 is my friend (MyFriend)

    public FriendAdapter(Activity context, int layoutID, List<Friend> objects, int displayType) {
        super(context, layoutID, objects);
        this.context = context;
        this.displayType = displayType;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_friend, null, false);
        }
        // Get item
        Friend fr = getItem(position);

        // add fr to friend list
        friendList.add(fr);

        // Get view
        findViewByIds(convertView);

        // set click listeners
        setOnClickListeners(position);

        // set username
        tvUsernames.get(position).setText(fr.getUsername());

        // set avatar
        setAvatar(profileImgs.get(position), fr.getAvatarData());

        return convertView;
    }

    private void findViewByIds(View convertView) {
        profileImgs.add(convertView.findViewById(R.id.profileImg));
        tvUsernames.add(convertView.findViewById(R.id.tvUsername));

        if (displayType == 1) {
            btnAddFriends.add(convertView.findViewById(R.id.btnAddFriend));

            // set VISIBILITY of btnAccept and btnDeclines to GONE
            convertView.findViewById(R.id.btnAccept).setVisibility(View.GONE);
            convertView.findViewById(R.id.btnDecline).setVisibility(View.GONE);
        } else if (displayType == 0) {
            btnAccepts.add(convertView.findViewById(R.id.btnAccept));
            btnDeclines.add(convertView.findViewById(R.id.btnDecline));

            // set VISIBILITY of btnAddFriend to GONE
            convertView.findViewById(R.id.btnAddFriend).setVisibility(View.GONE);
        } else {
            convertView.findViewById(R.id.btnAccept).setVisibility(View.GONE);
            convertView.findViewById(R.id.btnDecline).setVisibility(View.GONE);
            convertView.findViewById(R.id.btnAddFriend).setVisibility(View.GONE);
        }
    }

    private void setOnClickListeners(int position) {
        if (displayType == 1) {
            btnAddFriends.get(position).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // call API to post the friend request to database
                    postFriendRequest(tvUsernames.get(position).getText().toString());

                    // set the text of the button to "Cancel request"
                    btnAddFriends.get(position).setText("Cancel request");

                    // call API to post notification
                    postNotification(tvUsernames.get(position).getText().toString(), 3, -1, ApplicationInfo.username);
                }
            });
        } else if (displayType == 0) {
            btnAccepts.get(position).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // remove the Friend and its attribute base on the position
                    remove(friendList.get(position));
                    friendList.remove(position);
                    notifyDataSetChanged();

                    // call API to delete friend request from the database
                    deleteFriendRequest(tvUsernames.get(position).getText().toString(), false);
                    // call API to add friend in the database
                    postNewFriend(tvUsernames.get(position).getText().toString());

                    // call API to post notification
                    postNotification(tvUsernames.get(position).getText().toString(), 4, -1, ApplicationInfo.username);
                }
            });

            btnDeclines.get(position).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // remove the Friend and its attribute base on the position
                    remove(friendList.get(position));
                    friendList.remove(position);
                    notifyDataSetChanged();

                    // call API to delete friend request from the database
                    deleteFriendRequest(tvUsernames.get(position).getText().toString(), true);

                    // call API to post notification
                    postNotification(tvUsernames.get(position).getText().toString(), 5, -1, ApplicationInfo.username);
                }
            });
        }

        tvUsernames.get(position).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent myIntent = new Intent(context, ProfileActivity.class);
                 myIntent.putExtra(ApplicationInfo.KEY_TO_PROFILE_ACTIVITY, tvUsernames.get(position).getText().toString());
                 context.startActivity(myIntent);
            }
        });

        profileImgs.get(position).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(context, ProfileActivity.class);
                myIntent.putExtra(ApplicationInfo.KEY_TO_PROFILE_ACTIVITY, tvUsernames.get(position).getText().toString());
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
                imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, 40, 40, false));
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
