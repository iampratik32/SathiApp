package com.reddigitalentertainment.sathijivanko;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainConnectionsFragment extends Fragment {
    private BottomNavigationView bottomNavigationView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main_connections, container, false);

        bottomNavigationView = view.findViewById(R.id.navigator_connections);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationListener);
        bottomNavigationView.setSelectedItemId(R.id.dislikesButton);



        return view;
    }


    public BottomNavigationView.OnNavigationItemSelectedListener navigationListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;
            switch (menuItem.getItemId()){
                case R.id.likesButton:
                    selectedFragment = new LikesFragment();
                    break;

                case R.id.dislikesButton:
                    selectedFragment = new DislikesFragment();
                    break;
            }
            getFragmentManager().beginTransaction().replace(R.id.frameLayout_connections, selectedFragment).commit();

            return true;
        }
    };


}
