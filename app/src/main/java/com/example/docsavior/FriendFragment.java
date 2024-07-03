package com.example.docsavior;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
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
 * Use the {@link FriendFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FriendFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FriendFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FriendFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FriendFragment newInstance(String param1, String param2) {
        FriendFragment fragment = new FriendFragment();
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
        return inflater.inflate(R.layout.fragment_friend, container, false);
    }


    // MAIN THINGS FROM HERE
    private ImageButton btnLookup;
    private ImageButton btnProfile;
    private RecyclerView lvRequest;
    private FriendAdapter friendAdapter;
    private ArrayList<Friend> friendArrayList;
    private TextView tvNothing;

    private View loadingPanel;
    // this function is the same as onCreate() in Activity
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        findViewByIds();

        initVariables();

        setOnClickListeners();

        loadFriendRequests();
    }

    private void findViewByIds() {
        btnLookup = getView().findViewById(R.id.btnLookup);
        btnProfile = getView().findViewById(R.id.btnProfile);
        lvRequest = getView().findViewById(R.id.lvRequest);
        tvNothing = getView().findViewById(R.id.tvNothing);
        loadingPanel = getView().findViewById(R.id.loadingPanel);
    }

    private void setOnClickListeners() {
        btnLookup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity(), LookUpPostUserActivity.class);
                myIntent.putExtra(ApplicationInfo.KEY_TO_LOOK_UP_POST_USER_ACTIVITY, ApplicationInfo.LOOK_UP_TYPE_USER);
                startActivity(myIntent);
                getActivity().overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_right);
            }
        });

        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity(), ProfileActivity.class);
                myIntent.putExtra(ApplicationInfo.KEY_TO_PROFILE_ACTIVITY, ApplicationInfo.username);
                startActivity(myIntent);
                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
        lvRequest.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!recyclerView.canScrollVertically(-1) && newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    loadingPanel.setVisibility(View.VISIBLE);
                    friendArrayList.clear();
                    friendAdapter.notifyDataSetChanged();
                    loadFriendRequests();
                }
            }
        });
    }

    private void initVariables() {
        friendArrayList = new ArrayList<>();
        friendAdapter = new FriendAdapter(getActivity(), friendArrayList, 0);

        lvRequest.setHasFixedSize(true);
        lvRequest.setItemViewCacheSize(20);
        friendAdapter.setHasStableIds(true);

        lvRequest.setAdapter(friendAdapter);
        lvRequest.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadFriendRequests() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Requester> call = apiService.getAllFriendRequests(ApplicationInfo.username);

        call.enqueue(new Callback<Requester>() {
            @Override
            public void onResponse(Call<Requester> call, Response<Requester> response) {
                try {
                    if (response.isSuccessful()) {
                        Requester requesters = response.body();
                        if (requesters.getRequesters().length == 0) {
                            tvNothing.setVisibility(View.VISIBLE);
                        } else {
                            tvNothing.setVisibility(View.GONE);

                            assignRequestersToListView(requesters);
                        }
                    } else {
                        Toast.makeText(getActivity(), response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR1: ", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Requester> call, Throwable t) {
                Toast.makeText(getActivity(), "FAILURE: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void assignRequestersToListView(Requester requesters) {
        try {
            loadingPanel.setVisibility(View.GONE);
            for (int i = 0; i < requesters.getRequesters().length; i++) {
                getAvatarDataThenAddToArrayList(requesters.getRequesters()[i]);
            }
        } catch (Exception ex) {
            Log.e("ERROR2: ", ex.getMessage());
        }
    }

    private void getAvatarDataThenAddToArrayList(String username) {
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
                        Friend friend = new Friend(response.body().getDetail(), username);
                        friendArrayList.add(friend);
                        friendAdapter.notifyItemInserted(friendArrayList.size() - 1);
                    } else {
                        Toast.makeText(getActivity(), response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR106: ", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}