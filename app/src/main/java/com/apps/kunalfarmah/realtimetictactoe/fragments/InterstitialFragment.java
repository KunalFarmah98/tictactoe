package com.apps.kunalfarmah.realtimetictactoe.fragments;


import android.app.Activity;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.apps.kunalfarmah.realtimetictactoe.activity.EnterActivity;
import com.example.kunalfarmah.realtimetictactoe.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class InterstitialFragment extends Fragment {

    Button host;
    Button join;
    EnterActivity act;


    public InterstitialFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_interstitial, container, false);


        host = v.findViewById(R.id.host);
        join = v.findViewById(R.id.join);



        host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HostFragment host = new HostFragment();
                act.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,host).commit();
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JoinFragment join = new JoinFragment();
                act.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_containter,join).commit();
            }
        });




        return v;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        act = (EnterActivity)activity;
    }

}
