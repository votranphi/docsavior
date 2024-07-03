package com.example.docsavior;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MessageLoader extends Thread {

    private Context context;
    MessageAdapter messageAdapter;
    ArrayList<Message> messageArrayList;
    TextView tvStatus;
    private String username;

    public MessageLoader(Context context, MessageAdapter messageAdapter, ArrayList<Message> messageArrayList, String username, TextView tvStatus) {
        this.context = context;
        this.messageAdapter = messageAdapter;
        this.messageArrayList = messageArrayList;
        this.username = username;
        this.tvStatus = tvStatus;
    }

    @Override
    public void run() {
        try {
            while (true) {
                getAndSetUserStatus(username);
                getUnseenMessage();

                // get unseen message after every 5s
                Thread.sleep(5000);
            }
        } catch (Exception ex) {
            Log.e("ERROR285: ", ex.getMessage());
        }
    }

    private void getUnseenMessage() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<Message>> call = apiService.getUnseenMessage(ApplicationInfo.username, username);

        call.enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                try {
                    if (response.isSuccessful()) {
                        List<Message> messages = response.body();

                        for (Message i : messages) {
                            // assign the new message to listview
                            assignMessageToListView(i);
                            // update the message status to "seen"
                            postSeenToTrue(i.getId());
                        }
                    } else {
                        Toast.makeText(context, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR106: ", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void assignMessageToListView(Message message) {
        messageArrayList.add(message);
        messageAdapter.notifyItemInserted(messageArrayList.size() - 1);
    }

    private void postSeenToTrue(Integer id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Detail> call = apiService.postSeenToTrue(id);

        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                try {
                    if (response.isSuccessful()) {
                        // do thing
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

    private void getAndSetUserStatus(String username) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Detail> call = apiService.getUserStatus(username);

        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                try {
                    if (response.isSuccessful()) {
                        if (response.body().getDetail().equals("true")) {
                            tvStatus.setText("Online");
                        } else {
                            tvStatus.setText("Offline");
                        }
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