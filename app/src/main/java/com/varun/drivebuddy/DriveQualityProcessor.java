package com.varun.drivebuddy;

import android.widget.Toast;

import java.util.ArrayList;

public class DriveQualityProcessor {

    public DriveQualityProcessor(DriveProcessorState driveProcessorState){
        numBreaks = driveProcessorState.getNumBreaks();
        numSharpTurns = driveProcessorState.getNumSharpTurns();
        numAccelerations = driveProcessorState.getNumAccelerations();
    }

    public String getOutput(ArrayList<DataProcessorOutput> dataProcessorOutputs){
        DataProcessorOutput dataProcessorOutput = dataProcessorOutputs.get(dataProcessorOutputs.size() - 1);
        String output = "";

        int front = checkIsDanger(dataProcessorOutputs, dataProcessorOutputs.size() - 1, 1);
        int right = checkIsDanger(dataProcessorOutputs, dataProcessorOutputs.size() - 1, 2);
        int ht = checkIsDanger(dataProcessorOutputs, dataProcessorOutputs.size() - 1, 3);

        if(ht!=0){
            boolean sendSMS = false;
            if(checkIsDanger(dataProcessorOutputs, dataProcessorOutputs.size() - HEIGHT_NUM_DATA_POINT - 2, 3)!=0){
                sendSMS = true;
            }
            if(front!=0 && right!=0){
                sendSMS = true;
            }

            if(sendSMS){
                return "SEND SMS!";
            }
        }

        if(checkIsLastZeros(dataProcessorOutputs,dataProcessorOutputs.size() - 1, LAST_FEW_ZEROS)){

            for(int dim = 1;dim<=3;dim++) {
                if (checkIsDanger(dataProcessorOutputs, dataProcessorOutputs.size() - 10, dim)!=0){
                    return "SEND SMS!";
                }
            }
        }

        if(front!=0){
            if(front>0){
                output = "Please do not accelerate so fast!";
                numAccelerations++;
            }else{
                output = "Please do not brake so fast!";
                numBreaks++;
            }
        }else if(right!=0){
            output = "Please do not make such a sharp turn!";
            numSharpTurns++;
        }else if(dataProcessorOutput.getSpeed() - dataProcessorOutput.getSpeedLimit() > 0){
            int diff = dataProcessorOutput.getSpeed() - dataProcessorOutput.getSpeedLimit();
            if(diff <= RANGE_ALLOWED_TO_EXCEED_SPEED_LIMIT){
                output = "You are starting to exceed the speed limit. Consider slowing down.";
            }else if(diff <= 3* RANGE_ALLOWED_TO_EXCEED_SPEED_LIMIT){
                output = "You are exceeding the speed limit. Please slow down.";
            }else{
                output = "You are drastically over the speed limit. Please slow down immediately.";
            }
        }else if(dataProcessorOutput.getEngineRPM() > LIMIT_FOR_RPM){
            if(dataProcessorOutput.getEngineRPM() < LIMIT_FOR_RPM + 1000) {
                output = "Your engine's RPM is quite high. Please be gentle on the accelerator.";
            }else{
                output = "Your engines RPM is very high! Please be gentle on the accelerator!";
            }
        }
        return output;
    }

