package com.reddigitalentertainment.sathijivanko;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class    Chat_FeedFragment extends Fragment {

    private RecyclerView feedRecyclerView;
    private RecyclerView.Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressBar progressBar;
    int forDupCounter=0;
    int currentSize =0;
    private ArrayList<Feed_Class> feedUsers = new ArrayList<>();
    boolean isLoading = false;
    private int totalShowing;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_chat__feed, container, false);

        feedRecyclerView = view.findViewById(R.id.feedRecyclerView_chatFragment);
        feedRecyclerView.setNestedScrollingEnabled(true);
        feedRecyclerView.setHasFixedSize(true);
        feedRecyclerView.setItemViewCacheSize(20);
        feedRecyclerView.setDrawingCacheEnabled(true);
        feedRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        layoutManager = new LinearLayoutManager(getContext());
        feedRecyclerView.setLayoutManager(layoutManager);
        progressBar = view.findViewById(R.id.progressbar_chatFeed);

        DatabaseReference userFeed = FirebaseDatabase.getInstance().getReference().child("Feeds").child(SathiUserHolder.getSathiUser().getUserId());
        userFeed.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    LoadFeedInformation loadFeedInformation = new LoadFeedInformation();
                    loadFeedInformation.execute(dataSnapshot);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        return view;
    }

    private List<Feed_Class> getMatches() {
        progressBar.setVisibility(View.GONE);
        Collections.reverse(feedUsers);
        return feedUsers;
    }

    private class LoadFeedInformation extends AsyncTask<DataSnapshot,Integer,Integer>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Integer integer) {
            feedRecyclerView.setVisibility(View.VISIBLE);
        }

        @Override
        protected Integer doInBackground(final DataSnapshot... dataSnapshots) {
            readData(new onGetDataListener() {
                @Override
                public void onSuccess(List<Feed_Class> list) {
                    adapter = new Feed_PotentailUserAdapter(getMatches(), getContext());
                    adapter.setHasStableIds(true);
                    feedRecyclerView.setAdapter(adapter);
                }

                @Override
                public void onStart() {

                }

                @Override
                public void onFailure() {

                }
            });


            return null;
        }
    }

    public interface onGetDataListener{
        void onSuccess(List<Feed_Class> list);
        void onStart();
        void onFailure();
    }

    public void readData(final Chat_FeedFragment.onGetDataListener listener){
        listener.onStart();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Feeds").child(SathiUserHolder.getSathiUser().getUserId());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot post: dataSnapshot.getChildren()){
                        if(post.exists()){
                            final Feed_Class feedClass = new Feed_Class(post.child("User").getValue().toString(),(long)post.child("Time").getValue(),post.child("What").getValue().toString());
                            DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(feedClass.getUserId());
                            userDb.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        feedClass.setUserName(dataSnapshot.child("Name").getValue().toString());
                                        feedClass.setBio(dataSnapshot.child("Bio").getValue().toString());
                                        feedClass.setPreference(dataSnapshot.child("Preference").getValue().toString());
                                        feedClass.setAge(dataSnapshot.child("Age").getValue().toString());
                                        feedClass.setShowAge(dataSnapshot.child("ShowAge").getValue().toString());
                                        feedClass.setVerified(dataSnapshot.child("Verified").getValue().toString());
                                        feedClass.setDisplayPicture(dataSnapshot.child("DisplayPicture").getValue().toString());
                                        if(dataSnapshot.child("Matches").child("Matched").child(SathiUserHolder.getSathiUser().getUserId()).exists()){
                                            feedClass.setMatched("True");
                                        }
                                        else {
                                            feedClass.setMatched("False");
                                        }
                                        if(!feedUsers.contains(feedClass)){
                                            if(feedUsers.size()<4){
                                                feedUsers.add(feedClass);
                                                totalShowing++;
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }
                    }
                    CountDownTimer countDownTimer = new CountDownTimer(2000,1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            listener.onSuccess(feedUsers);
                            initScrollListener();
                        }
                    }.start();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initScrollListener(){
        feedRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == feedUsers.size() - 1) {
                        loadMore();
                        isLoading = true;
                    }
                }
            }
        });
    }

    private void loadMore() {
        feedUsers.add(null);
        adapter.notifyItemInserted(feedUsers.size() - 1);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                feedUsers.remove(feedUsers.size()-1);
                int scrollPosition = feedUsers.size();
                adapter.notifyItemRemoved(scrollPosition);
                int currentSize = scrollPosition;
                int nextLimit = currentSize + 4;

                readMoreData(nextLimit, new onGetDataListener() {
                    @Override
                    public void onSuccess(List<Feed_Class> list) {
                        adapter.notifyDataSetChanged();
                        isLoading = false;
                    }

                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onFailure() {

                    }
                });

            }
        },2000);

    }

    public void readMoreData(final int nextLimit, final Chat_FeedFragment.onGetDataListener listener){
        listener.onStart();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Feeds").child(SathiUserHolder.getSathiUser().getUserId());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot post: dataSnapshot.getChildren()){
                        if(post.exists()){
                            final Feed_Class feedClass = new Feed_Class(post.child("User").getValue().toString(),(long)post.child("Time").getValue(),post.child("What").getValue().toString());
                            DatabaseReference userDb = FirebaseDatabase.getInstance().getReference().child("Users").child(feedClass.getUserId());
                            userDb.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){
                                        if(feedUsers.size()<totalShowing){
                                            feedClass.setUserName(dataSnapshot.child("Name").getValue().toString());
                                            feedClass.setBio(dataSnapshot.child("Bio").getValue().toString());
                                            feedClass.setPreference(dataSnapshot.child("Preference").getValue().toString());
                                            feedClass.setAge(dataSnapshot.child("Age").getValue().toString());
                                            feedClass.setVerified(dataSnapshot.child("Verified").getValue().toString());
                                            feedClass.setShowAge(dataSnapshot.child("ShowAge").getValue().toString());
                                            if(dataSnapshot.child("DisplayPicture").exists()){
                                                for(DataSnapshot p:dataSnapshot.child("DisplayPicture").getChildren()){
                                                    feedClass.setDisplayPicture(p.getValue().toString());
                                                }
                                            }
                                            if(currentSize-1<nextLimit){
                                                if(!feedUsers.contains(feedClass)){
                                                    try {
                                                        if(feedClass.getUserId().equals(feedUsers.get(forDupCounter).getUserId())){
                                                            //
                                                        }
                                                    }
                                                    catch (IndexOutOfBoundsException e){
                                                        feedUsers.add(feedClass);
                                                        currentSize++;
                                                    }
                                                    forDupCounter++;
                                                }

                                            }
                                        }

                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                            CountDownTimer anotherCounter = new CountDownTimer(1500,1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {

                                }

                                @Override
                                public void onFinish() {
                                    listener.onSuccess(feedUsers);
                                }
                            };
                            anotherCounter.start();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
