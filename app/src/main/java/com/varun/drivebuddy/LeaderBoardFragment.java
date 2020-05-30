package com.varun.drivebuddy;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


/**
 * A simple {@link Fragment} subclass.
 */
public class LeaderBoardFragment extends Fragment {


    public LeaderBoardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_leader_board, container, false);

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Choose authentication providers
            Intent intent = new Intent(getContext(), SignInActivity.class);
            startActivity(intent);
        }

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("userScores");

        FirebaseRecyclerOptions<UserScore> options =
                new FirebaseRecyclerOptions.Builder<UserScore>()
                        .setQuery(query, UserScore.class)
                        .build();

        adapter = new FirebaseRecyclerAdapter<UserScore, UserScoreViewHolder>(options) {
            @Override
            public UserScoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                // Create a new instance of the ViewHolder, in this case we are using a custom
                // layout called R.layout.message for each item
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_score_item, parent, false);

                return new UserScoreViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(UserScoreViewHolder holder, int position, UserScore model) {
                holder.setName(model.getName());
                holder.setScore(model.getScore());
            }
        };

        RecyclerView recyclerView = rootView.findViewById(R.id.userScoresRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    public FirebaseRecyclerAdapter adapter;

}
