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


public class DislikesFragment extends Fragment {

    private RecyclerView recyclerView;
    private LikesAdapter dislikesAdapter;
    ProgressBar progressBar;
    private RecyclerView.LayoutManager layoutManager;
    private List<PotentialMatchesInfo> userList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_dislikes, container, false);

        recyclerView = view.findViewById(R.id.recyclerView_dislikes);
        recyclerView.setNestedScrollingEnabled(true);
        progressBar = view.findViewById(R.id.dislikesFragmentPB);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        dislikesAdapter = new LikesAdapter(userList,getContext(),"D");
        dislikesAdapter.setHasStableIds(true);
        TextView dislikeTextView = view.findViewById(R.id.dislikeTexView);
        dislikeTextView.setText("Users You Disliked from "+SathiUserHolder.getSathiUser().getLocation());

        DatabaseReference locationDb = FirebaseDatabase.getInstance().getReference().child("Locations").child(SathiUserHolder.getSathiUser().getLookingIn());
        locationDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot post:dataSnapshot.getChildren()){
                        String userId = post.getKey();
                        DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(userId);
                        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.child("Matches").child("Dislikes").child(SathiUserHolder.getSathiUser().getUserId()).exists()){
                                    String date = dataSnapshot.child("Matches").child("Dislikes").child(SathiUserHolder.getSathiUser().getUserId()).getValue().toString();
                                    PotentialMatchesInfo pUser = new PotentialMatchesInfo(userId,dataSnapshot.child("Name").getValue().toString()
                                            ,dataSnapshot.child("Bio").getValue().toString(),dataSnapshot.child("Age").getValue().toString(),
                                            dataSnapshot.child("ShowAge").getValue().toString(),dataSnapshot.child("Gender").getValue().toString(),
                                            dataSnapshot.child("Verified").getValue().toString());
                                    pUser.setDisplayPicture(dataSnapshot.child("DisplayPicture").getValue().toString());
                                    pUser.setMatchedDate(date);
                                    userList.add(pUser);
                                    dislikesAdapter.notifyDataSetChanged();
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

        recyclerView.setAdapter(dislikesAdapter);

        androidx.appcompat.widget.SearchView searchView  = view.findViewById(R.id.searchView_dislikeFragment);
        searchView.setQueryHint("Search Disliked Users");
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                dislikesAdapter.filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                dislikesAdapter.filter(newText);
                return true;
            }
        });

        CountDownTimer timer = new CountDownTimer(3000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                SathiUserHolder.getSathiUser().setUserDislikes(userList);
                recyclerView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);

            }
        }.start();

        return view;
    }

}
