package com.example.docsavior;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NewsfeedLoader extends Thread {

    private Context context;
    private RecyclerView lvPost;
    private ArrayList<Newsfeed> newsfeedArrayList;
    private NewsfeedAdapter newsFeedAdapter;
    private TextView tvNothing;
    private Map<Integer, Newsfeed> postCache = new HashMap<>();
    private Boolean isLoading = false; // check if app is calling api
    private int numberOfPost; // total posts in database
    private int page;
    private final int PAGE_SIZE = 2; // page size (load PAGE_SIZE post after scroll to the bottom of the ListView)
    private final int NUMBER_OF_POST_LOADED_FIRST = 2; // the number of posts will be loaded to the screen first time user enters the newsfeed screen

    public NewsfeedLoader(Context context, RecyclerView lvPost, ArrayList<Newsfeed> newsfeedArrayList, NewsfeedAdapter newsFeedAdapter, TextView tvNothing) {
        super();
        this.context = context;
        this.lvPost = lvPost;
        this.newsfeedArrayList = newsfeedArrayList;
        this.newsFeedAdapter = newsFeedAdapter;
        this.tvNothing = tvNothing;
        this.page = 0;
    }

    @Override
    public void run() {
        getNumberOfPosts(); // get total posts

        loadPostForTheFirstTime();

        setOnClickListeners();
    }

    private void loadPostForTheFirstTime() {
        for (int i = 0; i < NUMBER_OF_POST_LOADED_FIRST; i++) {
            getSequenceOfPost(i, PAGE_SIZE);
        }
    }

    private void getNumberOfPosts()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Integer> call = apiService.getNumberOfPosts();
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                try{
                    if(response.isSuccessful())
                    {
                        numberOfPost = response.body().intValue();
                    }
                }
                catch (Exception t)
                {
                    Log.d("Error: ", t.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setOnClickListeners() {
        lvPost.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (/* !recyclerView.canScrollVertically(0) && !isLoading && */ page <= (numberOfPost / PAGE_SIZE) + 1 && newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    isLoading = true; // app is calling api
                    getSequenceOfPost(page, PAGE_SIZE);
                }
            }
        });
    }


    private void getSequenceOfPost(int number, int pageSize) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<Newsfeed>> call = apiService.getSequenceOfPost(number, pageSize);

        call.enqueue(new Callback<List<Newsfeed>>() {
            @Override
            public void onResponse(Call<List<Newsfeed>> call, Response<List<Newsfeed>> response) {
                try {
                    if (response.isSuccessful()) {
                        List<Newsfeed> responseList = response.body();
                        page++; // if response successfully, page increase one more
                        // add the elements in responseList to newsfeedArrayList
                        for (Newsfeed i : responseList) {
                            if(!postCache.containsKey(i.getId())) // check if this post already load
                            {
                                newsfeedArrayList.add(i);
                                // update the ListView every one post
                                newsFeedAdapter.notifyDataSetChanged();
                                postCache.put(i.getId(), i); // put post into cache
                            }
                        }

                        // set the visibility of "NOTHING TO SHOW" to GONE
                        tvNothing.setVisibility(View.GONE);
                    } else if (response.code() == 600) {
                        // set the visibility of "NOTHING TO SHOW" to VISIBLE
                        tvNothing.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(context, response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                    isLoading = false; // after calling api, set isLoading to false
                } catch (Exception ex) {
                    Log.e("ERROR100: ", ex.getMessage());
                    isLoading = false; // after calling api, set isLoading to false
                }
            }

            @Override
            public void onFailure(Call<List<Newsfeed>> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                isLoading = false; // after calling api, set isLoading to false
            }
        });
    }
}
