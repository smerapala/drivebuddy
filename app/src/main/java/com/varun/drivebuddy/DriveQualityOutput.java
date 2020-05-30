package com.varun.drivebuddy;

public class DriveQualityOutput {
    public String overallResult;
    public int percentFollowedSpeedLimit;
    public int percentFollowedRPM;
    public int score;
    public int amountMultiplier;
    public int numAcceleration;
    public int numBreaks;
    public int numSharpTurns;

    public String getOverallResult() {
        return overallResult;
    }

    public void setOverallResult(String overallResult) {
        this.overallResult = overallResult;
    }

    public int getPercentFollowedSpeedLimit() {
        return percentFollowedSpeedLimit;
    }

    public void setPercentFollowedSpeedLimit(int percentFollowedSpeedLimit) {
        this.percentFollowedSpeedLimit = percentFollowedSpeedLimit;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getNumAcceleration() {
        return numAcceleration;
    }

    public void setNumAcceleration(int numAcceleration) {
        this.numAcceleration = numAcceleration;
    }

    public int getNumBreaks() {
        return numBreaks;
    }

    public void setNumBreaks(int numBreaks) {
        this.numBreaks = numBreaks;
    }

    public int getNumSharpTurns() {
        return numSharpTurns;
    }

    public void setNumSharpTurns(int numSharpTurns) {
        this.numSharpTurns = numSharpTurns;
    }

    public int getPercentFollowedRPM() {
        return percentFollowedRPM;
    }

    public void setPercentFollowedRPM(int percentFollowedRPM) {
        this.percentFollowedRPM = percentFollowedRPM;
    }

    public int getAmountMultiplier() {
        return amountMultiplier;
    }

    public void setAmountMultiplier(int amountMultiplier) {
        this.amountMultiplier = amountMultiplier;
    }
}
