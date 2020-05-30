package com.varun.drivebuddy;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.telephony.SmsManager;
import android.view.View;
import android.widget.Toast;

public class CrashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("person/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

        final String message = getIntent().getExtras().getString("msg");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    smsManager = SmsManager.getDefault();
                    if(message==null || message.isEmpty()){
                        smsManager.sendTextMessage(dataSnapshot.getValue(String.class), null, "Hi. This is a message from Drive Buddy. " + FirebaseAuth.getInstance().getCurrentUser().getDisplayName() + " was driving " +
                               "and might have met with an accident. This is the emergency contact listed.", null, null);
                    }else {
                        smsManager.sendTextMessage(dataSnapshot.getValue(String.class), null, message, null, null);
                    }
                }else{
                    Toast.makeText(CrashActivity.this, "Error sending message!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public SmsManager smsManager;

}
