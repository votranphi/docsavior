package com.example.docsavior;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SettingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingFragment newInstance(String param1, String param2) {
        SettingFragment fragment = new SettingFragment();
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
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    // MAIN THINGS FROM HERE
    ImageButton btnProfile;
    RelativeLayout rltNotification;
    RelativeLayout rltChangePassword;
    RelativeLayout rltLogOut;

    // this function is the same as onCreate() in Activity
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        findViewByIds();

        setOnClickListeners();
    }

    private void findViewByIds() {
        btnProfile = getView().findViewById(R.id.btnProfile);
        rltNotification = getView().findViewById(R.id.rltNotification);
        rltChangePassword = getView().findViewById(R.id.rltChangePassword);
        rltLogOut = getView().findViewById(R.id.rltLogOut);
    }

    private void setOnClickListeners() {
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        rltNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: open the Notification settings
            }
        });

        rltChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: open change password activity them do things
            }
        });

        rltLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call API to send logout signal
                postLogout();

                Intent myIntent = new Intent(getActivity(), MainActivity.class);
                // start MainActivity and clear all other activities
                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(myIntent);
            }
        });
    }

    private void postLogout() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Detail> call = apiService.postLogout(ApplicationInfo.username);

        call.enqueue(new Callback<Detail>() {
            @Override
            public void onResponse(Call<Detail> call, Response<Detail> response) {
                try {
                    if (response.isSuccessful()) {
                        Toast.makeText(getActivity(), "Logout successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR1: ", String.valueOf(response.code()));
                }
            }

            @Override
            public void onFailure(Call<Detail> call, Throwable t) {
                Toast.makeText(getActivity(), "FAILURE: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}