package com.varun.drivebuddy;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class CharityFragment extends Fragment {


    public CharityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_charity, container, false);

        final EditText editText = root.findViewById(R.id.editText);
        Button button  = root.findViewById(R.id.button);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount = editText.getEditableText().toString();
                if(!amount.isEmpty() && amount.matches("[0-9]+") && Integer.parseInt(amount) <= 20){
                    DriveModeFragment driveModeFragment = new DriveModeFragment(true, Integer.parseInt(amount));
                    openFragment(driveModeFragment);
                }else{
                    Toast.makeText(getContext(), "Please enter a valid dollar amount less than 20", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(((ViewGroup) getView().getParent()).getId(), fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
