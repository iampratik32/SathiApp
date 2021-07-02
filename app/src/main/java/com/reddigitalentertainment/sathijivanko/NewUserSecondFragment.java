package com.reddigitalentertainment.sathijivanko;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NewUserSecondFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_new_user_second,container,false);

        Fragment firstDetail = new FirstEnterDetail_Fragment();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().setCustomAnimations(R.anim.right_to_left,R.anim.exit_right,R.anim.left_to_right,R.anim.exit_left)
                .replace(R.id.frameLayout_newUserSecondFragment,firstDetail)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();

        return view;
    }

}
