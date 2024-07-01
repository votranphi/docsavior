package com.example.docsavior;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConversationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConversationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ConversationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConversationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConversationFragment newInstance(String param1, String param2) {
        ConversationFragment fragment = new ConversationFragment();
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
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    // MAIN THINGS FROM HERE
    private ImageButton btnProfile;
    private ListView rcvConversationList;
    private TextView tvNothing;

    private ArrayList<Conversation> conversationArrayList;
    private ConversationAdapter conversationAdapter;
    // this function is the same as onCreate() in Activity
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        findViewByIds();

        setOnClickListeners();

        initVariables();

        getAndSetConversations();
    }

    private void findViewByIds() {
        btnProfile = getView().findViewById(R.id.btnProfile);
        rcvConversationList = getView().findViewById(R.id.rcvConversationList);
        tvNothing = getView().findViewById(R.id.tvNothing);
    }

    public void setOnClickListeners() {
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getActivity(), ProfileActivity.class);
                myIntent.putExtra(ApplicationInfo.KEY_TO_PROFILE_ACTIVITY, ApplicationInfo.username);
                startActivity(myIntent);
            }
        });

        rcvConversationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // start ChatDetailActivity then do things
                Intent myIntent = new Intent(getActivity(), ChatDetailActivity.class);
                myIntent.putExtra(ApplicationInfo.KEY_TO_CHAT_DETAIL_ACTIVITY, conversationArrayList.get(position).getUsername());
                startActivity(myIntent);
            }
        });
    }

    private void initVariables() {
        conversationArrayList = new ArrayList<>();
        conversationAdapter = new ConversationAdapter(getActivity(), R.layout.item_chat, conversationArrayList);
        rcvConversationList.setAdapter(conversationAdapter);
    }

    private void getAndSetConversations() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<MessagedUsernames> call = apiService.getMessagedUsernames(ApplicationInfo.username);

        call.enqueue(new Callback<MessagedUsernames>() {
            @Override
            public void onResponse(Call<MessagedUsernames> call, Response<MessagedUsernames> response) {
                try {
                    if (response.isSuccessful()) {
                        getLatestMsgThenAddToListView(response.body());

                        // set the visibility of "NOTHING TO SHOW" to GONE
                        tvNothing.setVisibility(View.GONE);
                    } else if (response.code() == 600) {
                        // set the visibility of "NOTHING TO SHOW" to VISIBLE
                        tvNothing.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(getActivity(), response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR100: ", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<MessagedUsernames> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLatestMsgThenAddToListView(MessagedUsernames messagedUsernames) {
        try {
            for (int i = 0; i < messagedUsernames.getMessagedUsernames().length; i++) {
                loadLatestMessage(ApplicationInfo.username, messagedUsernames.getMessagedUsernames()[i]);
            }
        } catch (Exception ex) {
            Log.e("ERROR2: ", ex.getMessage());
        }
    }

    private void loadLatestMessage(String username, String sender) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ApplicationInfo.apiPath)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        Call<Message> call = apiService.getLatestMessage(username, sender);

        call.enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                try {
                    if (response.isSuccessful()) {
                        Message latestMessage = response.body();

                        Conversation conversation = null;
                        if (latestMessage.getUsername().equals(ApplicationInfo.username)) {
                            conversation = new Conversation(sender, latestMessage.getContent(), latestMessage.getIsSeen());
                        } else {
                            conversation = new Conversation(sender, "You: " + latestMessage.getContent(), true);
                        }
                        conversationArrayList.add(conversation);
                        conversationAdapter.notifyDataSetChanged();
                    } else if (response.code() == 600) {
                        // do nothing
                    } else {
                        Toast.makeText(getActivity(), response.code() + response.errorBody().string(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception ex) {
                    Log.e("ERROR100: ", ex.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}