    public DriveQualityOutput computeDriveQuality(ArrayList<DataProcessorOutput> dataProcessorOutputs){
        int timesExceededSpeedLimit = 0;

        int timesExceededRPM = 0;

        int totalTimes = dataProcessorOutputs.size();
        for(DataProcessorOutput dataProcessorOutput : dataProcessorOutputs) {
            if (dataProcessorOutput.getSpeed() - dataProcessorOutput.getSpeedLimit() > 0) {
                timesExceededSpeedLimit++;
            }
            if (dataProcessorOutput.getEngineRPM() - LIMIT_FOR_RPM > 0) {
                timesExceededRPM++;
            }
        }


        int percentOfTimeSpeeding = timesExceededSpeedLimit*100/totalTimes;
        int percentOfTimeExceedingRPM = timesExceededRPM*100/totalTimes;

        String result;

        if(percentOfTimeSpeeding<10){
            result = "Great job! You drove really well!";
        }else if(percentOfTimeSpeeding < 30){
            result  = "Good job! You drove well!";
        }else if(percentOfTimeSpeeding < 70){
            result = "You were speeding. Let's try to go a bit slower next time!";
        }else{
            result = "You were speeding a lot! Please try to go slower next time!";
        }

        int accel = (numAccelerations+numBreaks)/4;
        if(accel <0) accel = 0;

        int sharp = numSharpTurns - DriveQualityProcessor.LAT_NUM_DATA_POINT;
        if(sharp<0) sharp = 0;

        float smoothnessScore = 0;
        smoothnessScore+=accel;
        smoothnessScore+=sharp;
        smoothnessScore/=dataProcessorOutputs.size();

        smoothnessScore = 1 - smoothnessScore;

        float score = dataProcessorOutputs.size()*5;

        if(percentOfTimeSpeeding<10){
            //Skip penalty
        }else if(percentOfTimeSpeeding<30){
            score*=0.9;
        }else if(percentOfTimeSpeeding<70){
            score*=0.3;
        }else{
            score = -dataProcessorOutputs.size()*2;
        }

        if(smoothnessScore>0.9){
            score*=1.5;
        }else if(smoothnessScore>70){
            score*=1.2;
        }else if(smoothnessScore>50){
            //Skip penalty
        }else{
            score*=0.7;
        }

        score*=5;

        int amountScore = 0;
        amountScore+=(100 - percentOfTimeSpeeding);
        amountScore+=(100*smoothnessScore);
        amountScore/=2;
        amountScore = 100 - amountScore;

        DriveQualityOutput driveQualityOutput = new DriveQualityOutput();

        driveQualityOutput.setOverallResult(result);
        driveQualityOutput.setPercentFollowedSpeedLimit(100 - percentOfTimeSpeeding);
        driveQualityOutput.setPercentFollowedRPM(100 - percentOfTimeExceedingRPM);
        driveQualityOutput.setNumSharpTurns(numSharpTurns);
        driveQualityOutput.setNumBreaks(numBreaks);
        driveQualityOutput.setNumAcceleration(numAccelerations);
        driveQualityOutput.setScore((int) score);
        driveQualityOutput.setAmountMultiplier(amountScore);

        return driveQualityOutput;
    }

    int checkIsDanger(ArrayList<DataProcessorOutput> dataProcessorOutputs, int i, int dimension){
        if(i<=0) return 0;
        int danger;
        int num;
        if(dimension == 1){
            danger = LON_DATA_POINT_DANGER;
            num = LON_NUM_DATA_POINTS;
        }else if(dimension == 2){
            danger = LAT_DATA_POINT_DANGER;
            num = LAT_NUM_DATA_POINT;
        }else{
            danger = HEIGHT_DATA_POINT_DANGER;
            num = HEIGHT_NUM_DATA_POINT;
        }

        int t = i - num;

        while(t<i){
            if(t>=0) {
                if (dimension == 1) {
                    int r = dataProcessorOutputs.get(i).getLon() - dataProcessorOutputs.get(t).getLon();
                    if(Math.abs(r) >= danger){
                        if(r>0) return 1;
                        else return -1;
                    }
                }else if(dimension == 2){
                    int r = dataProcessorOutputs.get(i).getLat() - dataProcessorOutputs.get(t).getLat();
                    if(Math.abs(r) >= danger){
                        if(r>0) return 1;
                        else return -1;
                    }
                }else{
                    int r = dataProcessorOutputs.get(i).getHt() - dataProcessorOutputs.get(t).getHt();
                    if(Math.abs(r) >= danger){
                        if(r>0) return 1;
                        else return -1;
                    }
                }
            }
            t++;
        }

        return 0;
    }

    public DriveProcessorState getDriveProcessorState(){
        DriveProcessorState driveProcessorState = new DriveProcessorState();
        driveProcessorState.setNumAccelerations(numAccelerations);
        driveProcessorState.setNumSharpTurns(numSharpTurns);
        driveProcessorState.setNumBreaks(numBreaks);
        return driveProcessorState;
    }

    boolean checkIsLastZeros(ArrayList<DataProcessorOutput> dataProcessorOutputs, int i, int n){
        int t = i - n;
        if(t<0) return false;
        for(;t<=i;t++){
            if(dataProcessorOutputs.get(t).getSpeed() == 0){
                continue;
            }else{
                return false;
            }
        }
        return true;
    }


    public static int RANGE_ALLOWED_TO_EXCEED_SPEED_LIMIT = 5;
    public static int LIMIT_FOR_RPM = 6000;




    public static int LON_DATA_POINT_DANGER = 150;
    public static int LON_NUM_DATA_POINTS = 3;





    public static int LAT_DATA_POINT_DANGER = 170;
    public static int LAT_NUM_DATA_POINT = 3;



    public static int HEIGHT_DATA_POINT_DANGER = 120;
    public static int HEIGHT_NUM_DATA_POINT = 3;

    public int LAST_FEW_ZEROS = 10;

    public int numSharpTurns;
    public int numAccelerations;
    public int numBreaks;
}
