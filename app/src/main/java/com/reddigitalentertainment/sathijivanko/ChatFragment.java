package com.reddigitalentertainment.sathijivanko;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


public class ChatFragment extends Fragment {

    View view;
        @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

            view = inflater.inflate(R.layout.fragment_chat, container, false);

            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.frameLayout_chatFragment,new Chat_ChatFragment());
            fragmentTransaction.commit();


            final LinearLayout chatIcon = view.findViewById(R.id.chatButton_chatFragment);
            final LinearLayout feedIcon = view.findViewById(R.id.feedButton_chatFragment);
            final TextView chatText = view.findViewById(R.id.chatText_chatFragment);
            final TextView feedText = view.findViewById(R.id.feedText_chatFragment);

            chatIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction fragmentTransaction1 = getFragmentManager().beginTransaction();
                    fragmentTransaction1.replace(R.id.frameLayout_chatFragment,new Chat_ChatFragment());
                    fragmentTransaction1.addToBackStack(null);
                    fragmentTransaction1.commit();
                    chatText.setTextColor(Color.parseColor("#F38F76"));
                    feedText.setTextColor(Color.parseColor("#544643"));
                }
            });

            feedIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentTransaction fragmentTransaction1 = getFragmentManager().beginTransaction();
                    fragmentTransaction1.replace(R.id.frameLayout_chatFragment,new Chat_FeedFragment());
                    fragmentTransaction1.addToBackStack(null);
                    fragmentTransaction1.commit();
                    feedText.setTextColor(Color.parseColor("#F38F76"));
                    chatText.setTextColor(Color.parseColor("#544643"));
                }
            });


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        HideKeyboard.hideFragment(getActivity(),view);
    }
}
