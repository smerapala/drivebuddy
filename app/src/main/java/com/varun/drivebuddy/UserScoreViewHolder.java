package com.varun.drivebuddy;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserScoreViewHolder extends RecyclerView.ViewHolder{
    public TextView name;
    public TextView score;
    public UserScoreViewHolder(@NonNull View itemView) {
        super(itemView);
        name = itemView.findViewById(R.id.name);
        score = itemView.findViewById(R.id.score);
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    public void setScore(String score) {
        this.score.setText(score);
    }
}
