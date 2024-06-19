package com.example.docsavior;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NewsFeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NewsFeedFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NewsFeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NewFeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NewsFeedFragment newInstance(String param1, String param2) {
        NewsFeedFragment fragment = new NewsFeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_newsfeed, container, false);
    }

    // MAIN THINGS FROM HERE
    private ImageButton btnCreatePost;
    private ImageButton btnLookup;
    private ImageButton btnProfile;
    private ListView lvPost;
    private NewsFeedAdapter newsFeedAdapter;
    private ArrayList<NewsFeed> newsFeedArrayList;

    // this function is the same as onCreate() in Activity
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        findViewByIds();

        initVariables();

        setOnClickListeners();
    }

    private void findViewByIds() {
        btnCreatePost = getView().findViewById(R.id.btnCreatePost);
        btnLookup = getView().findViewById(R.id.btnLookup);
        btnProfile = getView().findViewById(R.id.btnProfile);
        lvPost = getView().findViewById(R.id.lvPost);
    }

    private void setOnClickListeners() {
        btnCreatePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open the CreatePostActivity when user click the button
                Intent myIntent = new Intent(getActivity(), CreatePostActivity.class);
                startActivity(myIntent);
            }
        });

        btnLookup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: start the look up activity then do the things
                Intent myIntent = new Intent(getActivity(), LookUpPostUserActivity.class);
                startActivity(myIntent);
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: start the profile activity then do the things
            }
        });
    }

    private void initVariables() {
        newsFeedArrayList = new ArrayList<>();
        newsFeedAdapter = new NewsFeedAdapter(getActivity(), R.layout.item_newsfeed, newsFeedArrayList);
    }

    // let's complete it later
    private void getAllPost() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService retrofitAPI = retrofit.create(ApiService.class);

        Call<List<NewsFeed>> call = retrofitAPI.getAllPosts();

        call.enqueue(new Callback<List<NewsFeed>>() {
            @Override
            public void onResponse(Call<List<NewsFeed>> call, Response<List<NewsFeed>> response) {
                if (response.isSuccessful()) {
                    ListView lvPost = getView().findViewById(R.id.lvPost);
                    ArrayList<NewsFeed> posts = new ArrayList<>();

                    for (NewsFeed i : response.body()) {
                        // posts.add(new NewsFeed(i.getId(), i.getUsername(), i.getPostDescription(), i.getPostContent(), i.getLikeNumber(), i.getDislikeNumber(), i.getCommentNumber()));
                    }

                    NewsFeedAdapter newfeedAdapter = new NewsFeedAdapter(getActivity(), R.layout.item_newsfeed, posts);

                    lvPost.setAdapter(newfeedAdapter);
                } else {
                    try {
                        JSONObject jsonObject = new JSONObject(response.errorBody().string());
                        Toast.makeText(getActivity(), jsonObject.get("detail").toString(), Toast.LENGTH_SHORT).show();
                    } catch (Exception ex) {

                    }
                }
            }

            @Override
            public void onFailure(Call<List<NewsFeed>> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}