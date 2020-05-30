package com.varun.drivebuddy.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.firebase.ui.auth.AuthUI;
import com.varun.drivebuddy.DataProcessorOutput;
import com.varun.drivebuddy.DriveModeFragment;
import com.varun.drivebuddy.DrivingResultFragment;
import com.varun.drivebuddy.LeaderBoardFragment;
import com.varun.drivebuddy.R;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        /*final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/

        Button button = root.findViewById(R.id.start_driving_mode);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DriveModeFragment driveModeFragment = new DriveModeFragment();
                openFragment(driveModeFragment);


                /*ArrayList<DataProcessorOutput> dataProcessorOutputArrayList =
                        new ArrayList<>();

                dataProcessorOutputArrayList.add(new DataProcessorOutput(28,32,5000));
                dataProcessorOutputArrayList.add(new DataProcessorOutput(30,32,6000));
                dataProcessorOutputArrayList.add(new DataProcessorOutput(35,32,8000));
                dataProcessorOutputArrayList.add(new DataProcessorOutput(50,32,3000));
                dataProcessorOutputArrayList.add(new DataProcessorOutput(20,32,5000));

                DrivingResultFragment drivingResultFragment = new DrivingResultFragment(dataProcessorOutputArrayList);

                openFragment(drivingResultFragment);*/
            }
        });

        Button leaderBoardButton = root.findViewById(R.id.leaderboard);
        leaderBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LeaderBoardFragment leaderBoardFragment = new LeaderBoardFragment();
                openFragment(leaderBoardFragment);
            }
        });

        Button signOutButton = root.findViewById(R.id.sign_out);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthUI.getInstance().signOut(getContext());
            }
        });

        return root;
    }

    public void openFragment(Fragment fragment){
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(((ViewGroup)getView().getParent()).getId(), fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
}