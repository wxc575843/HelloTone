package com.example.wxc575843.hellotone.Practice;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.wxc575843.hellotone.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class Practice extends Fragment {


    public Practice() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_practice, container, false);

        ImageButton btn_life = (ImageButton) view.findViewById(R.id.btn_practice_life);
        btn_life.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

        return view;

    }



}
