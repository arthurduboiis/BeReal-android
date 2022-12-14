package com.example.bereal;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;


public class TopBar extends Fragment {

    View view;
    ImageButton imageFriendBtn;
    ImageButton imageProfileBtn;
    TextView berealCenter;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container ,Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        view = inflater.inflate(R.layout.fragment_top_bar,container, false );

        imageFriendBtn = (ImageButton) view.findViewById(R.id.btn_friends);
        imageProfileBtn = (ImageButton) view.findViewById(R.id.btn_profile);
        berealCenter = (TextView) view.findViewById(R.id.bereal_text_center);

        imageProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent( getActivity().getApplicationContext(),UserActivity.class));
            }
        });

        imageFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity().getApplicationContext(), FriendsActivity.class));
            }
        });

        berealCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity().getApplicationContext(), MainActivity.class));
            }
        });


        return view;
    }


}