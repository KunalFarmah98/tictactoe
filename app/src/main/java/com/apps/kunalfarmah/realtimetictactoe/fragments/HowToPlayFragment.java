package com.apps.kunalfarmah.realtimetictactoe.fragments;


import android.os.Bundle;

import androidx.annotation.Keep;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kunalfarmah.realtimetictactoe.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class HowToPlayFragment extends Fragment {

    @Keep
    public HowToPlayFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_how_to_play, container, false);
    }

}
