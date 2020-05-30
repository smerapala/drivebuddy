package com.varun.drivebuddy;

public class DataProcessor {

    DataProcessor(){

    }

    public DataProcessorOutput processData(String data){
        String[] parsedRow = data.split(";",-1);

        DataProcessorOutput dataProcessorOutput = new DataProcessorOutput();

        dataProcessorOutput.setEngineRPM(Integer.parseInt(parsedRow[25]));
        dataProcessorOutput.setSpeed(Integer.parseInt(parsedRow[26]));
        dataProcessorOutput.setSpeedLimit(Integer.parseInt(parsedRow[29]));

        dataProcessorOutput.setLat(Integer.parseInt(parsedRow[19]));
        dataProcessorOutput.setHt(Integer.parseInt(parsedRow[20]));
        dataProcessorOutput.setLon(Integer.parseInt(parsedRow[18]));

        dataProcessorOutput.setActual_lat(parsedRow[7]);
        dataProcessorOutput.setActual_lon(parsedRow[8]);

        return dataProcessorOutput;
    }

}
