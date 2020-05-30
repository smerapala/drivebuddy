package com.varun.drivebuddy;

public class DriveProcessorState {
    public int numSharpTurns = 0;
    public int numAccelerations = 0;
    public int numBreaks = 0;

    public int getNumSharpTurns() {
        return numSharpTurns;
    }

    public void setNumSharpTurns(int numSharpTurns) {
        this.numSharpTurns = numSharpTurns;
    }

    public int getNumAccelerations() {
        return numAccelerations;
    }

    public void setNumAccelerations(int numAccelerations) {
        this.numAccelerations = numAccelerations;
    }

    public int getNumBreaks() {
        return numBreaks;
    }

    public void setNumBreaks(int numBreaks) {
        this.numBreaks = numBreaks;
    }
}
