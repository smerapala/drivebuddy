package com.varun.drivebuddy;


import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class DrivingResultFragment extends Fragment {


    public DrivingResultFragment() {
        // Required empty public constructor
        isCharity = false;
    }

    public DrivingResultFragment(ArrayList<DataProcessorOutput> dataProcessorOutputs, DriveProcessorState driveProcessorState){
        this.dataProcessorOutputs = new ArrayList<>();
        this.dataProcessorOutputs.addAll(dataProcessorOutputs);
        this.driveProcessorState = driveProcessorState;
        this.isCharity = false;
    }

    public DrivingResultFragment(boolean isCharity, int amount, ArrayList<DataProcessorOutput> dataProcessorOutputs, DriveProcessorState driveProcessorState){
        this.dataProcessorOutputs = new ArrayList<>();
        this.dataProcessorOutputs.addAll(dataProcessorOutputs);
        this.driveProcessorState = driveProcessorState;
        this.isCharity = isCharity;
        this.amount = amount;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_driving_result, container, false);


        final DriveQualityOutput driveQualityOutput;

        driveQualityProcessor = new DriveQualityProcessor(driveProcessorState);

        driveQualityOutput = driveQualityProcessor.computeDriveQuality(dataProcessorOutputs);

        TextView overallResult = root.findViewById(R.id.overallResult);
        overallResult.setText(String.valueOf(driveQualityOutput.getOverallResult()));

        TextView percentTimeFollowedSpeedLimit = root.findViewById(R.id.percentExceededSpeedLimit);
        percentTimeFollowedSpeedLimit.setText("You followed the speed limit "+String.valueOf(driveQualityOutput.percentFollowedSpeedLimit)+"% of the time!");

        TextView percentTimeFollowedRPM = root.findViewById(R.id.percentExceededRPMLimit);
        percentTimeFollowedRPM.setText("Your RPM was normal "+String.valueOf(driveQualityOutput.getPercentFollowedRPM())+"% of the time!");

        GraphView graphView =  (GraphView) root.findViewById(R.id.speedGraph);

        DataPoint[] speeds = new DataPoint[dataProcessorOutputs.size()];

        DataPoint[] limits = new DataPoint[dataProcessorOutputs.size()];

        DataPoint[] smooth = new DataPoint[dataProcessorOutputs.size()];

        for(int i = 0;i<dataProcessorOutputs.size();i++){
            speeds[i] = new DataPoint(i,dataProcessorOutputs.get(i).getSpeed());
            limits[i] = new DataPoint(i,dataProcessorOutputs.get(i).getSpeedLimit());
            smooth[i] = new DataPoint(i,(dataProcessorOutputs.get(i).getLon() + dataProcessorOutputs.get(i).getLat())/2);
        }

        LineGraphSeries<DataPoint> speedData = new LineGraphSeries<DataPoint>(speeds);
        Paint blue = new Paint();
        blue.setColor(Color.BLUE);
        blue.setStyle(Paint.Style.STROKE);
        blue.setStrokeWidth(10);
        speedData.setCustomPaint(blue);

        graphView.addSeries(speedData);

        LineGraphSeries<DataPoint> limitData = new LineGraphSeries<DataPoint>(limits);

        Paint red = new Paint();
        red.setColor(Color.RED);
        red.setStyle(Paint.Style.STROKE);
        red.setStrokeWidth(10);

        limitData.setCustomPaint(red);

        graphView.addSeries(limitData);

        graphView.setTitle("Speed vs Time");

        GridLabelRenderer gridLabel = graphView.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Time");
        gridLabel.setVerticalAxisTitle("Speed");


        TextView numAcceleration = root.findViewById(R.id.numAcceleration);
        int accel = (driveQualityOutput.getNumAcceleration()+driveQualityOutput.getNumBreaks())/4;
        if(accel <0) accel = 0;
        numAcceleration.setText("Number of sudden accelerations - "+String.valueOf(accel));

        TextView numSuddenTurn = root.findViewById(R.id.numsuddenturn);
        int sharp = driveQualityOutput.getNumSharpTurns() - DriveQualityProcessor.LAT_NUM_DATA_POINT;
        if(sharp<0) sharp = 0;
        numSuddenTurn.setText("Number of sharp risky turns - "+String.valueOf(sharp));

        TextView numBreak = root.findViewById(R.id.numBreak);
        numBreak.setText("Number of sudden breaks - "+String.valueOf(accel));

        GraphView smoothGraph = root.findViewById(R.id.smoothGraph);

        LineGraphSeries<DataPoint> smoothData = new LineGraphSeries<DataPoint>(smooth);
        Paint yellow = new Paint();
        yellow.setColor(Color.YELLOW);
        yellow.setStyle(Paint.Style.STROKE);
        yellow.setStrokeWidth(10);
        speedData.setCustomPaint(yellow);

        smoothGraph.addSeries(smoothData);

        smoothGraph.setTitle("Smoothness of Drive vs Time");


        TextView score = root.findViewById(R.id.scoreEarned);

        String prefix = "";

        if(driveQualityOutput.getScore() > 0) {
            prefix = "+";
        }

        score.setText(prefix+String.valueOf(driveQualityOutput.getScore()));

        GridLabelRenderer gridLabel1 = smoothGraph.getGridLabelRenderer();
        gridLabel1.setHorizontalAxisTitle("Time");
        gridLabel1.setVerticalAxisTitle("Smoothness");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference databaseReference = database.getReference("userScores/"+ FirebaseAuth.getInstance().getCurrentUser().getUid()+"/score");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int result = 0;
                if(!dataSnapshot.exists()){
                    result = 0;
                }else{
                    result += Integer.parseInt(dataSnapshot.getValue(String.class));
                }
                result+=driveQualityOutput.getScore();
                UserScore userScore = new UserScore();
                userScore.setScore(String.valueOf(result));
                userScore.setName(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                userScore.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());
                databaseReference.getParent().setValue(userScore);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        TextView textView = root.findViewById(R.id.textView4);
        if(!isCharity) textView.setVisibility(View.GONE);

        TextView donate = root.findViewById(R.id.donate);
        if(isCharity){
            int m = ((100 - driveQualityOutput.getAmountMultiplier())*amount)/100;
            //donate.setText(String.valueOf((driveQualityOutput.getAmountMultiplier()*amount)/100)+"$");
            donate.setText(String.valueOf(m)+"$");
        }else{
            donate.setVisibility(View.GONE);
        }

        return root;
    }

    public ArrayList<DataProcessorOutput> dataProcessorOutputs;
    public DriveQualityProcessor driveQualityProcessor;
    public DriveProcessorState driveProcessorState;
    public boolean isCharity;
    public int amount;

}
