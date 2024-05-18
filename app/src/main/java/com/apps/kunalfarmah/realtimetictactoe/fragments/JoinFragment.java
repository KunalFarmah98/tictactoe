package com.apps.kunalfarmah.realtimetictactoe.fragments;


import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.apps.kunalfarmah.realtimetictactoe.activity.OnlineActivity;
import com.example.kunalfarmah.realtimetictactoe.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


/**
 * A simple {@link Fragment} subclass.
 */
public class JoinFragment extends Fragment {


    FirebaseDatabase mDatabase;
    DatabaseReference mref, awayRef, diffRef;
    FirebaseUser user;
    RadioButton d1, d2, d3;
    int difficulty;
    boolean start;

    EditText token;
    Button play;

    public JoinFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_join, container, false);
        token = v.findViewById(R.id.token1);
        play = v.findViewById(R.id.play);

        d1 = v.findViewById(R.id.d1);
        d2 = v.findViewById(R.id.d2);
        d3 = v.findViewById(R.id.d3);


        d1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                difficulty = 1;
            }
        });


        d2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                difficulty = 2;
            }
        });


        d3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                difficulty = 3;
            }
        });


        mDatabase = FirebaseDatabase.getInstance();
        mref = mDatabase.getReference("Code");
        diffRef = mDatabase.getReference("Difficulty");

        //final String val = token.getText().toString();

        // storing the token in the db


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                try {
                    if (difficulty == 0) {
                        Toast.makeText(getContext(), "Please Select a Difficulty Level", Toast.LENGTH_SHORT).show();
                        return;
                    }



                    diffRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int diff = dataSnapshot.getValue(int.class);

                            try {
                                if (diff != difficulty) {
                                    start = false;
                                    Toast.makeText(getContext(), "Difficulties Don't Match!!", Toast.LENGTH_SHORT).show();
                                } else
                                    start = true;
                            }
                            catch(Exception e) {
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }

                    });


                    // Read from the database
                    mref.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            // This method is called once with the initial value and again
                            // whenever data at this location is updated.

                            if (start) {
                                try {
                                    String value = (String) dataSnapshot.getValue();
                                    Log.d("value", value);


                                    if (token.getText().toString().equals(value)) {
                                        Toast.makeText(getContext(), "Paired", Toast.LENGTH_SHORT).show();

                                        // updating code as play which will in turn start the game for friend and starting game for urself

                                        mref.setValue("Play");

                                        Intent start = new Intent(getContext(), OnlineActivity.class);
                                        start.putExtra("token",token.getText().toString());
//                                    start.setFlags(FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                        start.putExtra("isHost", "False");
                                        start.putExtra("difficulty", difficulty);
                                        startActivity(start);
                                        getActivity().finish();


                                    } else if (!token.getText().toString().equals(value) && !value.equalsIgnoreCase("Play")) {
                                        Toast.makeText(getContext(), "Please Enter Correct Code", Toast.LENGTH_SHORT).show();
                                    }
                                    //Log.d(TAG, "Value is: " + value);
                                } catch (Exception e) {
                                }
                            } else {

                            }
                        }


                        @Override
                        public void onCancelled(DatabaseError error) {
                            // Failed to read value
                            // Log.w(TAG, "Failed to read value.", error.toException());
                        }
                    });

                    user = FirebaseAuth.getInstance().getCurrentUser();
                    awayRef = mDatabase.getReference("AwayName");
                    awayRef.setValue(user.getDisplayName());


                }
                catch (Exception e){
                }
            }
        });


        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //   awayRef.removeValue();
        //    mref.removeValue();
        //mDatabase.goOffline();
    }

    @Override
    public void onStop() {
        super.onStop();
        //  awayRef.removeValue();
        // mref.removeValue();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        // awayRef.removeValue();
        //    mref.removeValue();

    }

    @Override
    public void onResume() {
        super.onResume();
        mDatabase.goOnline();
    }
}

