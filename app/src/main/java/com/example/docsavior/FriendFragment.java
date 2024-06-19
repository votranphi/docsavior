package com.example.docsavior;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
    private ListView lvRequest;
    private FriendAdapter friendAdapter;
    private ArrayList<Friend> friendArrayList;

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
    }

    private void setOnClickListeners() {
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
                Intent myIntent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(myIntent);
            }
        });
    }

    private void initVariables() {
        friendArrayList = new ArrayList<>();
        friendAdapter = new FriendAdapter(getActivity(), R.layout.item_friend, friendArrayList);
        lvRequest.setAdapter(friendAdapter);
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
                            // TODO: set visibility of "NOTHING TO SHOW" to VISIBLE
                        } else {
                            // TODO: set visibility of "NOTHING TO SHOW" to GONE

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
            for (int i = 0; i < requesters.getRequesters().length; i++) {
                Friend friend = new Friend("", requesters.getRequesters()[i]);
                friendArrayList.add(friend);
                friendAdapter.notifyDataSetChanged();
            }
        } catch (Exception ex) {
            Log.e("ERROR2: ", ex.getMessage());
        }
    }
}