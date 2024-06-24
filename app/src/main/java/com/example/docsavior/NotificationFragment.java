package com.example.docsavior;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public NotificationFragment(FragmentNavigation fragmentNavigation) {
        // Required empty public constructor
        this.fragmentNavigation = fragmentNavigation;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment(fragmentNavigation);
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
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    private ImageButton btnProfile;
    private TextView tvNothing;
    private ListView lvNotification;

    private ArrayList<Notification> notificationArrayList;

    private NotificationAdapter adapter;

    private static FragmentNavigation fragmentNavigation;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        findViewByIds();

        initVariables();

        setOnClickListeners();

        loadNotifications();
    }

    private void findViewByIds()
    {
        btnProfile = getView().findViewById(R.id.btnProfile);
        tvNothing = getView().findViewById(R.id.tvNothing);
        lvNotification = getView().findViewById(R.id.lvNotification);
    }

    private void initVariables() {
        notificationArrayList = new ArrayList<>();
        adapter = new NotificationAdapter(getActivity(), R.layout.item_notification, notificationArrayList);
        lvNotification.setAdapter(adapter);
    }
    private void setOnClickListeners()
    {
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity(), ProfileActivity.class);
                myIntent.putExtra(ApplicationInfo.KEY_TO_PROFILE_ACTIVITY, ApplicationInfo.username);
                startActivity(myIntent);
            }
        });

        lvNotification.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO: jump to the Activity base on the type of the notification
            }
        });

        lvNotification.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int notiType = notificationArrayList.get(position).getType();
                if (notiType >= 0 && notiType <= 2) {
                    Intent myIntent = new Intent(getActivity(), PostDetailActivity.class);
                    // put the id array list
                    myIntent.putExtra(ApplicationInfo.KEY_TO_POST_DETAIL_ACTIVITY, notificationArrayList.get(position).getIdPost());
                    startActivity(myIntent);
                } else if (notiType == 3) {
                    // go to friend fragment
                    if (fragmentNavigation != null) {
                        fragmentNavigation.goToFriendFragment();
                    }
                } else { // if (notiType >= 4 && notiType <= 5)
                    Intent myIntent = new Intent(getActivity(), ProfileActivity.class);
                    myIntent.putExtra(ApplicationInfo.KEY_TO_PROFILE_ACTIVITY, notificationArrayList.get(position).getInteracter());
                    startActivity(myIntent);
                }
            }
        });
    }

    private void loadNotifications()
    {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<Notification>> call = apiService.getAllMyNotifications(ApplicationInfo.username);

        call.enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                try {
                    if (response.isSuccessful()) {
                        List<Notification> responseList = response.body();

                        if (responseList.size() == 0) {
                            // set the visibility of "NOTHING TO SHOW" to GONE
                            tvNothing.setVisibility(View.VISIBLE);
                        } else {
                            // add the elements in responseList to newsFeedArrayList
                            for (Notification i : responseList) {
                                notificationArrayList.add(i);
                                // update the ListView
                                adapter.notifyDataSetChanged();
                            }

                            tvNothing.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(getActivity(), response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR100: ", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}