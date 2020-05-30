package com.varun.drivebuddy;

public class DataProcessorOutput {
    int speed;
    int speedLimit;
    int engineRPM;
    int lon;
    int ht;
    int lat;
    String actual_lat;
    String actual_lon;

    public DataProcessorOutput() {
    }

    public DataProcessorOutput(int speed, int speedLimit, int engineRPM) {
        this.speed = speed;
        this.speedLimit = speedLimit;
        this.engineRPM = engineRPM;
    }

    public int getLon() {
        return lon;
    }

    public void setLon(int lon) {
        this.lon = lon;
    }

    public int getHt() {
        return ht;
    }

    public void setHt(int ht) {
        this.ht = ht;
    }

    public int getLat() {
        return lat;
    }

    public void setLat(int lat) {
        this.lat = lat;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(int speedLimit) {
        this.speedLimit = speedLimit;
    }

    public int getEngineRPM() {
        return engineRPM;
    }

    public void setEngineRPM(int engineRPM) {
        this.engineRPM = engineRPM;
    }

    public String getActual_lat() {
        return actual_lat;
    }

    public void setActual_lat(String actual_lat) {
        this.actual_lat = actual_lat;
    }

    public String getActual_lon() {
        return actual_lon;
    }

    public void setActual_lon(String actual_lon) {
        this.actual_lon = actual_lon;
    }
}
