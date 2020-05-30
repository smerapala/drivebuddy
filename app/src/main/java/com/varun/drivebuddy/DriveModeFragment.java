package com.varun.drivebuddy;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class DriveModeFragment extends Fragment {


    public DriveModeFragment() {
        // Required empty public constructor
        startDrive = false;
        isCharity = false;
    }

    public DriveModeFragment(boolean isCharity, int amount){
        this.isCharity = true;
        this.amount = amount;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_drive_mode, container, false);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            // Choose authentication providers
            Intent intent = new Intent(getContext(), SignInActivity.class);
            startActivity(intent);
        }

        final Button startDrivingButton = rootView.findViewById(R.id.startDrivingButton);

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();


        databaseReference = firebaseDatabase.getReference("driving_data")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("rawValue");

        drivingDataTextView = rootView.findViewById(R.id.drivingDataTextView);

        final DriveProcessorState driveProcessorState = new DriveProcessorState();

        driveQualityProcessor = new DriveQualityProcessor(driveProcessorState);

        dataProcessor = new DataProcessor();

        dataProcessorOutputs = new ArrayList<>();

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String row = dataSnapshot.getValue(String.class);
                if (row != null && !row.equals("")) {
                    DataProcessorOutput dataProcessorOutput = dataProcessor.processData(row);
                    dataProcessorOutputs.add(dataProcessorOutput);
                    updateUIWithData(dataProcessorOutputs);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        startDrivingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!startDrive) {
                    startDrivingButton.setText("Stop Driving");
                    databaseReference.addValueEventListener(valueEventListener);
                } else {
                    startDrivingButton.setText("Start Driving");
                    databaseReference.removeEventListener(valueEventListener);

                    if (dataProcessorOutputs.size() < 1) {
                        Toast.makeText(getContext(), "Not enough data!", Toast.LENGTH_SHORT).show();
                        getFragmentManager().popBackStack();
                        return;
                    }

                    DriveProcessorState driveProcessorState1 = driveQualityProcessor.getDriveProcessorState();

                    DrivingResultFragment drivingResultFragment;
                    if(!isCharity) {
                        drivingResultFragment = new DrivingResultFragment(dataProcessorOutputs, driveProcessorState1);
                    }else{
                        drivingResultFragment = new DrivingResultFragment(isCharity, amount, dataProcessorOutputs, driveProcessorState1);
                    }
                    dataProcessorOutputs.clear();
                    openFragment(drivingResultFragment);
                }
                startDrive = !startDrive;
            }
        });

        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

            }
        });

        textToSpeech.setLanguage(Locale.US);

        ImageView imageView = rootView.findViewById(R.id.car);
        Glide.with(getContext()).load(getImage("car")).into(imageView);

        return rootView;
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(((ViewGroup) getView().getParent()).getId(), fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public int getImage(String imageName) {

        int drawableResourceId = this.getResources().getIdentifier(imageName, "drawable", getContext().getPackageName());

        return drawableResourceId;
    }

    private void updateUIWithData(ArrayList<DataProcessorOutput> dataProcessorOutputs) {
        String text = driveQualityProcessor.getOutput(dataProcessorOutputs);
        if (text == null || text.equals("")) {
            drivingDataTextView.setText("The drive is going well!");
        } else {
            if (text.contains("SMS")) {
                databaseReference.removeEventListener(valueEventListener);
                Intent intent = new Intent(getContext(), CrashActivity.class);
                intent.putExtra("msg", "Hi. " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + " was driving and might have met with an accident. Lat and Long are " + String.valueOf(dataProcessorOutputs.get(dataProcessorOutputs.size() - 1).getActual_lat()) + " and " +
                        String.valueOf(dataProcessorOutputs.get(dataProcessorOutputs.size() - 1).getActual_lat()));
                startActivity(intent);
                return;
            }
            drivingDataTextView.setText(text);
            if (shouldSpeak(text)) {
                if (textToSpeech.isSpeaking()) {
                    textToSpeech.stop();
                }
                previousSpokenPosition = dataProcessorOutputs.size();
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
                previousSpokenState = 0;
                if (text.contains("accelerate")) {
                    previousSpokenState = 1;
                } else if (text.contains("speed limit")) {
                    previousSpokenState = 2;
                } else if (text.contains("turn so")) {
                    previousSpokenState = 3;
                } else {
                    previousSpokenState = 4;
                }
            }
        }

    }

    private boolean shouldSpeak(String text) {
        int currentState = 0;
        if (text.contains("accelerate")) {
            currentState = 1;
        } else if (text.contains("speed limit")) {
            currentState = 2;
        } else if (text.contains("turn so")) {
            currentState = 3;
        } else {
            currentState = 4;
        }

        if (currentState == previousSpokenState) {
            if (dataProcessorOutputs.size() - previousSpokenPosition > 3) {
                return true;
            } else {
                return false;
            }
        }

        return true;
    }

    public boolean startDrive = false;
    public TextView drivingDataTextView;
    public DataProcessor dataProcessor;
    public ArrayList<DataProcessorOutput> dataProcessorOutputs;
    public DriveQualityProcessor driveQualityProcessor;
    public TextToSpeech textToSpeech;
    public int previousSpokenState = -1;
    public int previousSpokenPosition = -1;
    public ValueEventListener valueEventListener;
    public DatabaseReference databaseReference;
    public boolean isCharity;
    public int amount;

}
