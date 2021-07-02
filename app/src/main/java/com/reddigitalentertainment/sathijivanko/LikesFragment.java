package com.reddigitalentertainment.sathijivanko;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class LikesFragment extends Fragment {
    private RecyclerView recyclerView;
    private LikesAdapter likesAdapter;
    private ProgressBar progressBar;
    private RecyclerView.LayoutManager layoutManager;
    private List<PotentialMatchesInfo> userList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_likes, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_likes);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        progressBar = view.findViewById(R.id.likesFragmentPB);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        likesAdapter = new LikesAdapter(userList,getContext(),"L");
        likesAdapter.setHasStableIds(true);
        DatabaseReference likesDb = FirebaseDatabase.getInstance().getReference().child("Users").child(SathiUserHolder.getSathiUser().getUserId()).child("Matches");
        likesDb.child("Likes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot post:dataSnapshot.getChildren()){
                        String key = post.getKey();
                        String date = post.getValue().toString();
                        likesDb.child("Matched").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(!dataSnapshot.exists()){
                                    DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(key);
                                    userDb.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()){
                                                PotentialMatchesInfo pUser = new PotentialMatchesInfo(key,dataSnapshot.child("Name").getValue().toString()
                                                        ,dataSnapshot.child("Bio").getValue().toString(),dataSnapshot.child("Age").getValue().toString(),
                                                        dataSnapshot.child("ShowAge").getValue().toString(),dataSnapshot.child("Gender").getValue().toString(),
                                                        dataSnapshot.child("Verified").getValue().toString());
                                                pUser.setDisplayPicture(dataSnapshot.child("DisplayPicture").getValue().toString());
                                                pUser.setMatchedDate(date);
                                                userList.add(pUser);
                                                likesAdapter.notifyDataSetChanged();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recyclerView.setAdapter(likesAdapter);

        androidx.appcompat.widget.SearchView searchView  = view.findViewById(R.id.searchView_likeFragment);
        searchView.setQueryHint("Search Users that Liked You");
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                likesAdapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                likesAdapter.filter(newText);
                return true;
            }
        });

        CountDownTimer timer = new CountDownTimer(3000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                SathiUserHolder.getSathiUser().setUserLikes(userList);
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }.start();

        return view;
    }


}
