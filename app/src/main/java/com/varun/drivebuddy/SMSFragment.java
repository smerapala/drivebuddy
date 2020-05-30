package com.varun.drivebuddy;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class SMSFragment extends Fragment {


    public SMSFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_sm, container, false);

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("person/" + FirebaseAuth.getInstance().getCurrentUser().getUid());

        final TextView textView = root.findViewById(R.id.textView2);
        final EditText editText = root.findViewById(R.id.phonenumber);
        Button button = root.findViewById(R.id.sendphonenum);

        textView.setText("Phone Number not set");

        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (textView == null) return;
                if (dataSnapshot.exists()) {
                    textView.setText(dataSnapshot.getValue(String.class));
                } else {
                    textView.setText("Phone Number not set");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        databaseReference.addValueEventListener(valueEventListener);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = editText.getEditableText().toString();
                if (!phoneNumber.isEmpty() && phoneNumber.matches("[0-9]+")
                        && phoneNumber.length() == 10) {
                    databaseReference.setValue(phoneNumber);
                }else{
                    Toast.makeText(getContext(), "Please enter a valid phone number!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

}
