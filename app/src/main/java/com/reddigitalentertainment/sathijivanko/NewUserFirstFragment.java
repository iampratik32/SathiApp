package com.reddigitalentertainment.sathijivanko;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class NewUserFirstFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_new_user_first, container, false);
        Button nextButton = view.findViewById(R.id.yupButtonWelcomePage);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment newFragment = new NewUserSecondFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().setCustomAnimations(R.anim.right_to_left,R.anim.exit_right,R.anim.left_to_right,R.anim.exit_left)
                        .replace(R.id.firstTimeUsers_frameLayout,newFragment).addToBackStack(null)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
            }
        });
        return view;
    }


}
